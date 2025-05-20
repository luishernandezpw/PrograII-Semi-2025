package com.ugb.miprimeraaplicacion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class chats extends Activity {
    ImageView img;
    TextView tempVal;
    String to="", from="", user="", msg="", urlFoto="", urlCompletaFotoFirestore="";
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
        mostrarFoto();
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
}
