package com.sxtanna.db.struct.base

import com.sxtanna.db.ext.PrimaryKey
import com.sxtanna.db.struct.Table
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

/**
 * Describes the action to take when a unique key is found
 *  * Only use this on rows with a Unique Key
 */
sealed class Duplicate<in T : Table<*>> {

    abstract internal operator fun invoke(table : T) : String


    /**
     * Only will function for tables with a [PrimaryKey]
     */
    object Ignore : Duplicate<Table<*>>() {

        override fun invoke(table : Table<*>) : String {
            val key = table.fields.find { it.findAnnotation<PrimaryKey>() != null }
            return "$ODKU${key?.name}=${key?.name}"
        }

    }

    /**
     * Update choice priority
     *  * Provided rows [rows]
     *  * Table's [PrimaryKey]
     *  * All rows
     */
    class Update<in T : Table<E>, E : Any>(private val rows : List<KProperty1<E, *>>) : Duplicate<T>() {
        constructor(vararg rows : KProperty1<E, *>) : this(rows.toList())


        override fun invoke(table : T) : String {
            val rows = rows.takeIf { it.isNotEmpty() }?.map { it.name } // provided
                  ?: table.fields.find { it.findAnnotation<PrimaryKey>() != null }?.let { listOf(it.name) } // primary key
                  ?: table.fields.map { it.name } // all rows

            return "$ODKU${rows.joinToString { "$it=VALUES($it)" }}"
        }

    }


    private companion object {

        private const val ODKU = "ON DUPLICATE KEY UPDATE "

    }

}