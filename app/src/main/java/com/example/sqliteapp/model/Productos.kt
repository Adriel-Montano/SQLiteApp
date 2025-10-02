package com.example.sqliteapp.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.sqliteapp.db.HelperDB

class Productos(context: Context?) {
    private var db: SQLiteDatabase? = HelperDB(context).writableDatabase

    companion object {
        const val TABLE_NAME_PRODUCTOS = "productos"
        const val COL_ID = "idproductos"
        const val COL_IDCATEGORIA = "idcategoria"
        const val COL_DESCRIPCION = "descripcion"
        const val COL_PRECIO = "precio"
        const val COL_CANTIDAD = "cantidad"

        const val CREATE_TABLE_PRODUCTOS = (
                "CREATE TABLE IF NOT EXISTS $TABLE_NAME_PRODUCTOS (" +
                        "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COL_IDCATEGORIA INTEGER NOT NULL," +
                        "$COL_DESCRIPCION VARCHAR(150) NOT NULL," +
                        "$COL_PRECIO DECIMAL(10,2) NOT NULL," +
                        "$COL_CANTIDAD INTEGER," +
                        "FOREIGN KEY($COL_IDCATEGORIA) REFERENCES categoria(idcategoria)" +
                        ");"
                )
    }

    private fun cv(idcategoria: Int?, descripcion: String?, precio: Double?, cantidad: Int?): ContentValues =
        ContentValues().apply {
            put(COL_IDCATEGORIA, idcategoria)
            put(COL_DESCRIPCION, descripcion)
            put(COL_PRECIO, precio)
            put(COL_CANTIDAD, cantidad)
        }

    fun addNewProducto(idcategoria: Int?, descripcion: String?, precio: Double?, cantidad: Int?) {
        db?.insert(TABLE_NAME_PRODUCTOS, null, cv(idcategoria, descripcion, precio, cantidad))
    }

    fun deleteProducto(id: Int) {
        db?.delete(TABLE_NAME_PRODUCTOS, "$COL_ID=?", arrayOf(id.toString()))
    }

    fun updateProducto(id: Int, idcategoria: Int?, descripcion: String?, precio: Double?, cantidad: Int?) {
        db?.update(TABLE_NAME_PRODUCTOS, cv(idcategoria, descripcion, precio, cantidad), "$COL_ID=?", arrayOf(id.toString()))
    }

    fun searchProducto(id: Int): Cursor? =
        db?.query(TABLE_NAME_PRODUCTOS,
            arrayOf(COL_ID, COL_IDCATEGORIA, COL_DESCRIPCION, COL_PRECIO, COL_CANTIDAD),
            "$COL_ID=?", arrayOf(id.toString()), null, null, null)

    fun searchProductosAll(): Cursor? =
        db?.query(TABLE_NAME_PRODUCTOS,
            arrayOf(COL_ID, COL_IDCATEGORIA, COL_DESCRIPCION, COL_PRECIO, COL_CANTIDAD),
            null, null, null, null, "$COL_DESCRIPCION ASC")
}
