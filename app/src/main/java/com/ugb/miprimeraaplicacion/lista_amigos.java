package com.ugb.miprimeraaplicacion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class lista_amigos extends Activity {
    Bundle parametros = new Bundle();
    ListView ltsAmigos;
    Cursor cAmigos;
    DB db;
    final ArrayList<amigos> alAmigos = new ArrayList<amigos>();
    final ArrayList<amigos> alAmigosCopia = new ArrayList<amigos>();
    JSONArray jsonArray = new JSONArray();
    JSONObject jsonObject;
    amigos misAmigos;
    FloatingActionButton fab;
    int posicion = 0;
    DatabaseReference databaseReference;
    String miToken = "";
    detectarInternet di;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_amigos);

        ltsAmigos = findViewById(R.id.ltsAmigos);
        parametros.putString("accion", "nuevo");

        fab = findViewById(R.id.fabAgregarAmigo);
        fab.setOnClickListener(view -> abriVentana());
        listarDatos();
        buscarAmigos();
        mostrarChats();
    }
    private void mostrarChats(){
        ltsAmigos.setOnItemClickListener( (parent, view, position, id)->{
            try{
                Bundle parametros = new Bundle();
                parametros.putString("nombre", jsonArray.getJSONObject(position).getString("nombre"));
                parametros.putString("to", jsonArray.getJSONObject(position).getString("to"));
                parametros.putString("from", jsonArray.getJSONObject(position).getString("from"));
                parametros.putString("urlFoto", jsonArray.getJSONObject(position).getString("urlFoto"));
                parametros.putString("urlCompletaFotoFirestore", jsonArray.getJSONObject(position).getString("urlCompletaFotoFirestore"));

                Intent intent = new Intent(getApplicationContext(), chats.class);
                intent.putExtras(parametros);
                startActivity(intent);
            }catch (Exception e){
                mostrarMsg("Error al abrir el chat: " + e.getMessage());
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            posicion = info.position;
            menu.setHeaderTitle(jsonArray.getJSONObject(posicion).getJSONObject("value").getString("nombre"));
        } catch (Exception e) {
            mostrarMsg("Error crear el menu: " + e.getMessage());
        }
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try{
            if( item.getItemId()==R.id.mnxNuevo){
                abriVentana();
            }else if( item.getItemId()==R.id.mnxModificar){
                parametros.putString("accion", "modificar");
                parametros.putString("amigos", jsonArray.getJSONObject(posicion).getJSONObject("value").toString());
                abriVentana();
            } else if (item.getItemId()==R.id.mnxEliminar) {
                eliminarAmigo();
            }
            return true;
        }catch (Exception e){
            mostrarMsg("Error al mostrar el menu: " + e.getMessage());
            return super.onContextItemSelected(item);
        }
    }
    private void eliminarAmigo(){
        try{
            String nombre = jsonArray.getJSONObject(posicion).getJSONObject("value").getString("nombre");
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
            confirmacion.setTitle("Esta seguro de eliminar a: ");
            confirmacion.setMessage(nombre);
            confirmacion.setPositiveButton("Si", (dialog, which) -> {
                try {
                    di = new detectarInternet(this);
                    if(di.hayConexionInternet()){//online
                        JSONObject datosAmigos = new JSONObject();
                        String _id = jsonArray.getJSONObject(posicion).getJSONObject("value").getString("_id");
                        String _rev = jsonArray.getJSONObject(posicion).getJSONObject("value").getString("_rev");
                        String url = utilidades.url_mto + "/" + _id + "?rev=" + _rev;
                        enviarDatosServidor objEnviarDatosServidor = new enviarDatosServidor(this);
                        String respuesta = objEnviarDatosServidor.execute(datosAmigos.toString(), "DELETE", url).get();
                        JSONObject respuestaJSON = new JSONObject(respuesta);
                        if(!respuestaJSON.getBoolean("ok")) {
                            mostrarMsg("Error al intentar eliminar: " + respuesta);
                        }
                    }
                    String respuesta = db.administrar_amigos("eliminar", new String[]{jsonArray.getJSONObject(posicion).getJSONObject("value").getString("idAmigo")});
                    if(respuesta.equals("ok")) {
                        listarDatos();
                        mostrarMsg("Registro eliminado con exito");
                    }else{
                        mostrarMsg("Error al eliminar: " + respuesta);
                    }
                }catch (Exception e){
                    mostrarMsg("Error en eliminar: " + e.getMessage());
                }
            });
            confirmacion.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            });
            confirmacion.create().show();
        }catch (Exception e){
            mostrarMsg("Error eliminar: " + e.getMessage());
        }
    }
    private void abriVentana(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(parametros);
        startActivity(intent);
    }
    private void listarDatos(){
        try{
            databaseReference  = FirebaseDatabase.getInstance().getReference("amigos");
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tarea->{
                if(!tarea.isSuccessful()){
                    mostrarMsg("Error al obtener token: "+tarea.getException().getMessage());
                    return;
                }else{
                    miToken = tarea.getResult();
                    if( miToken!=null && miToken.length()>0 ){
                        databaseReference.orderByChild("token").equalTo(miToken).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try{
                                    if( snapshot.getChildrenCount()<=0 ){
                                        mostrarMsg("No hay amigos registrados.");
                                        parametros.putString("accion", "nuevo");
                                        abriVentana();
                                    }
                                }catch (Exception e){
                                    mostrarMsg("Error al llamar la ventana: " + e.getMessage());
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                mostrarMsg("Error se cancelo: " + error.getMessage());
                            }
                        });
                    }
                }
            });
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        for( DataSnapshot dataSnapshot : snapshot.getChildren() ){
                            amigos amigo = dataSnapshot.getValue(amigos.class);

                            jsonObject = new JSONObject();
                            jsonObject.put("idAmigo", amigo.getIdAmigo());
                            jsonObject.put("nombre", amigo.getNombre());
                            jsonObject.put("direccion", amigo.getDireccion());
                            jsonObject.put("telefono", amigo.getTelefono());
                            jsonObject.put("email", amigo.getEmail());
                            jsonObject.put("dui", amigo.getDui());
                            jsonObject.put("urlFoto", amigo.getFoto());
                            jsonObject.put("urlCompletaFotoFirestore", amigo.getUrlCompletaFotoFirestore());
                            jsonObject.put("to", amigo.getToken());
                            jsonObject.put("from", miToken);
                            jsonArray.put(jsonObject);
                        }
                        mostrarDatosAmigos();
                    }catch (Exception e){
                        mostrarMsg("Error al escuchar evento de firebase: " + e.getMessage());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){
            mostrarMsg("Error al listar datos: " + e.getMessage());
        }
    }
    private void mostrarDatosAmigos(){
        try{
            if(jsonArray.length()>0){
                alAmigos.clear();
                alAmigosCopia.clear();

                for (int i=0; i<jsonArray.length(); i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    misAmigos = new amigos(
                            jsonObject.getString("idAmigo"),
                            jsonObject.getString("nombre"),
                            jsonObject.getString("direccion"),
                            jsonObject.getString("telefono"),
                            jsonObject.getString("email"),
                            jsonObject.getString("dui"),
                            jsonObject.getString("urlFoto"),
                            jsonObject.getString("urlCompletaFotoFirestore"),
                            jsonObject.getString("to")
                    );
                    alAmigos.add(misAmigos);
                }
                alAmigosCopia.addAll(alAmigos);
                ltsAmigos.setAdapter(new AdaptadorAmigos(this, alAmigos));
                registerForContextMenu(ltsAmigos);
            }else{
                mostrarMsg("No hay amigos registrados.");
                abriVentana();
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar: " + e.getMessage());
        }
    }
    private void buscarAmigos(){
        TextView tempVal = findViewById(R.id.txtBuscarAmigos);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alAmigos.clear();
                String buscar = tempVal.getText().toString().trim().toLowerCase();
                if( buscar.length()<=0){
                    alAmigos.addAll(alAmigosCopia);
                }else{
                    for (amigos item: alAmigosCopia){
                        if(item.getNombre().toLowerCase().contains(buscar) ||
                                item.getDui().toLowerCase().contains(buscar) ||
                                item.getEmail().toLowerCase().contains(buscar)){
                            alAmigos.add(item);
                        }
                    }
                    ltsAmigos.setAdapter(new AdaptadorAmigos(getApplicationContext(), alAmigos));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}