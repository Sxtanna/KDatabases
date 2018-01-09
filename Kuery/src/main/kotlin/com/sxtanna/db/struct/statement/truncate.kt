package com.sxtanna.db.struct.statement

import com.sxtanna.db.struct.Table

/**
 * An object that can truncate (delete all data) tables
 */
interface DBTruncater {

    /**
     * Truncate the data in this table
     */
    fun <T : Any> truncate(table : Table<T>)


    /**
     * An object that can truncate (delete all data) from its table
     */
    interface TableTruncater<T : Any> {

        /**
         * @see [DBTruncater.truncate]
         */
        fun truncate()

    }

}