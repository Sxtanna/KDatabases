package com.sxtanna.db.struct.statement

import com.sxtanna.db.struct.Table
import com.sxtanna.db.type.Executed
import com.sxtanna.db.type.Targeted

/**
 * Describes an SQL "DELETE" statement
 */
interface Delete<T : Any> : Executed, Targeted<Delete<T>, T>

/**
 * An object that can delete rows from a table
 */
interface DBDeleter {

    /**
     * Create a Delete statement for this table
     *  * Execute statement either by calling [Delete.execute]
     *  * or
     *  * Returning it from a Kuery#invoke block
     */
    fun <T : Any> delete(table: Table<T>): Delete<T>

    /**
     * Delete the supplied rows from this table
     *  * Executed automatically
     */
    fun <T : Any> delete(table: Table<T>, vararg rows: T) {
        delete(table, rows.toList())
    }

    /**
     * Delete the supplied rows from this table
     *  * Executed automatically
     */
    fun <T : Any> delete(table: Table<T>, rows: Collection<T>)

    /**
     * Delete all rows from this table
     *  * Executed automatically
     *  * Can be undone
     *  * **Consider using [DBTruncater.truncate] instead**
     *
     * @see [DBTruncater.truncate]
     */
    fun <T : Any> deleteAllRows(table: Table<T>)


    /**
     * An object that can delete rows from its table
     */
    interface TableDeleter<T : Any> {

        /**
         * @see [DBDeleter.delete]
         */
        fun delete(): Delete<T>

        /**
         * Deleter.delete(table : Table&lt;T>, varargs rows : T)
         *
         * @sample [Cannot_link_to_specific_method][delete]
         */
        fun delete(vararg rows: T) {
            delete(rows.toList())
        }

        /**
         * Deleter.delete(table : Table&lt;T>, rows : Collection&lt;T>)
         *
         * @sample [Cannot_link_to_specific_method][delete]
         */
        fun delete(rows: Collection<T>)

        /**
         * @see [DBDeleter.deleteAllRows]
         * @see [DBTruncater.truncate]
         */
        fun deleteAllRows()

    }

}