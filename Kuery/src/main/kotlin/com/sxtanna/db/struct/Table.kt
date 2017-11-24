package com.sxtanna.db.struct

import com.sxtanna.db.ext.PrimaryKey
import com.sxtanna.db.type.Named
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.jvm.kotlinProperty

/**
 * Describes an SQL Table, based on a JVM Object [T]
 */
class Table<T : Any> @PublishedApi internal constructor(val clazz : KClass<T>) : Named {

    override val name = clazz.simpleName ?: clazz.jvmName

    internal val fields by lazy {
        clazz.java.declaredFields
              .filter { it.getAnnotation(Transient::class.java) == null }
              .mapNotNull { it.kotlinProperty as? KProperty1<T, *> }
    }

    internal val columns = mutableMapOf<String, SqlType.Cache>()


    init {
        fields.forEach {
            columns[it.name] = Resolver.SqlO[it]
        }
    }


    /**
     * Retrieve the Primary Key of this table, or null if none
     */
    fun getPrimaryKey() = fields.find { it.findAnnotation<PrimaryKey>() != null }


    override fun equals(other : Any?) : Boolean {
        if (this === other) return true
        if (other !is Table<*>) return false

        if (clazz != other.clazz) return false
        if (columns != other.columns) return false

        return true
    }

    override fun hashCode() : Int {
        var result = clazz.hashCode()
        result = 31 * result + columns.hashCode()
        return result
    }


    companion object {

        /**
         * Create a table from a field
         */
        inline fun <reified T : Any> of() = Table(T::class)

    }

}