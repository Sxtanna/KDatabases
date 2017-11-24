package com.sxtanna.db.struct.statement

import com.sxtanna.db.struct.Table
import com.sxtanna.db.struct.base.Duplicate

/**
 * An object that can insert rows into a table
 */
interface Inserter {

    /**
     * Insert these rows into this table
     *  * Executes automatically
     */
    fun <E : Any> insert(table : Table<E>, vararg rows : E) {
        insert(table, rows.toList())
    }

    /**
     * Insert these rows into this table
     *  * Executes automatically
     */
    fun <E : Any> insert(table : Table<E>, rows : Collection<E>)

    /**
     * Insert these rows into this table, using this duplicate key flag
     *  * Executes automatically
     */
    fun <E : Any> insert(table : Table<E>, duplicate : Duplicate<Table<E>>, vararg rows : E) {
        insert(table, duplicate, rows.toList())
    }

    /**
     * Insert these rows into this table, using this duplicate key flag
     *  * Executes automatically
     */
    fun <E : Any> insert(table : Table<E>, duplicate : Duplicate<Table<E>>, rows : Collection<E>)


    /**
     * An object that can insert rows into its table
     */
    interface TableInserter<E : Any> {

        /**
         * @see [Inserter.insert]
         */
        fun insert(vararg rows : E) {
            insert(rows.toList())
        }

        /**
         * Insert.insert(table : Table&lt;E>, rows : Collection&lt;E>)
         *
         * @sample [Cannot_link_to_specific_method][insert]
         */
        fun insert(rows : Collection<E>)

        /**
         * Insert.insert(table : Table&lt;E>, duplicate : Duplicate&lt;Table&lt;E>>, vararg rows : E)
         *
         * @sample [Cannot_link_to_specific_method][insert]
         */
        fun insert(duplicate : Duplicate<Table<E>>, vararg rows : E) {
            insert(duplicate, rows.toList())
        }

        /**
         * Insert.insert(table : Table&lt;E>, duplicate : Duplicate&lt;Table&lt;E>>, rows : Collection&lt;E>)
         *
         * @sample [Cannot_link_to_specific_method][insert]
         */
        fun insert(duplicate : Duplicate<Table<E>>, rows : Collection<E>)

    }

}