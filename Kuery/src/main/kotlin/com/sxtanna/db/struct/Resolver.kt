package com.sxtanna.db.struct

import com.sxtanna.db.ext.*
import com.sxtanna.db.struct.SqlType.*
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.ResultSet
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

/**
 * Resolve types for [SqlType] going to and from the database
 */
@Suppress("RemoveExplicitTypeArguments")
object Resolver {

    /**
     * Defines how to resolve objects from a [ResultSet]
     *
     * Has default implementations for
     *  * [Char]
     *  * [UUID]
     *  * [Boolean]
     *  * Any [Enum]
     *  * [String]
     *  * [Byte]
     *  * [Short]
     *  * [Int]
     *  * [Long]
     *  * [BigInteger]
     *  * [Float]
     *  * [Double]
     *  * [BigDecimal]
     *
     * New implementations can be added using
     * [SqlI.resolve]
     */
    object SqlI {

        @PublishedApi
        internal val adapters = mutableMapOf<KClass<*>, ResultSet.(KProperty1<*, *>) -> Any>()


        init {

            resolve<Char> {
                getString(it.name)[0]
            }

            resolve<UUID> {
                UUID.fromString(getString(it.name))
            }

            resolve<Boolean> {
                getBoolean(it.name)
            }

            resolve<Enum<*>> {
                val value = getString(it.name)
                checkNotNull((it.returnType.jvmErasure as Enum<*>).javaClass.enumConstants.find { it.name == value })
            }

            resolve<String> {
                getString(it.name)
            }

            resolve<Byte> {
                getByte(it.name)
            }

            resolve<Short> {
                getShort(it.name)
            }

            resolve<Int> {
                getInt(it.name)
            }

            resolve<Long> {
                getLong(it.name)
            }

            resolve<BigInteger> {
                BigInteger(getString(it.name))
            }

            resolve<Float> {
                getFloat(it.name)
            }

            resolve<Double> {
                getDouble(it.name)
            }

            resolve<BigDecimal> {
                getBigDecimal(it.name)
            }

        }


        internal operator fun <T : Any?> get(resultSet : ResultSet, property : KProperty1<*, T>) : T {
            val type = property.returnType.jvmErasure
            val adapter = adapters[type] ?: adapters[if (type.isSubclassOf(Enum::class)) Enum::class else Any::class]

            return checkNotNull(adapter) { "No adapter for $type" }.invoke(resultSet, property) as T
        }

        /**
         * Define how to resolve [T] from a [ResultSet]
         */
        inline fun <reified T : Any> resolve(noinline block : ResultSet.(KProperty1<*, T>) -> T) {
            adapters[T::class] = (block as ResultSet.(KProperty1<*, *>) -> Any)
        }

    }

    /**
     * Defines how to resolve [SqlType.Cache] from an object's properties
     *
     * Has default implementations for
     *  * [Char]
     *  * [UUID]
     *  * [Boolean]
     *  * Any [Enum]
     *  * [String]
     *  * [Byte]
     *  * [Short]
     *  * [Int]
     *  * [Long]
     *  * [BigInteger]
     *  * [Float]
     *  * [Double]
     *  * [BigDecimal]
     *  * If the object has no resolver, a [SqlVarChar] is used with its [Object.toString] result
     *
     * New implementations can be added using
     * [SqlO.resolve]
     */
    object SqlO {

        @PublishedApi
        internal val adapters = mutableMapOf<KClass<*>, KProperty1<*, *>.() -> SqlType.Cache>()


