package com.ugb.miprimeraaplicacion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "amigos";
    private static final int DATABASE_VERSION = 1;
    private static final String SQLdb = "CREATE TABLE amigos (idAmigo INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, direccion TEXT, telefono TEXT, email TEXT, dui TEXT, urlFoto TEXT)";
    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLdb);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Actualizar la estrucutra de la base de datos si es necesario
    }
    public String administrar_amigos(String accion, String[] datos) {
        try{
            SQLiteDatabase db = getWritableDatabase();
            String mensaje = "ok", sql = "";
            switch (accion) {
                case "agregar":
                    sql = "INSERT INTO amigos (nombre, direccion, telefono, email, dui, urlFoto) VALUES ('"+ datos[1] +"', '" + datos[2] + "', '" + datos[3] + "', '" + datos[4] + "', '" + datos[5] + "', '" + datos[6] + "')";
                    break;
                case "modificar":
                    sql = "UPDATE amigos SET nombre = '" + datos[1] + "', direccion = '" + datos[2] + "', telefono = '" + datos[3] + "', email = '" + datos[4] + "', dui = '" + datos[5] + "', urlFoto = '" + datos[6] + "' WHERE idAmigo = " + datos[0];
                    break;
                case "eliminar":
                    sql = "DELETE FROM amigos WHERE idAmigo = " + datos[0];
                    break;
            }
            db.execSQL(sql);
            db.close();
            return mensaje;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    public Cursor lista_amigos() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM amigos", null);
    }
}
