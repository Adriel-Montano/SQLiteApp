package com.example.sqliteapp.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.sqliteapp.db.HelperDB

class Categoria(context: Context?) {
    private var db: SQLiteDatabase? = HelperDB(context).writableDatabase

    companion object {
        const val TABLE_NAME_CATEGORIA = "categoria"
        const val COL_ID = "idcategoria"
        const val COL_NOMBRE = "nombre"

        const val CREATE_TABLE_CATEGORIA = (
                "CREATE TABLE IF NOT EXISTS $TABLE_NAME_CATEGORIA (" +
                        "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COL_NOMBRE VARCHAR(50) NOT NULL);"
                )
    }

    private fun cv(nombre: String?): ContentValues = ContentValues().apply {
        put(COL_NOMBRE, nombre)
    }

    fun insertValuesDefault() {
        val categories = arrayOf("Abarrotes","Carnes","Embutidos","Mariscos","Pescado",
            "Bebidas","Verduras","Frutas","Bebidas Carbonatadas","Bebidas no carbonatadas")

        val cursor: Cursor? = db?.query(TABLE_NAME_CATEGORIA, arrayOf(COL_ID), null, null, null, null, null)

        if (cursor == null || cursor.count <= 0) {
            categories.forEach { db?.insert(TABLE_NAME_CATEGORIA, null, cv(it)) }
        }
        cursor?.close()
    }

    fun showAllCategoria(): Cursor? =
        db?.query(TABLE_NAME_CATEGORIA, arrayOf(COL_ID, COL_NOMBRE), null, null, null, null, "$COL_NOMBRE ASC")

    fun searchID(nombre: String): Int? {
        val cursor = db?.query(TABLE_NAME_CATEGORIA, arrayOf(COL_ID), "$COL_NOMBRE=?", arrayOf(nombre), null, null, null)
        cursor?.moveToFirst()
        val id = if (cursor != null && cursor.count > 0) cursor.getInt(0) else null
        cursor?.close()
        return id
    }

    fun searchNombre(id: Int): String? {
        val cursor = db?.query(TABLE_NAME_CATEGORIA, arrayOf(COL_NOMBRE), "$COL_ID=?", arrayOf(id.toString()), null, null, null)
        cursor?.moveToFirst()
        val nombre = if (cursor != null && cursor.count > 0) cursor.getString(0) else null
        cursor?.close()
        return nombre
    }
}

