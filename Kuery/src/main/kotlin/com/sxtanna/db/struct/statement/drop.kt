package com.sxtanna.db.struct.statement

import com.sxtanna.db.struct.Table

/**
 * An object that can drop (delete) a table
 */
interface Dropper {

    /**
     * Drop this table
     */
    fun <E : Any> drop(table : Table<E>)


    /**
     * An object that can drop (delete) its table
     */
    interface TableDropper<E : Any> {

        /**
         * @see [Dropper.drop]
         */
        fun drop()

    }

}