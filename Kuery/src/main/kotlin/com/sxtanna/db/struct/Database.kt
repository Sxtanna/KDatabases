package com.sxtanna.db.struct

import com.sxtanna.db.type.Named

abstract class Database : Named {

    override val name : String
        get() = this::class.simpleName ?: throw UnsupportedOperationException("Class ${this::class} cannot be a database")

    @PublishedApi
    internal val tables = mutableListOf<Table<*>>()


    inline fun <reified T : Any> table() : Table<T> {
        return Table.of<T>().also { tables.add(it) }
    }

}