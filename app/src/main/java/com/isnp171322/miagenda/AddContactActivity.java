package com.isnp171322.miagenda;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.isnp171322.miagenda.Clases.ConexionSQLite;

public class AddContactActivity extends AppCompatActivity {
    ConexionSQLite objConexion;
    final String NOMBRE_BASE_DATOS = "miagenda";
    EditText nombre, telefono;
    Button botonAgregar, botonRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        objConexion = new ConexionSQLite(AddContactActivity.this, NOMBRE_BASE_DATOS, null, 2);

        nombre = findViewById(R.id.txtEditName);
        telefono = findViewById(R.id.txtEditPhone);
        botonAgregar = findViewById(R.id.btnGuardarCambios);
        botonRegresar = findViewById(R.id.btnRegresar);

        // Evento (CLICK)
        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Registrar
                registrar();
            }
        });

        // Evento (CLICK)
        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Regresar
                regresar();
            }
        });
    }

    private void regresar() {
        Intent actividad = new Intent(AddContactActivity.this, MainActivity.class);
        startActivity(actividad);
        AddContactActivity.this.finish();
    }

    private void registrar() {
        // Validación de campos vacíos

        if (nombre.getText().toString().isEmpty() || telefono.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa todos los datos", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            SQLiteDatabase miBaseDatos = objConexion.getWritableDatabase();

            // Uso de ContentValues para evitar inyecciones SQL y mejorar la seguridad
            ContentValues valores = new ContentValues();
            valores.put("nombre", nombre.getText().toString());
            valores.put("telefono", telefono.getText().toString());

            // Inserta los datos en la tabla 'contactos'
            miBaseDatos.insert("contactos", null, valores);
            miBaseDatos.close();

            // Muestra un mensaje de éxito
            Toast.makeText(AddContactActivity.this, "Datos registrados con éxito", Toast.LENGTH_LONG).show();

            // Limpia las cajas de texto para ingresar un nuevo contacto o preciona el boton regresar
            nombre.setText("");
            telefono.setText("");
        } catch (Exception error) {
            Toast.makeText(AddContactActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}