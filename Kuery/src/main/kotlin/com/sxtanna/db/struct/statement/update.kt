package com.sxtanna.db.struct.statement

import com.sxtanna.db.ext.Value
import com.sxtanna.db.struct.Table
import com.sxtanna.db.type.Executed
import com.sxtanna.db.type.Targeted
import kotlin.reflect.KProperty1

/**
 * Describes an SQL "UPDATE" statement
 */
interface Update<E : Any> : Executed, Targeted<Update<E>, E> {

    /**
     * Set this column to this value
     */
    fun <R : Any?> set(column : KProperty1<E, R>, value : R) : Update<E>

}


/**
 * An object that can update rows and columns in a table
 */
interface Updater {

    /**
     * Create an update statement for this table
     *  * Execute statement either by calling [Update.execute]
     *  * or
     *  * Returning it from a Kuery#invoke block
     */
    fun <E : Any> update(table : Table<E>) : Update<E>

    /**
     * Update the supplied rows in this table
     *  * Executed automatically
     */
    fun <E : Any> update(table : Table<E>, vararg rows : E) {
        update(table, rows.toList())
    }

    /**
     * Update the supplied rows in this table
     *  * Executed automatically
     */
    fun <E : Any> update(table : Table<E>, rows : Collection<E>)

    /**
     * Create an update statement that updates the supplied columns in this table
     *  * Execute statement either by calling [Update.execute]
     *  * or
     *  * Returning it from a Kuery#invoke block
     */
    fun <E : Any> update(table : Table<E>, vararg values : Value<E, *>) : Update<E> {
        val update = update(table)
        values.forEach { update.set(it.prop, it.value) }

        return update
    }

    /**
     * Update all rows in this table to this row
     *  * Executed automatically
     *  * Does not work on tables with primary keys
     */
    fun <E : Any> updateAllRows(table : Table<E>, row : E)

    /**
     * Update all rows in this table to these values
     *  * Executed automatically
     *  * Cannot set the value of a primary key
     */
    fun <E : Any> updateAllRows(table : Table<E>, vararg values : Value<E, *>)


    /**
     * An object that can update rows and columns in its table
     */
    interface TableUpdater<E : Any> {

        /**
         * @see [Updater.update]
         */
        fun update() : Update<E>

        /**
         * Updater.update(table : Table&lt;E>, vararg rows : E)
         *
         * @sample [Cannot_link_to_specific_method][update]
         */
        fun update(vararg rows : E) {
            update(rows.toList())
        }

        /**
         * Updater.update(table : Table<E>, rows : Collection<E>)
         *
         * @sample [Cannot_link_to_specific_method][update]
         */
        fun update(rows : Collection<E>)

        /**
         * Updater.update(table : Table&lt;E>, vararg values : Value&lt;E, *>)
         *
         * @sample [Cannot_link_to_specific_method][update]
         */
        fun update(vararg values : Value<E, *>) : Update<E>

        /**
         * @see [Updater.updateAllRows]
         */
        fun updateAllRows(row : E)

        /**
         * Updater.updateAllRows(table : Table&lt;E>, vararg values : Value&lt;E, *>)
         *
         * @sample [Cannot_link_to_specific_method][updateAllRows]
         */
        fun updateAllRows(vararg values : Value<E, *>)

    }

}