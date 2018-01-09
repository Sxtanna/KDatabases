package com.sxtanna.db.struct.statement

import com.sxtanna.db.struct.Database
import com.sxtanna.db.struct.Table

/**
 * An object that can drop (delete) a table
 */
interface DBDropper {

    fun drop(database : Database)

    /**
     * Drop this table
     */
    fun <T : Any> drop(table : Table<T>)


    /**
     * An object that can drop (delete) its table
     */
    interface TableDropper<T : Any> {

        /**
         * @see [DBDropper.drop]
         */
        fun drop()

    }

}