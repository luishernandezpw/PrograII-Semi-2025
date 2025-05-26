package com.ugb.miprimeraaplicacion;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class chats extends Activity {
    ImageView img;
    TextView tempVal;
    Button btn;
    String to="", from="", user="", msg="", urlFoto="", urlCompletaFotoFirestore="";
    DatabaseReference databaseReference;
    private chatsArrayAdapter chatArrayAdapter;
    ListView ltsChats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats);

        img = findViewById(R.id.imgAtras);
        img.setOnClickListener(view -> {
            abrirVentana();
        });
        tempVal = findViewById(R.id.lblToChats);
        Bundle parametros = getIntent().getExtras();
        if (parametros != null && parametros.getString("to") != null && parametros.getString("to") != "") {
            to = parametros.getString("to");
            from = parametros.getString("from");
            user = parametros.getString("nombre");
            urlFoto = parametros.getString("urlFoto");
            urlCompletaFotoFirestore = parametros.getString("urlCompletaFotoFirestore");
            tempVal.setText(user);
        }
        tempVal = findViewById(R.id.txtMsgChats);
        mostrarFoto();
        enviarMsg();
        ltsChats = findViewById(R.id.ltsChats);

        chatArrayAdapter = new chatsArrayAdapter(getApplicationContext(), R.layout.msgizquierda);
        ltsChats.setAdapter(chatArrayAdapter);
        historialMsg();
    }
    private void enviarMsg(){
        btn = findViewById(R.id.btnEnviarMsg);
        btn.setOnClickListener(v->{
            guardarMsgFirebase(tempVal.getText().toString());
            sendChatMessage(false, tempVal.getText().toString());
        });
    }
    private void guardarMsgFirebase(String msg){
        try{
            JSONObject data = new JSONObject();
            data.put("para", to);
            data.put("de", from);
            data.put("msg", msg);
            data.put("nombre", user);

            JSONObject notificacion = new JSONObject();
            notificacion.put("title", "Mensaje de "+ user);
            notificacion.put("body", data);

            JSONObject misDatos = new JSONObject();
            misDatos.put("to", to);
            misDatos.put("notificacion", notificacion);
            misDatos.put("data", data);

            //enviar msg a los servidores de google
            enviarDatos objEviar = new enviarDatos();
            objEviar.execute(misDatos.toString());

            //guardart en firebase
            chats_mensajes chatsMsg = new chats_mensajes(from, msg, to, to+"_"+from);
            String key = databaseReference.push().getKey();
            databaseReference.child(key).setValue(chatsMsg);
        }catch (Exception e){

            mostrarMsg("Error al guardar msg en firebasemmmmm: "+ e.getMessage());
        }
    }
    private void sendChatMessage(Boolean posicion, String msg){
        try{
            chatArrayAdapter.add(new chatMessage(posicion, msg));
            tempVal.setText("");
        }catch (Exception e){
            mostrarMsg("Error al posicional el msg: "+ e.getMessage());
        }
    }
    private void mostrarFoto(){
        try{
            img = findViewById(R.id.imgFotoAmigoChats);
            Glide.with(getApplicationContext()).load(urlCompletaFotoFirestore).into(img);
        }catch (Exception e){
            mostrarMsg("Error al cargar la foto"+ e.getMessage());
        }
    }
    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    private void abrirVentana(){
        Intent intent = new Intent(this, lista_amigos.class);
        startActivity(intent);
    }
    void historialMsg(){
        databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if( snapshot.getChildrenCount()>0 ){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if( (dataSnapshot.child("de").getValue().equals(from) && dataSnapshot.child("para").getValue().equals(to))
                                || (dataSnapshot.child("de").getValue().equals(to) && dataSnapshot.child("para").getValue().equals(from))) {
                            sendChatMessage(dataSnapshot.child("para").getValue().equals(from), dataSnapshot.child("msg").getValue().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private class enviarDatos extends AsyncTask<String, String, String>{
        HttpURLConnection httpURLConnection;
        @Override
        protected String doInBackground(String... parametros) {
            StringBuilder result = new StringBuilder();

            String jsonResponse = null;
            String jsonDATA = parametros[0];
            BufferedReader reader = null;

            try{
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Authorization","key=BI8BanTAjfy-2d28jD3K4x4fhXV7qVuo8I-cWQxb0q5W-35JLEeO1nV6UcaPAN8XdsrMsSJffRU_6lqFhBlrxx0");
                //establecer los encabezados y los dfatos
                Writer writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(),"UTF-8"));
                writer.write(jsonDATA);
                writer.close();
                //obtener datosen formato json
                InputStream inputStream = httpURLConnection.getInputStream();
                if(inputStream==null) return null;
                reader = new BufferedReader(new InputStreamReader(inputStream));
                //procesar la respuesta
                String inputLine;
                StringBuffer buffer = new StringBuffer();
                while((inputLine = reader.readLine()) != null){
                    buffer.append(inputLine);
                }
                if(buffer.length()==0) return null;
                jsonResponse = buffer.toString();
                return jsonResponse;
            }catch (Exception e){
                mostrarMsg("Error al enviar al server de GCM: "+ e.getMessage());
                e.printStackTrace();
                Log.d("URI: ", "Error al enviar la notificacion: "+ e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if(s!=null){
                    JSONObject jsonObject = new JSONObject(s);
                    if( jsonObject.getInt("success")<=0 ){
                        mostrarMsg("Error "+ s);
                    }
                }
            }catch (Exception e){
                mostrarMsg("Error al enviar ls notificacion: "+ e.getMessage());
            }
        }
    }
}
