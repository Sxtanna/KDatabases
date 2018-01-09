package com.sxtanna.db.type

/**
 * Represents a statement that needs to be executed
 */
interface Executed {

    /**
     * Execute this statement
     */
    fun execute()

}