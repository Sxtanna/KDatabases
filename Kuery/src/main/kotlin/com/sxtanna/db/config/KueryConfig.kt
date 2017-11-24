package com.sxtanna.db.config

/**
 * Contains the various configuration options available
 */
class KueryConfig(val data : OptionsData, val pool : OptionsPool, val user : OptionsUser) {
    internal constructor() : this(OptionsData(), OptionsPool(), OptionsUser())


    /**
     * Holds the data used to connect to the database
     *  * IP Address
     *  * Port
     *  * Database name
     */
    data class OptionsData(val address : String, val port : Short, val database : String) {
        internal constructor() : this("", 3306, "")
    }

    /**
     * Holds the data used to configure the pool
     *  * Custom Pool name
     *  * Pooled connections size
     *  * Idle Connection timeout
     *  * Connecting Connection timeout
     */
    data class OptionsPool(val name : String, val size : Int, val idleTimeout : Long, val connTimeout : Long) {
        internal constructor() : this("KueryPool", 10, 10000L, 1000L)
    }

    /**
     * Holds the data used to authenticate on the server
     *  * Username
     *  * Password
     */
    data class OptionsUser(val name : String, val auth : String) {
        internal constructor() : this("", "")
    }


    companion object {

        val DEFAULT = KueryConfig()

    }

}