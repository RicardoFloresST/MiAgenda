package com.isnp171322.miagenda;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.isnp171322.miagenda.Clases.ConexionSQLite;

public class EditContactActivity extends AppCompatActivity {
    ConexionSQLite objConexion;
    final String NOMBRE_BASE_DATOS = "miagenda";
    EditText editNombre, editTelefono;
    Button btnGuardarCambios, botonRegresar;
    String nombreOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        // Inicializar la conexión a la base de datos
        objConexion = new ConexionSQLite(EditContactActivity.this, NOMBRE_BASE_DATOS, null, 2);

        // Asociar los elementos de la interfaz
        editNombre = findViewById(R.id.txtEditName);
        editTelefono = findViewById(R.id.txtEditPhone);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        botonRegresar = findViewById(R.id.btnRegresar);

        // Configurar el botón de regreso
        botonRegresar.setOnClickListener(view -> regresar2());

        // Obtener nombre y teléfono del contacto enviados desde MainActivity
        nombreOriginal = getIntent().getStringExtra("nombreContacto");
        String telefonoOriginal = getIntent().getStringExtra("telefonoContacto");

        // Validar que los datos no sean nulos antes de asignarlos
        if (nombreOriginal != null) {
            editNombre.setText(nombreOriginal);
        } else {
            Toast.makeText(this, "Nombre no recibido", Toast.LENGTH_SHORT).show();
        }

        if (telefonoOriginal != null) {
            editTelefono.setText(telefonoOriginal);
        } else {
            Toast.makeText(this, "Teléfono no recibido", Toast.LENGTH_SHORT).show();
        }

        // Configurar el botón para guardar los cambios
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());

    }

    private void regresar2() {
        Intent actividad = new Intent(EditContactActivity.this, MainActivity.class);
        startActivity(actividad);
        EditContactActivity.this.finish();
    }

    private void guardarCambios() {
        SQLiteDatabase baseDatos = objConexion.getWritableDatabase();

        // Crear un ContentValues para actualizar los datos
        ContentValues valores = new ContentValues();
        valores.put("nombre", editNombre.getText().toString());
        valores.put("telefono", editTelefono.getText().toString());

        // Actualizar en la base de datos usando el nombre original como referencia
        int filasAfectadas = baseDatos.update("contactos", valores, "nombre=?", new String[]{nombreOriginal});
        baseDatos.close();

        if (filasAfectadas > 0) {
            Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad y regresa a la lista
        } else {
            Toast.makeText(this, "Error al actualizar el contacto", Toast.LENGTH_SHORT).show();
        }
    }

}
