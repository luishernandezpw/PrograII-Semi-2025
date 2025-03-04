package com.ugb.miprimeraaplicacion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btn;
    TextView tempVal;
    DB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DB(this);
        btn = findViewById(R.id.btnGuardarAmigo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarAmigo();
            }
        });
    }
    private void guardarAmigo() {
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

        String[] datos = {"", nombre, direccion, telefono, email, dui, ""};
        db.administrar_amigos("agregar", datos);
        Toast.makeText(getApplicationContext(), "Registro guardado con exito.", Toast.LENGTH_LONG).show();
    }

}