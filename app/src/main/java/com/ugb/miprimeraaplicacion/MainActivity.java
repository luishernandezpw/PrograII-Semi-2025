package com.ugb.miprimeraaplicacion;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    TextView tempVal;
    LocationManager locationManager;
    LocationListener locationListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tempVal = findViewById(R.id.lblSensorGps);
        obtenerPosicion();
    }
    void obtenerPosicion(){
        try{
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != getPackageManager().PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != getPackageManager().PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                tempVal.setText("Solicitando permisos de ubicación...");
            }
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mostrarUbicacion(location);
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    tempVal.setText("Estado del proveedor: "+ status);
                }
                @Override
                public void onProviderEnabled(String provider) {
                    tempVal.setText("Proveedor habilitado: "+ provider);
                }
                @Override
                public void onProviderDisabled(String provider) {
                    tempVal.setText("Proveedor deshabilitado: "+ provider);
                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }catch (SecurityException e){
            tempVal.setText("Error al obtener la ubicación: "+ e.getMessage());
        }
    }
    void mostrarUbicacion(Location location){
        tempVal.setText("Latitud: "+ location.getLatitude() + "\nLongitud: "+ location.getLongitude() + "\nAltitud: "+ location.getAltitude());
    }
}