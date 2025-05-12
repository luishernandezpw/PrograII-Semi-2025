package com.ugb.miprimeraaplicacion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    Button btn;
    TextView tempVal;
    String accion = "nuevo", idAmigo = "", id="", rev="";
    ImageView img;
    String urlCompletaFoto = "", getUrlCompletaFotoFirestore = "";
    Intent tomarFotoIntent;
    detectarInternet di;
    DatabaseReference databaseReference;
    String miToken = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        obtenerToken();
        img = findViewById(R.id.imgFotoAmigo);

        btn = findViewById(R.id.btnGuardarAmigo);
        btn.setOnClickListener(view -> subirFotoFirestore());

        fab = findViewById(R.id.fabListaAmigos);
        fab.setOnClickListener(view -> abrirVentana());

        mostrarDatos();
        tomarFoto();
    }
    private void subirFotoFirestore(){
        mostrarMsg("Subiendo foto a firestore");
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(urlCompletaFoto));
        final StorageReference fileRef = reference.child("fotosAmigos/"+file.getLastPathSegment());

        final UploadTask uploadTask = fileRef.putFile(file);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                getUrlCompletaFotoFirestore = uri.toString();
                guardarAmigo();
            }).addOnFailureListener(e -> {
                mostrarMsg("Error al obtener la url de la foto: "+e.getMessage());
            });
        }).addOnFailureListener(e -> {
            mostrarMsg("Error al subir la foto: "+e.getMessage());
        });
    }
    private void obtenerToken(){
        try{
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tarea->{
                if(!tarea.isSuccessful()){
                    mostrarMsg("Error al obtener token: "+tarea.getException().getMessage());
                }else{
                    miToken = tarea.getResult();
                }
            });
        }catch (Exception e){
            mostrarMsg("Error al obtener token: "+e.getMessage());
        }
    }
    private void mostrarDatos(){
        try {
            Bundle parametros = getIntent().getExtras();
            accion = parametros.getString("accion");
            if (accion.equals("modificar")) {
                JSONObject datos = new JSONObject(parametros.getString("amigos"));
                id = datos.getString("_id");
                rev = datos.getString("_rev");
                idAmigo = datos.getString("idAmigo");

                tempVal = findViewById(R.id.txtNombre);
                tempVal.setText(datos.getString("nombre"));

                tempVal = findViewById(R.id.txtDireccion);
                tempVal.setText(datos.getString("direccion"));

                tempVal = findViewById(R.id.txtTelefono);
                tempVal.setText(datos.getString("telefono"));

                tempVal = findViewById(R.id.txtEmail);
                tempVal.setText(datos.getString("email"));

                tempVal = findViewById(R.id.txtDui);
                tempVal.setText(datos.getString("dui"));

                urlCompletaFoto = datos.getString("urlFoto");
                img.setImageURI(Uri.parse(urlCompletaFoto));
            }else {
                //idAmigo = ;
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar datos: "+e.getMessage());
        }
    }
    private void tomarFoto(){
        img.setOnClickListener(view->{
            tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File fotoAmigo = null;
            try{
                fotoAmigo = crearImagenAmigo();
                if( fotoAmigo!=null ){
                    Uri uriFotoAimgo = FileProvider.getUriForFile(MainActivity.this,
                            "com.ugb.miprimeraaplicacion.fileprovider", fotoAmigo);
                    tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoAimgo);
                    startActivityForResult(tomarFotoIntent, 1);
                }else{
                    mostrarMsg("Nose pudo crear la imagen.");
                }
            }catch (Exception e){
                mostrarMsg("Error al tomar foto: "+e.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if( requestCode==1 && resultCode==RESULT_OK ){
                img.setImageURI(Uri.parse(urlCompletaFoto));
            }else{
                mostrarMsg("No se tomo la foto.");
            }
        }catch (Exception e){
            mostrarMsg("Error al tomar la foto: "+e.getMessage());
        }
    }

    private File crearImagenAmigo() throws Exception{
        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),
                fileName = "imagen_"+ fechaHoraMs+"_";
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if( dirAlmacenamiento.exists()==false ){
            dirAlmacenamiento.mkdir();
        }
        File image = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        urlCompletaFoto = image.getAbsolutePath();
        return image;
    }
    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    private void abrirVentana(){
        Intent intent = new Intent(this, lista_amigos.class);
        startActivity(intent);
    }
    private void guardarAmigo() {
        try {
            tempVal = findViewById(R.id.txtNombre);
            String nombre = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtDireccion);
            String direccion = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtTelefono);
            String telefono = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtEmail);
            String email = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtDui);
            String dui = tempVal.getText().toString();

            databaseReference = FirebaseDatabase.getInstance().getReference("amigos");
            String key = databaseReference.push().getKey();

            if( miToken.equals("") || miToken==null ){
                obtenerToken();
            }
            amigos amigo = new amigos(idAmigo, nombre, direccion, telefono, email, dui, urlCompletaFoto, getUrlCompletaFotoFirestore, miToken);
            if( key!= null ){
                databaseReference.child(key).setValue(amigo).addOnSuccessListener(success->{
                    mostrarMsg("Registro guardado con exito.");
                    abrirVentana();
                }).addOnFailureListener(failure->{
                    mostrarMsg("Error al registrar datos: "+failure.getMessage());
                });
            } else {
                mostrarMsg("Error al guardar en firebase.");
            }
        }catch (Exception e){
            mostrarMsg("Error guardar: "+e.getMessage());
        }
    }
}