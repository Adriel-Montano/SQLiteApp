package com.example.sqliteapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sqliteapp.db.HelperDB
import com.example.sqliteapp.model.Categoria
import com.example.sqliteapp.model.Productos

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var managerCategoria: Categoria? = null
    private var managerProductos: Productos? = null
    private var dbHelper: HelperDB? = null
    private var db: SQLiteDatabase? = null
    private var cursor: Cursor? = null

    private var txtId: EditText? = null
    private var txtNombre: EditText? = null
    private var txtPrecio: EditText? = null
    private var txtCantidad: EditText? = null
    private var cmbCategorias: Spinner? = null

    private var btnAgregar: Button? = null
    private var btnActualizar: Button? = null
    private var btnEliminar: Button? = null
    private var btnBuscar: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtId = findViewById(R.id.txtId)
        txtNombre = findViewById(R.id.txtNombre)
        txtPrecio = findViewById(R.id.txtPrecio)
        txtCantidad = findViewById(R.id.txtCantidad)
        cmbCategorias = findViewById(R.id.cmbCategorias)

        btnAgregar = findViewById(R.id.btnAgregar)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnBuscar = findViewById(R.id.btnBuscar)

        dbHelper = HelperDB(this)
        db = dbHelper?.writableDatabase

        managerCategoria = Categoria(this)
        managerProductos = Productos(this)

        setSpinnerCategorias()

        btnAgregar?.setOnClickListener(this)
        btnActualizar?.setOnClickListener(this)
        btnEliminar?.setOnClickListener(this)
        btnBuscar?.setOnClickListener(this)
    }

    private fun setSpinnerCategorias() {
        managerCategoria?.insertValuesDefault()
        cursor = managerCategoria?.showAllCategoria()

        val cat = ArrayList<String>()
        cursor?.let {
            if (it.count > 0) {
                it.moveToFirst()
                do {
                    cat.add(it.getString(1))
                } while (it.moveToNext())
            }
            it.close()
        }

        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, cat)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbCategorias?.adapter = adaptador
    }

    override fun onClick(view: View) {
        val nombre = txtNombre?.text.toString().trim()
        val precio = txtPrecio?.text.toString().trim()
        val cantidad = txtCantidad?.text.toString().trim()
        val categoria = cmbCategorias?.selectedItem?.toString()?.trim() ?: ""
        val idcategoria = managerCategoria?.searchID(categoria)
        val idproducto = txtId?.text.toString().trim()

        when (view.id) {
            R.id.btnAgregar -> {
                if (vericarFormulario("insertar")) {
                    managerProductos?.addNewProducto(
                        idcategoria,
                        nombre,
                        precio.toDouble(),
                        cantidad.toInt()
                    )
                    Toast.makeText(this, "Producto agregado", Toast.LENGTH_LONG).show()
                    limpiarCampos()
                }
            }
            R.id.btnActualizar -> {
                if (vericarFormulario("actualizar")) {
                    managerProductos?.updateProducto(
                        idproducto.toInt(),
                        idcategoria,
                        nombre,
                        precio.toDouble(),
                        cantidad.toInt()
                    )
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_LONG).show()
                    limpiarCampos()
                }
            }
            R.id.btnEliminar -> {
                if (vericarFormulario("eliminar")) {
                    managerProductos?.deleteProducto(idproducto.toInt())
                    Toast.makeText(this, "Producto eliminado", Toast.LENGTH_LONG).show()
                    limpiarCampos()
                }
            }
            R.id.btnBuscar -> {
                if (vericarFormulario("buscar")) {
                    val cursor = managerProductos?.searchProducto(idproducto.toInt())
                    if (cursor != null && cursor.count > 0) {
                        cursor.moveToFirst()
                        val idCat = cursor.getInt(cursor.getColumnIndexOrThrow(Productos.COL_IDCATEGORIA))
                        val descripcion = cursor.getString(cursor.getColumnIndexOrThrow(Productos.COL_DESCRIPCION))
                        val precioVal = cursor.getDouble(cursor.getColumnIndexOrThrow(Productos.COL_PRECIO))
                        val cantidadVal = cursor.getInt(cursor.getColumnIndexOrThrow(Productos.COL_CANTIDAD))

                        txtNombre?.setText(descripcion)
                        txtPrecio?.setText(precioVal.toString())
                        txtCantidad?.setText(cantidadVal.toString())

                        val catPosition = (cmbCategorias?.adapter as? ArrayAdapter<String>)?.getPosition(
                            managerCategoria?.searchNombre(idCat)
                        )
                        if (catPosition != null && catPosition >= 0) {
                            cmbCategorias?.setSelection(catPosition)
                        }
                        Toast.makeText(this, "Producto encontrado", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    }
                    cursor?.close()
                }
            }
        }
    }

    private fun vericarFormulario(opc: String): Boolean {
        var notificacion = "Se han generado algunos errores, favor verifíquelos"
        var response = true
        var idproducto_v = true
        var nombre_v = true
        var precio_v = true
        var cantidad_v = true

        val nombre = txtNombre?.text.toString().trim()
        val precio = txtPrecio?.text.toString().trim()
        val cantidad = txtCantidad?.text.toString().trim()
        val idproducto = txtId?.text.toString().trim()

        if (opc == "insertar" || opc == "actualizar") {
            if (nombre.isEmpty()) { txtNombre?.error = "Ingrese el nombre"; nombre_v = false }
            if (precio.isEmpty()) { txtPrecio?.error = "Ingrese el precio"; precio_v = false }
            if (cantidad.isEmpty()) { txtCantidad?.error = "Ingrese la cantidad"; cantidad_v = false }

            if (opc == "actualizar") {
                if (idproducto.isEmpty()) { idproducto_v = false; notificacion = "No se ha seleccionado un producto" }
                response = !( !nombre_v || !precio_v || !cantidad_v || !idproducto_v )
            } else {
                response = !( !nombre_v || !precio_v || !cantidad_v )
            }
        } else if (opc == "eliminar" || opc == "buscar") {
            if (idproducto.isEmpty()) { response = false; notificacion = "No se ha seleccionado un producto" }
        }

        if (!response) { Toast.makeText(this, notificacion, Toast.LENGTH_LONG).show() }
        return response
    }

    private fun limpiarCampos() {
        txtId?.setText("")
        txtNombre?.setText("")
        txtPrecio?.setText("")
        txtCantidad?.setText("")
        cmbCategorias?.setSelection(0)
    }
}
