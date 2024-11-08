package com.isnp171322.miagenda;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.isnp171322.miagenda.Clases.ConexionSQLite;

public class ViewProfileActivity extends AppCompatActivity {
    ConexionSQLite objConexion;
    final String NOMBRE_BASE_DATOS = "miagenda";
    EditText viewNombre, viewTelefono;
    Button btnLlamar, botonRegresar;
    String nombreOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Inicializar la conexión a la base de datos
        objConexion = new ConexionSQLite(ViewProfileActivity.this, NOMBRE_BASE_DATOS, null, 2);

        // Asociar los elementos de la interfaz
        viewNombre = findViewById(R.id.txtViewName);
        viewTelefono = findViewById(R.id.txtViewPhone);
        btnLlamar = findViewById(R.id.btnLlamar);
        botonRegresar = findViewById(R.id.btnRegresar);

        // Hacer que los EditText sean de solo lectura
        viewNombre.setEnabled(false);
        viewTelefono.setEnabled(false);

        // Configurar el botón de regreso
        botonRegresar.setOnClickListener(view -> regresar2());

        // Obtener nombre y teléfono del contacto enviados desde MainActivity
        nombreOriginal = getIntent().getStringExtra("nombreContacto");
        String telefonoOriginal = getIntent().getStringExtra("telefonoContacto");

        // Validar que los datos no sean nulos antes de asignarlos
        if (nombreOriginal != null) {
            viewNombre.setText(nombreOriginal);
        } else {
            Toast.makeText(this, "Nombre no recibido", Toast.LENGTH_SHORT).show();
        }

        if (telefonoOriginal != null) {
            viewTelefono.setText(telefonoOriginal);
        } else {
            Toast.makeText(this, "Teléfono no recibido", Toast.LENGTH_SHORT).show();
        }

        // Configurar el botón para llamar
        btnLlamar.setOnClickListener(v -> realizarLlamada());
    }

    private void regresar2() {
        Intent actividad = new Intent(ViewProfileActivity.this, MainActivity.class);
        startActivity(actividad);
        ViewProfileActivity.this.finish();
    }

    // Método para realizar la llamada
    private void realizarLlamada() {
        String telefono = viewTelefono.getText().toString();
        if (!telefono.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + telefono));
            startActivity(intent);
        } else {
            Toast.makeText(this, "No hay número de teléfono para llamar", Toast.LENGTH_SHORT).show();
        }
    }
}
