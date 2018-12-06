package com.sxtanna.db.ext

import com.sxtanna.db.struct.Resolver
import com.sxtanna.db.struct.SqlType.Attribute
import com.sxtanna.db.struct.SqlType.Attribute.*
import com.sxtanna.db.struct.statement.*
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.internal.OnlyInputTypes
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

// I am not documenting this internal crap, NEXT!

internal fun Boolean.value(input : String?) = if (this) input ?: "" else ""

internal fun <T : Any> Boolean.value(input : T?) = if (this) input else null


//region Property attribute retrievable
internal fun KProperty1<*, *>.isNotNull() : Attribute<*>? {
    return returnType.isMarkedNullable.not().value(SqlNotNull)
}

internal fun KProperty1<*, *>.isPrimary() : Attribute<*>? {
    return findAnnotation<PrimaryKey>()?.let { SqlPrimary }
}

internal fun KProperty1<*, *>.isUnsigned() : Attribute<*>? {
    return findAnnotation<Unsigned>()?.let { SqlUnsigned }
}
//endregion


//region ResultSet whileNext implementations
/**
 * Execute this block of code for every result in the set
 */
inline fun ResultSet.whileNext(block : ResultSet.() -> Unit) {
    while (this.next()) this.block()
}

/**
 * Map each result in the set to an object and return a list of them
 */
inline fun <O> ResultSet.mapWhileNext(mapper : ResultSet.() -> O) : List<O> {
    val output = mutableListOf<O>()
    this.whileNext { output.add(this.mapper()) }

    return output
}
//endregion


//region Kuery `Value`
/**
 * Create a Column|Value association with strict typing
 */
infix fun <E, @OnlyInputTypes R> KProperty1<E, R>.value(value : R) = Value(this, value)

/**
 * Column|Value association
 */
data class Value<E, out R> internal constructor(val prop : KProperty1<E, R>, val value : R)
//endregion


//region Select `ForEach` implementations
/**
 * Performs a given action on each [R1]
 */
fun <R1 : Any> Select1<*, List<R1>>.forEach(block : (R1) -> Unit) {
    val (r1) = this
    r1.forEach(block)
}

/**
 * Performs a given action on each [R1] and [R2]
 */
fun <R1 : Any, R2 : Any> Select2<*, List<R1>, List<R2>>.forEach(block : (R1, R2) -> Unit) {
    val (r1, r2) = this
    r1.forEachIndexed { index, it -> block(it, r2[index]) }
}

/**
 * Performs a given action on each [R1], [R2], and [R3]
 */
fun <R1 : Any, R2 : Any, R3 : Any> Select3<*, List<R1>, List<R2>, List<R3>>.forEach(block : (R1, R2, R3) -> Unit) {
    val (r1, r2, r3) = this
    r1.forEachIndexed { index, it -> block(it, r2[index], r3[index]) }
}

/**
 * Performs a given action on each [R1], [R2], [R3], and [R4]
 */
fun <R1 : Any, R2 : Any, R3 : Any, R4 : Any> Select4<*, List<R1>, List<R2>, List<R3>, List<R4>>.forEach(block : (R1, R2, R3, R4) -> Unit) {
    val (r1, r2, r3, r4) = this
    r1.forEachIndexed { index, it -> block(it, r2[index], r3[index], r4[index]) }
}

/**
 * Performs a given action on each [R1], [R2], [R3], [R4], and [R5]
 */
fun <R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any> Select5<*, List<R1>, List<R2>, List<R3>, List<R4>, List<R5>>.forEach(block : (R1, R2, R3, R4, R5) -> Unit) {
    val (r1, r2, r3, r4, r5) = this
    r1.forEachIndexed { index, it -> block(it, r2[index], r3[index], r4[index], r5[index]) }
}
//endregion


operator fun PreparedStatement.set(index : Int, any : Any?) {
    setObject(index, Resolver.SqlD[any])
}