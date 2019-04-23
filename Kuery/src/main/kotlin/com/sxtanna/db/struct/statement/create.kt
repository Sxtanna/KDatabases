package com.sxtanna.db.struct.statement

import com.sxtanna.db.struct.Database
import com.sxtanna.db.struct.Table

/**
 * An object that can create a table in a database
 */
interface DBCreator {


    fun create(database: Database, andTables: Boolean = false)

    /**
     * Create a table in the database, if it doesn't exist
     *  * Executed automatically
     */
    fun <T : Any> create(table: Table<T>)


    /**
     * An object that can create its table
     */
    interface TableCreator<T : Any> {

        /**
         * @see [DBCreator.create]
         */
        fun create()

    }

}