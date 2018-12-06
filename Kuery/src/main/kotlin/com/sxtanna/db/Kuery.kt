package com.sxtanna.db

import com.sxtanna.db.config.KueryConfig
import com.sxtanna.db.struct.Table
import com.sxtanna.db.struct.statement.Select1
import com.sxtanna.db.type.DatabaseBridge
import com.sxtanna.db.type.Executed
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.intellij.lang.annotations.Language
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * SQL Pool wrapper
 */
class Kuery(private val config : KueryConfig, internal val logger : Logger = getLogger("Kuery-${config.pool.name}")) : DatabaseBridge<Connection> {

    private lateinit var pool : HikariDataSource

    override fun load() {
        logger.debug("Kuery is loading")
        check(::pool.isInitialized.not()) { "Kuery is already loaded" }

        val config = HikariConfig().apply {
            driverClassName = "org.mariadb.jdbc.Driver"

            jdbcUrl = "jdbc:mariadb://${config.data.address}:${config.data.port}/${config.data.database}?useSSL=false"

            // oof
            config.pool.name.takeIf { it.isNotBlank() }?.let { poolName = it }

            maximumPoolSize = config.pool.size
            idleTimeout = config.pool.idleTimeout
            connectionTimeout = config.pool.connTimeout

            username = config.user.name
            password = config.user.auth

            addDataSourceProperty("cachePrepStmts", true)
            addDataSourceProperty("prepStmtCacheSize", 250)
            addDataSourceProperty("prepStmtCacheSqlLimit", 2048)
            addDataSourceProperty("useServerPrepStmts", true)
            addDataSourceProperty("cacheCallableStmts", true)
            addDataSourceProperty("elideSetAutoCommits", true)
            addDataSourceProperty("useLocalSessionState", true)
            addDataSourceProperty("alwaysSendSetIsolation", true)
            addDataSourceProperty("cacheResultSetMetadata", true)
            addDataSourceProperty("cacheServerConfiguration", true)
        }

        pool = HikariDataSource(config)
    }

    override fun unload() {
        logger.debug("Kuery is unloading")
        check(::pool.isInitialized) { "Kuery is not loaded" }

        pool.close()
    }


    override fun connect() : Connection = pool.connection


    /**
     * Create a [KueryTask] from this [Kuery] instance
     *  * Connection does not close immediately
     */
    operator fun invoke() : KueryTask = KueryTask(this, connect())

    /**
     * Create a [KueryTaskTable] from this [Kuery] instance
     *  * Connection does not close immediately
     */
    operator fun <E : Any> invoke(table : Table<E>) = KueryTaskTable(table, invoke())


    /**
     * Execute database operations using [KueryTask]
     *  * Connection closes immediately after code block
     *
     *  @return Optional result of database operations
     */
    operator fun <R> invoke(block : KueryTask.() -> R) : R {
        return connect {
            val result = KueryTask(this, it).let(block)
            if (result is Executed) result.execute()
            if (result is Select1<*, *>) result.component1()

            result
        }
    }

    /**
     * Execute database operations using [KueryTaskTable]
     *  * Connection closes immediately after code block
     *
     *  @return Optional result of database operations
     */
    operator fun <E : Any, R> invoke(table : Table<E>, block : KueryTaskTable<E>.() -> R) : R {
        return connect {
            val result = KueryTaskTable(table, KueryTask(this, it)).let(block)
            if (result is Executed) result.execute()
            if (result is Select1<*, *>) result.component1()

            result
        }
    }


    /**
     * Execute a statement
     */
    internal fun Connection.push(@Language("MySQL") statement : String, block : PreparedStatement.() -> Unit = {}) {
        prepareStatement(statement).apply(block).execute()
    }

    /**
     * Execute a query
     */
    internal fun Connection.pull(@Language("MySQL") statement : String, block : PreparedStatement.() -> Unit = {}) : ResultSet {
        return prepareStatement(statement).apply(block).executeQuery()
    }

}