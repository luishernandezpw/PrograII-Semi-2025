package com.ugb.miprimeraaplicacion;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class enviarDatosServidor extends AsyncTask<String, String, String> {
    Context context;
    String respuesta = "";
    HttpURLConnection httpURLConnection;
    public enviarDatosServidor(Context context){
        this.context = context;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
    @Override
    protected String doInBackground(String... parametros) {
        String jsonResponse = "";
        String jsonDatos = parametros[0];
        String metodo = parametros[1];
        String _url = parametros[2];
        BufferedReader bufferedReader;
        try{
            URL url = new URL(_url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod(metodo);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Authorization", "Basic " + utilidades.credencialesCodificadas);
            //Enviar los datos al servidor
            Writer writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
            writer.write(jsonDatos);
            writer.close();
            //obtener/leer la respuesta del servidor
            InputStream inputStream = httpURLConnection.getInputStream();
            if( inputStream==null ) return null;
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            respuesta = bufferedReader.toString();
            //procesar la respuesta del servidor
            String linea;
            StringBuffer stringBuffer = new StringBuffer();
            while((linea = bufferedReader.readLine()) != null){
                stringBuffer.append(linea);
            }
            if(stringBuffer.length()==0) return null;
            jsonResponse = stringBuffer.toString();
        }catch (Exception e){
            return e.getMessage();
        }
        finally {
            httpURLConnection.disconnect();
        }
        return jsonResponse;
    }
}
