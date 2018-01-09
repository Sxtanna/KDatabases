package com.sxtanna.db.type

interface DatabaseBridge<out C : AutoCloseable> {

    /**
     * Load the database
     */
    fun load()

    /**
     * Unload the database
     */
    fun unload()


    /**
     * Pull a connection from the database
     */
    fun connect() : C

    /**
     * Use a connection from the database
     *  * Closes automatically after code block
     */
    fun <R> connect(block : (C) -> R) = connect().use(block)

}