        init {

            resolve<Char> {
                SqlChar[1, isNotNull(), isPrimary()]
            }

            resolve<UUID> {
                SqlChar[36, isNotNull(), isPrimary()]
            }

            resolve<Boolean> {
                SqlBoolean[isNotNull(), isPrimary()]
            }

            resolve<Enum<*>> {
                val clazz = returnType.jvmErasure as KClass<out Enum<*>>
                SqlEnum[clazz, isNotNull(), isPrimary()]
            }

            resolve(Any::class, String::class) {

                val fixed = findAnnotation<Fixed>()?.length

                val type = when {
                    findAnnotation<Tiny>() != null -> SqlTinyText
                    findAnnotation<Small>() != null -> SqlText
                    findAnnotation<Medium>() != null -> SqlMediumText
                    findAnnotation<Big>() != null -> SqlLongText
                    else -> null
                }

                if (type != null) {
                    return@resolve type.get(isNotNull(), isPrimary())
                }

                val sizedType = (if (fixed != null) SqlChar else SqlVarChar)
                sizedType[(fixed ?: findAnnotation<Size>()?.length ?: 255).coerceIn(1, 255), isNotNull(), isPrimary()]
            }

            resolve<Byte> {
                val size = findAnnotation<Size>()?.length ?: 3
                SqlTinyInt[size.coerceIn(1, 3), isNotNull(), isPrimary(), isUnsigned()]
            }

            resolve<Short> {
                val size = findAnnotation<Size>()?.length ?: 5
                SqlSmallInt[size.coerceIn(1, 5), isNotNull(), isPrimary(), isUnsigned()]
            }

            resolve<Int> {

                val type = when {
                    findAnnotation<Tiny>() != null -> SqlTinyInt
                    findAnnotation<Small>() != null -> SqlSmallInt
                    findAnnotation<Medium>() != null -> SqlMediumInt
                    findAnnotation<Big>() != null -> SqlBigInt
                    else -> SqlInt
                }

                val max = when(type) {
                    SqlTinyInt -> 3
                    SqlSmallInt -> 5
                    SqlMediumInt -> 7
                    SqlBigInt -> 19
                    else -> 10
                }

                val size = findAnnotation<Size>()?.length ?: max
                type[size.coerceIn(1, max), isNotNull(), isPrimary(), isUnsigned()]
            }

            resolve(Long::class, BigInteger::class) {
                val size = findAnnotation<Size>()?.length ?: 19
                SqlBigInt[size.coerceIn(1, 19), isNotNull(), isPrimary(), isUnsigned()]
            }

            resolve<Float> {
                val size = findAnnotation<Size>()

                val length = (size?.length ?: 14)
                val places = (size?.places ?: 7).coerceAtLeast(0)

                SqlFloat[length.coerceAtLeast(places), places, isNotNull(), isPrimary(), isUnsigned()]
            }

            resolve<Double> {
                val size = findAnnotation<Size>()

                val length = (size?.length ?: 30)
                val places = (size?.places ?: 15).coerceAtLeast(0)

                SqlDouble[length.coerceAtLeast(places), places, isNotNull(), isPrimary(), isUnsigned()]
            }

            resolve<BigDecimal> {
                val size = findAnnotation<Size>()

                val length = (size?.length ?: 10).coerceIn(1, 65)
                val places = (size?.places ?:  0).coerceIn(0, 30)

                SqlDecimal[length.coerceAtLeast(places), places, isNotNull(), isPrimary(), isUnsigned()]
            }


            // start declared

            resolveWith<SqlTinyInt, Byte>()

            resolveWith<SqlSmallInt, Short>()

            resolve<SqlMediumInt> {
                val size = findAnnotation<Size>()?.length ?: 7
                SqlMediumInt[size.coerceIn(1, 7), isNotNull(), isPrimary()]
            }

            resolveWith<SqlInt, Int>()

            resolveWith<SqlBigInt, Long>()

            resolveWith<SqlFloat, Float>()

            resolveWith<SqlDouble, Double>()

            resolveWith<SqlDecimal, BigDecimal>()

            resolve<SqlChar> {
                val size = findAnnotation<Size>()
                SqlChar[size?.length ?: 1, isNotNull(), isPrimary()]
            }

            resolveWith<SqlVarChar, String>()

            resolve(SqlTinyText::class, SqlText::class, SqlMediumText::class, SqlLongText::class) {
                (returnType.jvmErasure.objectInstance as SqlType)[isNotNull(), isPrimary()]
            }

            resolveWith<SqlBoolean, Boolean>()

            resolve<SqlSet> {
                val values = requireNotNull(findAnnotation<SetTypes>()) { "You must specify the set values" }
                SqlSet[values.types, isNotNull(), isPrimary()]
            }

            resolve<SqlEnum> {
                val clazz = requireNotNull(findAnnotation<EnumType>()) { "You must specify which enum this is for" }
                SqlEnum[clazz.clazz, isNotNull(), isPrimary()]
            }

        }


        internal operator fun get(property : KProperty1<*, *>) : SqlType.Cache {
            val type = property.returnType.jvmErasure
            val adapter = adapters[type] ?: adapters[if (type.isSubclassOf(Enum::class)) Enum::class else Any::class]

            return checkNotNull(adapter) { "Impossible... but for type $type" }.invoke(property)
        }


        /**
         * Resolve type [T] with [block]
         */
        inline fun <reified T : Any> resolve(noinline block : KProperty1<*, *>.() -> SqlType.Cache) {
            adapters[T::class] = block
        }

        /**
         * Resolve many [types] using the same [block]
         */
        fun resolve(vararg types : KClass<*>, block : KProperty1<*, *>.() -> SqlType.Cache) {
            types.forEach { adapters[it] = block }
        }


        private inline fun <reified T : SqlType, reified O : Any> resolveWith() {
            adapters[T::class] = requireNotNull(adapters[O::class]) { "Oops, no adapter for ${O::class}" }
        }

    }

}