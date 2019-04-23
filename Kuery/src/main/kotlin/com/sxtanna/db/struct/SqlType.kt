package com.sxtanna.db.struct

import com.sxtanna.db.struct.SqlType.Attribute.*
import com.sxtanna.db.type.Named
import kotlin.reflect.KClass

/**
 * Defines the supported SQL Types of this DSL
 *  * These descriptions are 100% copy-pasted from my best friend "HeidiSQL", don't @ me if they are wrong, make an issue pls
 */
sealed class SqlType(name: String? = null) : Named {

    override val name by lazy { requireNotNull(name ?: this::class.simpleName?.substring(3)?.toUpperCase()) }


    operator fun get(vararg attributes: Attribute<*>?): Cache = Cache(attributes.filterNotNull().toMutableList())


    final override fun toString() = "Type=$name"

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SqlType) return false

        if (name != other.name) return false

        return true
    }

    final override fun hashCode(): Int {
        return name.hashCode()
    }


    /**
     * Represents cached attributes for this [SqlType] at the column its created for
     */
    open inner class Cache internal constructor(protected val attributes: MutableList<Attribute<*>>) {
        constructor(attributes: Array<out Attribute<*>?>) : this(attributes.filterNotNull().toMutableList())

        open fun name() = name

        override fun toString(): String {
            return "${name()}${if (attributes.has<SqlPrimary>()) " PRIMARY KEY" else ""}${if (attributes.has<SqlNotNull>()) " NOT NULL" else ""}"
        }


        protected inline fun <reified A : Attribute<*>> MutableList<Attribute<*>>.has() = any { it is A }

        protected inline fun <reified A : Attribute<*>> MutableList<Attribute<*>>.rem() = removeAll { it is A }

    }


    // base implementations

    /**
     * Represents an [SqlType] with a size
     */
    abstract class SizedType(name: String? = null) : SqlType(name) {

        operator fun get(size: Int, vararg attributes: Attribute<*>?): Cache = SizedCache(size, attributes)

        @JvmName("otherGet")
        operator fun get(size: Int, attributes: Array<out Attribute<*>?>): Cache = SizedCache(size, attributes)


        open inner class SizedCache internal constructor(protected val size: Int, attributes: Array<out Attribute<*>?>) : Cache(attributes) {

            override fun name() = "$name($size)"

        }

    }

    /**
     * Represents an [SqlType] that is a number
     */
    abstract class NumberType(name: String? = null) : SizedType(name) {

        open inner class NumberCache internal constructor(protected val size: Int, attributes: Array<out Attribute<*>?>) : Cache(attributes) {

            override fun name() = "${super.name()}${if (attributes.has<SqlUnsigned>()) " UNSIGNED" else ""}"

        }

    }

    /**
     * Represents an [SqlType] that is a floating point number
     */
    abstract class DecimalType(name: String? = null) : NumberType(name) {

        operator fun get(size: Int, places: Int, vararg attributes: Attribute<*>?): Cache = DecimalCache(size, places, attributes)

        @JvmName("otherGet")
        operator fun get(size: Int, places: Int, attributes: Array<out Attribute<*>?>): Cache = DecimalCache(size, places, attributes)


        inner class DecimalCache internal constructor(size: Int, private val places: Int, attributes: Array<out Attribute<*>?>) : NumberCache(size, attributes) {

            override fun name() = "$name($size, $places)${if (attributes.has<SqlUnsigned>()) " UNSIGNED" else ""}"

        }

    }

    /**
     * Represents an [SqlType] that holds a collection of some sort
     */
    abstract class CollType(name: String? = null) : SqlType(name) {

        operator fun get(values: Array<out Any>, vararg attributes: Attribute<*>?): Cache = CollCache(values, attributes)

        @JvmName("otherGet")
        operator fun get(values: Array<out Any>, attributes: Array<out Attribute<*>?>): Cache = CollCache(values, attributes)


        inner class CollCache internal constructor(private val values: Array<out Any>, attributes: Array<out Attribute<*>?>) : Cache(attributes) {

            override fun toString(): String {
                val name = name()
                return "$name(${values.joinToString { "'$it'" }})${super.toString().substringAfter(name)}"
            }

        }

    }


    // whole number types
    /**
     * Sql "TINYINT" data type, "A very small integer"
     *
     * **Range**
     *  * **Signed -128..127**
     *  * **Unsigned 0..255**
     */
    object SqlTinyInt : NumberType()

    /**
     * Sql "SMALLINT" data type, "A small integer"
     *
     * **Range**
     *  * **Signed -32,768..32,767**
     *  * **Unsigned 0..65,535**
     */
    object SqlSmallInt : NumberType()

    /**
     * Sql "MEDIUMINT" data type, "A medium-sized integer"
     *
     * **Range**
     *  * **Signed -8,388,608..8,388,607**
     *  * **Unsigned 0..16,777,215**
     */
    object SqlMediumInt : NumberType()

    /**
     * Sql "INT" data type, "A normal-sized integer"
     *
     * **Range**
     *  * **Signed -2,147,483,648..2,147,483,647**
     *  * **Unsigned 0..4,294,967,295**
     */
    object SqlInt : NumberType()

    /**
     * Sql "BIGINT" data type, "A large integer"
     *
     * **Range**
     *  * **Signed -9,223,372,036,854,775,808..9,223,372,036,854,775,807**
     *  * **Unsigned 0..18,446,744,073,709,551,615**
     */
    object SqlBigInt : NumberType()


    // floating point number types

    /**
     * Sql "FLOAT" data type, "A small (single-precision) floating-point number"
     *
     * **Range**
     *  * **Signed -3.402823466E+38..3.402823466E+38**
     *  * **Unsigned 0..3.402823466E+38**
     */
    object SqlFloat : DecimalType()

    /**
     * Sql "DOUBLE" data type, "A normal sized (double-precision) floating-point number"
     *
     * **Range**
     *  * **Signed -1.7976931348623157E+308..1.7976931348623157E+308**
     *  * **Unsigned 0..1.7976931348623157E+308**
     */
    object SqlDouble : DecimalType()

    /**
     * Sql "DECIMAL" data type, "A packed "exact" fixed-point number"
     */
    object SqlDecimal : DecimalType()


    // text types
    /**
     * Sql "CHAR" data type, "A fixed-length string"
     * * Always right padded to specified length, automatically trimmed when retrieved
     *
     * **Length Range**
     * * **0..255**
     */
    object SqlChar : SizedType()

    /**
     * Sql "VARCHAR" data type, "A variable-length string"
     * * Max length affected by character set and row size
     *
     * **Length Range**
     * * **0..65,535**
     */
    object SqlVarChar : SizedType()

    /**
     * Sql "TINYTEXT" data type, "A TEXT column with a maximum length of 255"
     *  * Max length affected by multi-byte characters
     */
    object SqlTinyText : SqlType()

    /**
     * Sql "TEXT" data type, "A TEXT column with a maximum length of 65,535"
     *  * Max length affected by multi-byte characters
     */
    object SqlText : SqlType()

    /**
     * Sql "MEDIUMTEXT" data type, "A TEXT column with a maximum length of 16,777,215"
     *  * Max length affected by multi-byte characters
     */
    object SqlMediumText : SqlType()

    /**
     * Sql "LONGTEXT" data type, "A TEXT column with a maximum length of 4,294,967,295"
     *  * Max length affected by multi-byte characters
     */
    object SqlLongText : SqlType()


    // misc types

    /**
     * Sql "BOOL/TINYINT" data type, "A type that can take either true(1), false(0), or NULL"
     */
    object SqlBoolean : SqlType()

    /**
     * Sql "SET" data type, "A set. A string object that can have zero or more values"
     *  * Each value must be chosen from the list of values specified
     *
     * **Maximum size**
     *  * **64 elements**
     */
    object SqlSet : CollType() {

        operator fun get(values: Collection<Any>, vararg attributes: Attribute<*>?): Cache {
            return get(values.toTypedArray(), attributes)
        }

        @JvmName("otherGet")
        operator fun get(values: Collection<Any>, attributes: Array<out Attribute<*>?>): Cache {
            return get(values.toTypedArray(), attributes)
        }

    }

    /**
     * Sql "ENUM" data type, "An enumeration. A string object that can have only one value"
     *  * The value must be chosen from the list of values specified. or NULL
     *
     *  **Maximum size**
     *   * **65,535 distinct elements**
     */
    object SqlEnum : CollType() {

        operator fun get(clazz: KClass<out Enum<*>>, vararg attributes: Attribute<*>?): Cache {
            return get(clazz.java.enumConstants, attributes)
        }

        @JvmName("otherGet")
        operator fun get(clazz: KClass<out Enum<*>>, attributes: Array<out Attribute<*>?>): Cache {
            return get(clazz.java.enumConstants, attributes)
        }

    }


    /**
     * Represents row column specific attributes
     */
    sealed class Attribute<out T : Any?>(override val name: String) : Named {

        internal abstract val value: T


        /**
         * Represents the default value for rows in this column
         *  * You should like... never use this btw... this is an ORM, don't be a dumbo
         *  * Actually, I'm not even going to implement it... sue me.. ¯\_(-_-)_/¯
         *  * Also, I'm marking it as internal, I might revisit it later, idk..
         */
        internal class SqlDefault<out T : Any?>(override val value: T) : Attribute<T>("DEFAULT")


        /**
         * Represents a column whose values cannot be NULL
         */
        object SqlNotNull : Attribute<Boolean>("NOT NULL") {
            override val value = true
        }

        /**
         * Represents a column whose values are the PRIMARY KEY of the table
         */
        object SqlPrimary : Attribute<Boolean>("PRIMARY KEY") {
            override val value = true
        }

        /**
         * Represents a column whose number values are UNSIGNED
         */
        object SqlUnsigned : Attribute<Boolean>("UNSIGNED") {
            override val value = true
        }

    }

}