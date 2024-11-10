package com.isnp171322.miagenda;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.database.Cursor;
import android.widget.PopupMenu;
import java.util.ArrayList;

import com.google.firebase.FirebaseApp;
import com.isnp171322.miagenda.Clases.ConexionSQLite;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MisNotificacionesFCM";
    private final ActivityResultLauncher<String> activityResultLauncher
            = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean o) {
            if (o) {
                Toast.makeText(MainActivity.this, "Permiso de notificacion concedido", Toast.LENGTH_SHORT).show();
            }
        }
    });

    ConexionSQLite objConexion;
    final String NOMBRE_BASE_DATOS = "miagenda";
    Button botonAgregar, botonBuscar;
    EditText etBuscar;
    ListView listaContactos;
    ArrayList<String> lista;
    ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        objConexion = new ConexionSQLite(MainActivity.this, NOMBRE_BASE_DATOS, null, 2);

        botonAgregar = findViewById(R.id.btnAgregar);
        botonBuscar = findViewById(R.id.btnBuscar);
        etBuscar = findViewById(R.id.txtBuscar);
        listaContactos = findViewById(R.id.lvContactos);

        botonAgregar.setOnClickListener(view -> {
            Intent ventana = new Intent(MainActivity.this, AddContactActivity.class);
            startActivity(ventana);
            FirebaseApp.initializeApp(this);
            askNotificationPermission();
            
        });

        // Llenar lista inicial
        lista = llenarLista("", false); // Llamar con 'false' para obtener solo nombres
        adaptador = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, lista);
        listaContactos.setAdapter(adaptador);

        // Configurar botón de búsqueda
        botonBuscar.setOnClickListener(view -> {
            String textoBusqueda = etBuscar.getText().toString().trim(); // Usar trim() para eliminar espacios
            lista.clear();

            if (textoBusqueda.isEmpty()) {
                // Si el campo de búsqueda está vacío, regresar a la lista original
                lista.addAll(llenarLista("", false)); // Obtener todos los contactos
            } else {
                // Buscar en nombre y teléfono
                lista.addAll(llenarLista(textoBusqueda, true));
            }
            adaptador.notifyDataSetChanged();
        });

        // Implementar un LongClick para mostrar menú de opciones
        listaContactos.setOnItemLongClickListener((parent, view, position, id) -> {
            String contactoCompleto = lista.get(position);
            mostrarMenuOpciones(view, contactoCompleto);
            return true;
        });
    }

    private void askNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_DENIED){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            lista.clear();
            lista.addAll(llenarLista("", false)); // Llenar lista inicial solo con nombres
            adaptador.notifyDataSetChanged();
        } catch (Exception error) {
            Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Modificado para aceptar un parámetro 'buscando' que indica si se está buscando o no
    public ArrayList<String> llenarLista(String filtro, boolean buscando) {
        ArrayList<String> miLista = new ArrayList<>();
        SQLiteDatabase base = objConexion.getReadableDatabase();

        String consulta;
        String[] args;

        if (buscando) {
            // Búsqueda: selecciona nombre y teléfono
            consulta = "SELECT nombre, telefono FROM contactos WHERE nombre LIKE ? OR telefono LIKE ? ORDER BY nombre ASC";
            args = new String[]{"%" + filtro + "%", "%" + filtro + "%"}; // Buscar en ambos campos
        } else {
            // Cargar solo nombres
            consulta = "SELECT nombre FROM contactos ORDER BY nombre ASC";
            args = null; // No se aplica filtro
        }

        Cursor cadaRegistro = base.rawQuery(consulta, args);

        if (cadaRegistro.moveToFirst()) {
            do {
                if (buscando) {
                    // Para la búsqueda, añade nombre y teléfono
                    String nombreTelefono = cadaRegistro.getString(0) + " - " + cadaRegistro.getString(1);
                    miLista.add(nombreTelefono);
                } else {
                    // Para la lista inicial, solo añade el nombre
                    miLista.add(cadaRegistro.getString(0));
                }
            } while (cadaRegistro.moveToNext());
        }
        cadaRegistro.close();
        return miLista;
    }

    @SuppressLint("NonConstantResourceId")
    private void mostrarMenuOpciones(View view, String contactoCompleto) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add(0, 1, 0, "Ver Contacto");
        popupMenu.getMenu().add(0, 2, 1, "Modificar Contacto");
        popupMenu.getMenu().add(0, 3, 2, "Eliminar Contacto");

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1: // Ver Contacto
                    verContacto(contactoCompleto);
                    return true;
                case 2: // Modificar Contacto
                    modificarContacto(contactoCompleto);
                    return true;
                case 3: // Eliminar Contacto
                    confirmarEliminarContacto(contactoCompleto);
                    return true;
                default:
                    return false;
            }
        });

        popupMenu.show();
    }

    private void verContacto(String contactoCompleto) {
        // Dividir el nombre y el teléfono
        String[] partesContacto = contactoCompleto.split(" - ");
        String nombreContacto = partesContacto[0];

        // Obtener el número de teléfono desde la base de datos en caso necesario
        SQLiteDatabase baseDatos = objConexion.getReadableDatabase();
        String consulta = "SELECT telefono FROM contactos WHERE nombre=?";
        Cursor cursor = baseDatos.rawQuery(consulta, new String[]{nombreContacto});

        String telefonoContacto = "";
        if (cursor.moveToFirst()) {
            telefonoContacto = cursor.getString(0);
        }
        cursor.close();
        baseDatos.close();

        // Crear Intent para iniciar ViewPerfileActivity y pasar datos del contacto
        Intent intent = new Intent(MainActivity.this, ViewProfileActivity.class);
        intent.putExtra("nombreContacto", nombreContacto);
        intent.putExtra("telefonoContacto", telefonoContacto);
        startActivity(intent);
    }

    private void modificarContacto(String contactoCompleto) {
        String[] partesContacto = contactoCompleto.split(" - ");
        String nombreContacto = partesContacto[0];

        // Obtener el número de teléfono de la base de datos
        SQLiteDatabase baseDatos = objConexion.getReadableDatabase();
        String consulta = "SELECT telefono FROM contactos WHERE nombre=?";
        Cursor cursor = baseDatos.rawQuery(consulta, new String[]{nombreContacto});

        String telefonoContacto = "";
        if (cursor.moveToFirst()) {
            telefonoContacto = cursor.getString(0);
        }
        cursor.close();
        baseDatos.close();

        // Iniciar la actividad de edición con los datos obtenidos
        Intent modificarIntent = new Intent(MainActivity.this, EditContactActivity.class);
        modificarIntent.putExtra("nombreContacto", nombreContacto);
        modificarIntent.putExtra("telefonoContacto", telefonoContacto);
        startActivity(modificarIntent);
    }

    private void confirmarEliminarContacto(String nombreContacto) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro que desea eliminar este contacto " + nombreContacto + "?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> eliminarContacto(nombreContacto))
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void eliminarContacto(String nombreContacto) {
        SQLiteDatabase baseDatos = objConexion.getWritableDatabase();
        int filasEliminadas = baseDatos.delete("contactos", "nombre=?", new String[]{nombreContacto.split(" - ")[0]});
        baseDatos.close();

        if (filasEliminadas > 0) {
            Toast.makeText(this, "Contacto eliminado: " + nombreContacto, Toast.LENGTH_SHORT).show();
            lista.remove(nombreContacto);
            adaptador.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Error al eliminar el contacto", Toast.LENGTH_SHORT).show();
        }
    }
}
