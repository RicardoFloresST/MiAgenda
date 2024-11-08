package com.isnp171322.miagenda.Clases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;


public class ConexionSQLite extends SQLiteOpenHelper {
    // Sentencia SQL para crear la tabla de contactos
    final String TABLE_CONTACTOS = "CREATE TABLE contactos(" +
            "id_contactos INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "nombre TEXT, " +
            "telefono TEXT)";

    // Constructor
    public ConexionSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Ejecuta la creación de la tabla cuando se crea la base de datos
        db.execSQL(TABLE_CONTACTOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Elimina la tabla existente si hay una actualización en la versión

        db.execSQL("DROP TABLE IF EXISTS contactos");
        onCreate(db);  // Crea nuevamente la tabla actualizada
    }
}