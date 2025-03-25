package com.ugb.miprimeraaplicacion;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class obtenerDatosServidor extends AsyncTask<String, String, String> {
    HttpURLConnection httpURLConnection;
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
    @Override
    protected String doInBackground(String... parametros) {
        StringBuilder respuesta = new StringBuilder();
        try{
            URL url = new URL(utilidades.url_consulta);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Authorization", "Basic " + utilidades.credencialesCodificadas);

            InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String linea;
            while((linea = bufferedReader.readLine()) != null){
                respuesta.append(linea);
            }
        }catch (Exception e){
            return e.getMessage();
        }
        finally {
            httpURLConnection.disconnect();
        }
        return respuesta.toString();
    }
}
