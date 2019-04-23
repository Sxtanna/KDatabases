package com.sxtanna.db.ext

import com.sxtanna.db.struct.SqlType
import com.sxtanna.db.struct.SqlType.SqlEnum
import com.sxtanna.db.struct.SqlType.SqlSet
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.PROPERTY
import kotlin.reflect.KClass

/**
 * Mark this field as the primary key of the table
 *
 * **Applicable on every type**
 */
@Retention(RUNTIME)
@Target(PROPERTY, FIELD)
annotation class PrimaryKey

/**
 * Specify that this number should be Unsigned
 *
 * **Applicable on [Byte], [Short], [Int], [Long], [BigInteger], [Float], [Double], [BigDecimal]**
 */
@Retention(RUNTIME)
@Target(PROPERTY, FIELD)
annotation class Unsigned

/**
 * Specify the size of this field, be int max digits, or string length
 *
 * **Applicable on [Byte], [Short], [Int], [Long], [BigInteger], [Float], [Double], and [String]**
 */
@Retention(RUNTIME)
@Target(PROPERTY, FIELD)
annotation class Size(val length: Int, val places: Int = 0)

/**
 * Use the type [SqlType.SqlChar] instead of [SqlType.SqlVarChar], and use this length
 *
 * **Applicable on [String]**
 *
 * **Max Length is 255**
 */
@Retention(RUNTIME)
@Target(PROPERTY, FIELD)
annotation class Fixed(val length: Int)

/**
 * Marks this column as a "Tiny" variant of its type
 *
 * **Applicable on [Int], [String], and unresolvable types**
 */
@Retention(RUNTIME)
@Target(PROPERTY, FIELD)
annotation class Tiny

/**
 * Marks this column as a "Small" variant of its type
 *
 * **Applicable on [Int], [String], and unresolvable types**
 */
@Retention(RUNTIME)
@Target(PROPERTY, FIELD)
annotation class Small

/**
 * Marks this column as a "Medium" variant of its type
 *
 * **Applicable on [Int], [String], and unresolvable types**
 */
@Retention(RUNTIME)
@Target(PROPERTY, FIELD)
annotation class Medium

/**
 * Marks this column as a "Big" variant of its type
 *
 * **Applicable on [Int], [String], and unresolvable types**
 */
@Retention(RUNTIME)
@Target(PROPERTY, FIELD)
annotation class Big

/**
 * Define the options available for [SqlSet]
 */
@Retention(RUNTIME)
@Target(PROPERTY, FIELD)
annotation class SetTypes(vararg val types: String)

/**
 * Define the enum this [SqlEnum] uses
 */
@Retention(RUNTIME)
@Target(PROPERTY, FIELD)
annotation class EnumType(val clazz: KClass<out Enum<*>>)