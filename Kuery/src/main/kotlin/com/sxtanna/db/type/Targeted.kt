package com.sxtanna.db.type

import com.sxtanna.db.struct.base.Where
import kotlin.reflect.KProperty1

/**
 * Represents an SQL statement that has "WHERE" clauses
 */
interface Targeted<T : Targeted<T, E>, E : Any> {

    /**
     * Define the "WHERE" clauses for this statement
     */
    fun <R : Any?> where(prop : KProperty1<E, R>, block : Where<E, R>.(KProperty1<E, R>) -> Unit) : T

}