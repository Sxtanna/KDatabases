package com.sxtanna.db.struct.statement

import com.sxtanna.db.struct.Table

/**
 * An object that can truncate (delete all data) tables
 */
interface Truncater {

    /**
     * Truncate the data in this table
     */
    fun <E : Any> truncate(table : Table<E>)


    /**
     * An object that can truncate (delete all data) from its table
     */
    interface TableTruncater<E : Any> {

        /**
         * @see [Truncater.truncate]
         */
        fun truncate()

    }

}