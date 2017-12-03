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
     *  * Idle Connection timeout (milliseconds)
     *  * Connecting Connection timeout (milliseconds)
     */
    data class OptionsPool(val name : String, val size : Int, val connTimeout : Long, val idleTimeout : Long) {
        internal constructor() : this("KueryPool", 10, 1_000L, 10_000L)

        init {
            require(connTimeout >= 250) { "Minimum connection timeout is 250 milliseconds" }
            require(idleTimeout >= 10_000L) { "Minimum idle timeout is 10,000 milliseconds (10 seconds)" }
        }

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