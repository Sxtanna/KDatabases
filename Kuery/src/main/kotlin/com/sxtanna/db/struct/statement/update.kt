package com.sxtanna.db.struct.statement

import com.sxtanna.db.ext.Value
import com.sxtanna.db.struct.Table
import com.sxtanna.db.type.Executed
import com.sxtanna.db.type.Targeted
import kotlin.internal.OnlyInputTypes
import kotlin.reflect.KProperty1

/**
 * Describes an SQL "UPDATE" statement
 */
interface Update<T : Any> : Executed, Targeted<Update<T>, T> {

    /**
     * Set this column to this value
     */
    fun <@OnlyInputTypes R : Any?> set(column: KProperty1<T, R>, value: R): Update<T>

}


/**
 * An object that can update rows and columns in a table
 */
interface DBUpdater {

    /**
     * Create an update statement for this table
     *  * Execute statement either by calling [Update.execute]
     *  * or
     *  * Returning it from a Kuery#invoke block
     */
    fun <T : Any> update(table: Table<T>): Update<T>

    /**
     * Update the supplied rows in this table
     *  * Executed automatically
     */
    fun <T : Any> update(table: Table<T>, vararg rows: T) {
        update(table, rows.toList())
    }

    /**
     * Update the supplied rows in this table
     *  * Executed automatically
     */
    fun <T : Any> update(table: Table<T>, rows: Collection<T>)

    /**
     * Create an update statement that updates the supplied columns in this table
     *  * Execute statement either by calling [Update.execute]
     *  * or
     *  * Returning it from a Kuery#invoke block
     */
    fun <T : Any> update(table: Table<T>, vararg values: Value<T, *>): Update<T> {
        val update = update(table)
        values.forEach { update.set(it.prop, it.value) }

        return update
    }

    /**
     * Update all rows in this table to this row
     *  * Executed automatically
     *  * Does not work on tables with primary keys
     */
    fun <T : Any> updateAllRows(table: Table<T>, row: T)

    /**
     * Update all rows in this table to these values
     *  * Executed automatically
     *  * Cannot set the value of a primary key
     */
    fun <T : Any> updateAllRows(table: Table<T>, vararg values: Value<T, *>)


    /**
     * An object that can update rows and columns in its table
     */
    interface TableUpdater<T : Any> {

        /**
         * @see [DBUpdater.update]
         */
        fun update(): Update<T>

        /**
         * Updater.update(table : Table&lt;T>, vararg rows : T)
         *
         * @sample [Cannot_link_to_specific_method][update]
         */
        fun update(vararg rows: T) {
            update(rows.toList())
        }

        /**
         * Updater.update(table : Table<T>, rows : Collection<T>)
         *
         * @sample [Cannot_link_to_specific_method][update]
         */
        fun update(rows: Collection<T>)

        /**
         * Updater.update(table : Table&lt;T>, vararg values : Value&lt;T, *>)
         *
         * @sample [Cannot_link_to_specific_method][update]
         */
        fun update(vararg values: Value<T, *>): Update<T>

        /**
         * @see [DBUpdater.updateAllRows]
         */
        fun updateAllRows(row: T)

        /**
         * Updater.updateAllRows(table : Table&lt;T>, vararg values : Value&lt;T, *>)
         *
         * @sample [Cannot_link_to_specific_method][updateAllRows]
         */
        fun updateAllRows(vararg values: Value<T, *>)

    }

}