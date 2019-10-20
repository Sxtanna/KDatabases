package com.sxtanna.db.struct.statement

import com.sxtanna.db.struct.Table
import com.sxtanna.db.struct.base.Duplicate

/**
 * An object that can insert rows into a table
 */
interface DBInserter {

    /**
     * Insert these rows into this table
     *  * Executes automatically
     */
    fun <T : Any> insert(table: Table<T>, vararg rows: T) {
        insert(table, rows.toList())
    }

    /**
     * Insert these rows into this table
     *  * Executes automatically
     */
    fun <T : Any> insert(table: Table<T>, rows: Collection<T>)

    /**
     * Insert these rows into this table, using this duplicate key flag
     *  * Executes automatically
     */
    fun <T : Any> insert(table: Table<T>, duplicate: Duplicate<Table<T>>, vararg rows: T) {
        insert(table, duplicate, rows.toList())
    }

    /**
     * Insert these rows into this table, using this duplicate key flag
     *  * Executes automatically
     */
    fun <T : Any> insert(table: Table<T>, duplicate: Duplicate<Table<T>>, rows: Collection<T>)


    /**
     * An object that can insert rows into its table
     */
    interface TableInserter<T : Any> {

        /**
         * @see [DBInserter.insert]
         */
        fun insert(vararg rows: T) {
            insert(rows.toList())
        }

        /**
         * Insert.insert(table : Table&lt;T>, rows : Collection&lt;T>)
         *
         * @sample [Cannot_link_to_specific_method][insert]
         */
        fun insert(rows: Collection<T>)

        /**
         * Insert.insert(table : Table&lt;T>, duplicate : Duplicate&lt;Table&lt;T>>, vararg rows : T)
         *
         * @sample [Cannot_link_to_specific_method][insert]
         */
        fun insert(duplicate: Duplicate<Table<T>>, vararg rows: T) {
            insert(duplicate, rows.toList())
        }

        /**
         * Insert.insert(table : Table&lt;T>, duplicate : Duplicate&lt;Table&lt;T>>, rows : Collection&lt;T>)
         *
         * @sample [Cannot_link_to_specific_method][insert]
         */
        fun insert(duplicate: Duplicate<Table<T>>, rows: Collection<T>)

    }

}