package com.sxtanna.db.struct.base

import com.sxtanna.db.ext.value
import com.sxtanna.db.struct.base.Where.Position.*
import kotlin.reflect.KProperty1

/**
 * A collection of Sql "WHERE" clauses
 */
class Where<E : Any, R : Any?> {

    internal val clauses = mutableListOf<Clause>()


    // equals
    /**
     * **WHERE {column}=?**
     */
    infix fun KProperty1<E, R>.equals(other: R) {
        val clause = Clause.Equal(false, other, name)
        clauses.add(clause)
    }

    /**
     * **WHERE {column}!=?**
     */
    infix fun KProperty1<E, R>.notEquals(other: R) {
        val clause = Clause.Equal(true, other, name)
        clauses.add(clause)
    }

    // between
    /**
     * **WHERE {column} BETWEEN ? AND ?**
     */
    infix fun KProperty1<E, R>.within(range: Pair<R, R>) {
        if (range.first == null || range.second == null) {
            throw IllegalArgumentException("Cannot use a range with null values $range")
        }

        val first = range.first ?: return
        val second = range.second ?: return

        val clause = Clause.Between(false, first, second, name)
        clauses.add(clause)
    }

    /**
     * **WHERE {column} NOT BETWEEN ? AND ?**
     */
    infix fun KProperty1<E, R>.notWithin(range: Pair<R, R>) {
        if (range.first == null || range.second == null) {
            throw IllegalArgumentException("Cannot use a range with null values $range")
        }

        val first = range.first ?: return
        val second = range.second ?: return

        val clause = Clause.Between(true, first, second, name)
        clauses.add(clause)
    }

    // like
    /**
     * **WHERE {column} LIKE %?**
     */
    infix fun KProperty1<E, R>.endsWith(other: R) {
        val clause = Clause.Like(END, false, other, name)
        clauses.add(clause)
    }

    /**
     * **WHERE {column} NOT LIKE %?**
     */
    infix fun KProperty1<E, R>.notEndsWith(other: R) {
        val clause = Clause.Like(END, true, other, name)
        clauses.add(clause)
    }

    /**
     * **WHERE {column} LIKE ?%**
     */
    infix fun KProperty1<E, R>.startsWith(other: R) {
        val clause = Clause.Like(START, false, other, name)
        clauses.add(clause)
    }

    /**
     * **WHERE {column} NOT LIKE ?%**
     */
    infix fun KProperty1<E, R>.notStartsWith(other: R) {
        val clause = Clause.Like(START, true, other, name)
        clauses.add(clause)
    }

    /**
     * **WHERE {column} LIKE %?%**
     */
    infix fun KProperty1<E, R>.contains(other: R) {
        val clause = Clause.Like(CONTAINS, false, other, name)
        clauses.add(clause)
    }

    /**
     * **WHERE {column} NOT LIKE %?%**
     */
    infix fun KProperty1<E, R>.notContains(other: R) {
        val clause = Clause.Like(CONTAINS, true, other, name)
        clauses.add(clause)
    }

    // relations

    /**
     * **WHERE {column} < ?**
     */
    infix fun KProperty1<E, R>.lessThan(other: R) {
        if (other == null) throw IllegalArgumentException("Cannot use a relative operator on null")

        val clause = Clause.Less(false, other, name)
        clauses.add(clause)
    }

    /**
     * **WHERE {column} <= ?**
     */
    infix fun KProperty1<E, R>.lessThanOrEquals(other: R) {
        if (other == null) throw IllegalArgumentException("Cannot use a relative operator on null")

        val clause = Clause.Less(true, other, name)
        clauses.add(clause)
    }

    /**
     * **WHERE {column} > ?**
     */
    infix fun KProperty1<E, R>.moreThan(other: R) {
        if (other == null) throw IllegalArgumentException("Cannot use a relative operator on null")

        val clause = Clause.More(false, other, name)
        clauses.add(clause)
    }

    /**
     * **WHERE {column} >= ?**
     */
    infix fun KProperty1<E, R>.moreThanOrEquals(other: R) {
        if (other == null) throw IllegalArgumentException("Cannot use a relative operator on null")

        val clause = Clause.More(true, other, name)
        clauses.add(clause)
    }


    override fun toString() = clauses.joinToString(" AND ")

    /**
     * Create a Pair&lt;[R], [R]> using the range operator
     */
    operator fun R.rangeTo(other: R): Pair<R, R> = this to other


    /**
     * Defines Sql "WHEN" clauses
     */
    sealed class Clause {

        internal abstract val data: Any?
        internal abstract val column: String

        protected open var not: Boolean = false


        abstract override fun toString(): String


        internal class Equal(override var not: Boolean, override val data: Any?, override val column: String) : Clause() {

            override fun toString() = "`$column`${not.value("!")}=?"

        }


        internal class Between(override var not: Boolean, override val data: Any, val other: Any, override val column: String) : Clause() {

            override fun toString() = "`$column` ${not.value("NOT ")}BETWEEN ? AND ?"

        }


        internal class Like(pos: Position, override var not: Boolean, data: Any?, override val column: String) : Clause() {

            override val data: Any = pos.toString().replace("?", data.toString())


            override fun toString() = "`$column` ${not.value("NOT ")}LIKE ?"

        }


        internal abstract class Relational(private val orEqual: Boolean, private val symbol: Char) : Clause() {

            final override var not = false


            override fun toString() = "`$column` $symbol${orEqual.value("=")} ?"

        }


        internal class Less(orEqual: Boolean, override val data: Any, override val column: String) : Relational(orEqual, '<')

        internal class More(orEqual: Boolean, override val data: Any, override val column: String) : Relational(orEqual, '>')

    }


    enum class Position(private val place: String) {

        END      ("%?"),
        START    ("?%"),
        CONTAINS ("%?%");

        override fun toString() = place

    }

}