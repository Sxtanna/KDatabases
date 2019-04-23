package com.sxtanna.db.struct

import com.sxtanna.db.type.Named

abstract class Database : Named {

    override val name: String
        get() = this::class.simpleName ?: throw UnsupportedOperationException("Class ${this::class} cannot be a database")

    @PublishedApi
    internal val tables = mutableListOf<Table<*>>()


    fun <T : Any> add(table: Table<T>): Table<T> {
        tables += table
        return table
    }


    fun <T : Any> table(clazz: Class<T>): Table<T> {
        return add(Table.of(clazz))
    }

    inline fun <reified T : Any> table(): Table<T> {
        return add(Table.of())
    }

}