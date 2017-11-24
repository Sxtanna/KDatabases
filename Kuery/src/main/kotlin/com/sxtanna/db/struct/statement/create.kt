package com.sxtanna.db.struct.statement

import com.sxtanna.db.struct.Table

/**
 * An object that can create a table in a database
 */
interface Creator {

    /**
     * Create a table in the database, if it doesn't exist
     *  * Executed automatically
     */
    fun <E : Any> create(table : Table<E>)


    /**
     * An object that can create its table
     */
    interface TableCreator<E : Any> {

        /**
         * @see [Creator.create]
         */
        fun create()

    }

}