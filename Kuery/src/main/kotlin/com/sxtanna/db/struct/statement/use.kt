package com.sxtanna.db.struct.statement

import com.sxtanna.db.struct.Database

/**
 * An object that can use (switch/select) a specific [Database]
 */
interface DBUser { // spooky name lol..

    /**
     * Use this database
     */
    fun use(database : Database)

}