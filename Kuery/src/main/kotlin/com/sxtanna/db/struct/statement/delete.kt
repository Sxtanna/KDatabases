package com.sxtanna.db.struct.statement

import com.sxtanna.db.struct.Table
import com.sxtanna.db.type.Executed
import com.sxtanna.db.type.Targeted

/**
 * Describes an SQL "DELETE" statement
 */
interface Delete<E : Any> : Executed, Targeted<Delete<E>, E>

/**
 * An object that can delete rows from a table
 */
interface Deleter {

    /**
     * Create a Delete statement for this table
     *  * Execute statement either by calling [Delete.execute]
     *  * or
     *  * Returning it from a Kuery#invoke block
     */
    fun <E : Any> delete(table : Table<E>) : Delete<E>

    /**
     * Delete the supplied rows from this table
     *  * Executed automatically
     */
    fun <E : Any> delete(table : Table<E>, vararg rows : E) {
        delete(table, rows.toList())
    }

    /**
     * Delete the supplied rows from this table
     *  * Executed automatically
     */
    fun <E : Any> delete(table : Table<E>, rows : Collection<E>)

    /**
     * Delete all rows from this table
     *  * Executed automatically
     *  * Can be undone
     *  * **Consider using [Truncater.truncate] instead**
     *
     * @see [Truncater.truncate]
     */
    fun <E : Any> deleteAllRows(table : Table<E>)


    /**
     * An object that can delete rows from its table
     */
    interface TableDeleter<E : Any> {

        /**
         * @see [Deleter.delete]
         */
        fun delete() : Delete<E>

        /**
         * Deleter.delete(table : Table&lt;E>, varargs rows : E)
         *
         * @sample [Cannot_link_to_specific_method][delete]
         */
        fun delete(vararg rows : E) {
            delete(rows.toList())
        }

        /**
         * Deleter.delete(table : Table&lt;E>, rows : Collection&lt;E>)
         *
         * @sample [Cannot_link_to_specific_method][delete]
         */
        fun delete(rows : Collection<E>)

        /**
         * @see [Deleter.deleteAllRows]
         * @see [Truncater.truncate]
         */
        fun deleteAllRows()

    }

}