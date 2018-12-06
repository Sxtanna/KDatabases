package com.sxtanna.db

import com.sxtanna.db.ext.*
import com.sxtanna.db.struct.Database
import com.sxtanna.db.struct.Resolver
import com.sxtanna.db.struct.Table
import com.sxtanna.db.struct.base.Duplicate
import com.sxtanna.db.struct.base.Order
import com.sxtanna.db.struct.base.Order.*
import com.sxtanna.db.struct.base.Order.Direction.ASCEND
import com.sxtanna.db.struct.base.Where
import com.sxtanna.db.struct.base.Where.Clause.Between
import com.sxtanna.db.struct.statement.*
import com.sxtanna.db.type.Executed
import org.intellij.lang.annotations.Language
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

/**
 * Main task class
 * * If a call doesn't return [Unit] it requires you to make a call to either
 * * [SelectN.componentN] directly or through destructuring
 * * or
 * * [Executed.execute] for [Delete] and [Update]
 *
 * [kotlin.reflect.KClass.declaredMemberProperties]
 */
class KueryTask(private val kuery: Kuery, private val connection: Connection) : DBCreator, DBDeleter, DBDropper, DBInserter, DBSelector, DBTruncater, DBUpdater, DBUser {

    /**
     * Push a statement to the database
     */
    fun push(@Language("MySQL") statement: String, block: PreparedStatement.() -> Unit = {}) {
        kuery.logger.debug("Pushing statement `$statement`")
        with(kuery) { connection.push(statement, block) }
    }

    /**
     * Pull the results of a query from the database
     */
    fun pull(@Language("MySQL") statement: String, block: PreparedStatement.() -> Unit = {}): ResultSet {
        kuery.logger.debug("Pulling statement `$statement`")
        return with(kuery) { connection.pull(statement, block) }
    }


    //region Table cursors
    /**
     * Open and return a Table task handler
     */
    fun <E : Any> on(table: Table<E>) = KueryTaskTable(table, this)

    /**
     * Open and use a Table task handler
     */
    fun <E : Any, R : Any> on(table: Table<E>, block: KueryTaskTable<E>.() -> R): R {
        return on(table).block()
    }
    //endregion


    //region Create statement
    override fun create(database: Database, andTables: Boolean) {
        push("CREATE DATABASE ${database.name}")

        if (andTables) database.tables.forEach { create(it) }
    }

    override fun <E : Any> create(table: Table<E>) {
        val columns = table.columns.entries.joinToString { "${it.key} ${it.value}" }
        push("CREATE TABLE IF NOT EXISTS ${table.name}($columns)")
    }
    //endregion


    //region Delete statements
    override fun <E : Any> delete(table: Table<E>): Delete<E> {
        return DeleteImpl(table)
    }

    override fun <E : Any> delete(table: Table<E>, rows: Collection<E>) {
        if (rows.isEmpty()) return

        val statement = "DELETE FROM ${table.name} WHERE ${table.fields.joinToString(" AND ") { "${it.name}=?" }}"
        kuery.logger.debug("Pushing statement `$statement`")

        val state = connection.prepareStatement(statement).apply {

            var index = 1
            rows.forEach { e ->
                table.fields.map { it.get(e) }.forEach { this[index++] = it }
                index = 1

                addBatch()
            }

        }

        state.executeBatch()
    }

    override fun <E : Any> deleteAllRows(table: Table<E>) {
        push("DELETE FROM ${table.name}")
    }
    //endregion


    //region Drop statements
    override fun drop(database: Database) {
        push("DROP DATABASE ${database.name}")
    }

    override fun <E : Any> drop(table: Table<E>) {
        push("DROP TABLE ${table.name}")
    }
    //endregion


    //region Insert statements
    override fun <E : Any> insert(table: Table<E>, rows: Collection<E>) {
        if (rows.isEmpty()) return

        val columns = table.columns.keys
        val values = "(${Array(columns.size) { "?" }.joinToString()})"

        push("INSERT INTO ${table.name} (${columns.joinToString()}) VALUES ${(0 until rows.size).joinToString { values }}") {

            var index = 1
            rows.forEach { e ->
                table.fields.map { it.get(e) }.forEach { this[index++] = it }
            }

        }
    }

    override fun <E : Any> insert(table: Table<E>, duplicate: Duplicate<Table<E>>, rows: Collection<E>) {
        if (rows.isEmpty()) return

        val columns = table.columns.keys
        val values = "(${Array(columns.size) { "?" }.joinToString()})"

        push("INSERT INTO ${table.name} (${columns.joinToString()}) VALUES ${(0 until rows.size).joinToString { values }} ${duplicate(table)}") {

            var index = 1
            rows.forEach { e ->
                table.fields.map { it.get(e) }.forEach { this[index++] = it }
            }

        }
    }
    //endregion


    //region Select statements
    override fun <E : Any> select(table: Table<E>): Select1<E, List<E>> {
        return Select1Impl(table, emptyList())
    }

    override fun <E : Any, R1 : Any> selectOne(table: Table<E>, prop: KProperty1<E, R1>): Select1<E, R1> {
        return SelectOneImpl(table, prop)
    }

    override fun <E : Any, R1 : Any?>
            select(table: Table<E>,
                   prop: KProperty1<E, R1>): Select1<E, List<R1>> {
        return Select1Impl(table, listOf(prop))
    }

    override fun <E : Any, R1 : Any?, R2 : Any?>
            select(table: Table<E>,
                   prop1: KProperty1<E, R1>,
                   prop2: KProperty1<E, R2>): Select2<E, List<R1>, List<R2>> {
        return Select2Impl(table, listOf(prop1, prop2))
    }

    override fun <E : Any, R1 : Any?, R2 : Any?, R3 : Any?>
            select(table: Table<E>,
                   prop1: KProperty1<E, R1>,
                   prop2: KProperty1<E, R2>,
                   prop3: KProperty1<E, R3>): Select3<E, List<R1>, List<R2>, List<R3>> {
        return Select3Impl(table, listOf(prop1, prop2, prop3))
    }

    override fun <E : Any, R1 : Any?, R2 : Any?, R3 : Any?, R4 : Any?>
            select(table: Table<E>,
                   prop1: KProperty1<E, R1>,
                   prop2: KProperty1<E, R2>,
                   prop3: KProperty1<E, R3>,
                   prop4: KProperty1<E, R4>): Select4<E, List<R1>, List<R2>, List<R3>, List<R4>> {
        return Select4Impl(table, listOf(prop1, prop2, prop3, prop4))
    }

    override fun <E : Any, R1 : Any?, R2 : Any?, R3 : Any?, R4 : Any?, R5 : Any?>
            select(table: Table<E>,
                   prop1: KProperty1<E, R1>,
                   prop2: KProperty1<E, R2>,
                   prop3: KProperty1<E, R3>,
                   prop4: KProperty1<E, R4>,
                   prop5: KProperty1<E, R5>): Select5<E, List<R1>, List<R2>, List<R3>, List<R4>, List<R5>> {
        return Select5Impl(table, listOf(prop1, prop2, prop3, prop4, prop5))
    }
    //endregion


    //region Truncate statement
    override fun <E : Any> truncate(table: Table<E>) {
        push("TRUNCATE TABLE ${table.name}")
    }
    //endregion


    //region Update statements
    override fun <E : Any> update(table: Table<E>): Update<E> {
        return UpdateImpl(table)
    }

    override fun <E : Any> update(table: Table<E>, rows: Collection<E>) {
        val key = table.getPrimaryKey()
                ?: return kuery.logger.error("Cannot update entire rows in a table without a primary key")

        val statement = "UPDATE ${table.name} SET ${table.fields.joinToString { "${it.name}=?" }} WHERE ${key.name}=?"
        kuery.logger.debug("Pushing statement `$statement`")

        connection.prepareStatement(statement).apply {

            var index = 1
            rows.forEach { e ->
                table.fields.map { it.get(e) }.forEach { this[index++] = it }
                this[index++] = key.get(e)

                index = 1

                addBatch()
            }

        }.executeBatch()
    }

    override fun <E : Any> updateAllRows(table: Table<E>, row: E) {
        if (table.getPrimaryKey() != null) return kuery.logger.error("Cannot update all rows to a single row of a table with a primary key")

        push("UPDATE ${table.name} SET ${table.fields.joinToString { "${it.name}=?" }}") {

            var index = 1
            table.fields.map { it.get(row) }.forEach { this[index++] = it }

        }
    }

    override fun <E : Any> updateAllRows(table: Table<E>, vararg values: Value<E, *>) {
        if (values.any { it.prop.findAnnotation<PrimaryKey>() != null }) {
            return kuery.logger.error("Cannot update all rows in a table with a primary key to the same value")
        }

        push("UPDATE ${table.name} SET ${values.joinToString { "${it.prop.name}=?" }}") {

            var index = 1
            values.forEach { this[index++] = it.value }

        }
    }
    //endregion


    //region Use statement
    override fun use(database: Database) {
        push("USE ${database.name}")
    }
    //endregion


    //region Select implementations
    internal abstract inner class SelectImpl<E : Any, R1 : Any>(protected val table: Table<E>, target: List<KProperty1<E, *>>)
        : Select1<E, R1> {

        private var limit = 0L
        private val order = mutableListOf<Order>()
        private val where = mutableListOf<Where.Clause>()

        internal val results = mutableMapOf<Int, MutableList<Any?>>()

        protected val target = if (target.isEmpty()) "*" else target.joinToString { it.name }


        protected lateinit var lastResult: ResultSet


        override fun execute() {
            if (::lastResult.isInitialized) return // not going to pull results again

            val order = order.isNotEmpty().value(" ORDER BY ${order.joinToString()}")
            val where = where.isNotEmpty().value(" WHERE ${where.joinToString(" AND ")}")
            val limit = if (limit < 1) "" else " LIMIT $limit"

            // pull results from database
            val result = pull("SELECT $target FROM ${table.name}$where$order$limit") {

                var offset = 0
                this@SelectImpl.where.forEachIndexed { index, it ->

                    this[index + 1 + offset] = it.data

                    if (it is Between) {
                        this[index + 1 + ++offset] = it.data
                    }

                }

            }


            // return early if empty
            if (result.isBeforeFirst.not()) {
                result.close()
                lastResult = result
                return
            }


            // gather column names
            val columns = result.metaData.let { m ->
                (1..m.columnCount).map { m.getColumnName(it) }
            }

            // gather fields
            val fields = table.clazz.declaredMemberProperties.associateBy { it.name }

            // populate results with empty
            columns.forEachIndexed { index, _ ->
                results[index] = mutableListOf()
            }

            // for every result, populate every index
            result.whileNext {

                columns.forEachIndexed { index, s ->

                    val field = fields[s]
                    results[index]?.add(field?.let { Resolver.SqlI[this, it] })

                }

            }

            lastResult = result
        }

        final override fun component1(): R1 {
            if (::lastResult.isInitialized.not()) execute() // maintain past behavior
            return first() // return first component
        }

        override fun limit(count: Long): Select1<E, R1> = apply {
            this.limit = count
        }

        override fun <R : Any> where(prop: KProperty1<E, R>, block: Where<E, R>.(KProperty1<E, R>) -> Unit): Select1<E, R1> = apply {
            where.addAll(Where<E, R>().apply { block(prop) }.clauses)
        }

        override fun <R : Any> order(prop: KProperty1<E, R>, direction: Direction): Select1<E, R1> = apply {
            order.add(if (direction == ASCEND) Ascend(prop.name) else Descend(prop.name))
        }

        protected open fun first(): R1 {
            if (results.isEmpty()) return emptyList<Any>() as R1 // gotta keep that quick return

            return checkNotNull(results[0] as R1) {
                table.fields[1].let { "Results for ${it.name} weren't ${it.returnType}" }
            }
        }

    }

    internal inner class SelectOneImpl<E : Any, R1 : Any>(table: Table<E>, target: KProperty1<E, R1>)
        : SelectImpl<E, R1>(table, listOf(target)) {

        override fun first(): R1 {
            return (super.first() as List<R1>)[0]
        }

    }

    internal open inner class Select1Impl<E : Any, R1 : Any?>(table: Table<E>, target: List<KProperty1<E, *>>)
        : SelectImpl<E, List<R1>>(table, target) {

        final override fun first(): List<R1> {
            return if (target != "*") super.first() else { // try to create the entire object

                // get primary constructor
                val construct = table.clazz.primaryConstructor ?: return emptyList()

                val params = construct.parameters
                val pNames = params.associateBy { it.name ?: "" }

                // associate columns with indexes
                val target = lastResult.metaData.let { m ->
                    (1..m.columnCount).associate { it to m.getColumnName(it) }
                }

                // associate params with indexes
                val map = results.keys.associateBy { pNames[target[it + 1]] }

                // the least amount of creatable objects (might not actually need this due to nullability)
                val min = results.values.minBy { it.size }?.size ?: 0

                val values = mutableMapOf<KParameter, Any?>()

                (0 until min).mapNotNull { ri ->

                    params.forEach {
                        val index = map[it]
                        values[it] = results[index]?.get(ri)
                    }

                    construct.callBy(values).also { values.clear() }
                } as List<R1>
            }
        }

    }

    internal open inner class Select2Impl<E : Any, R1 : Any?, R2 : Any?>(table: Table<E>, target: List<KProperty1<E, *>>)
        : Select1Impl<E, R1>(table, target), Select2<E, List<R1>, List<R2>> {

        final override fun component2(): List<R2> {
            return checkNotNull(results[1] as List<R2>) {
                table.fields[2].let { "Results for ${it.name} weren't ${it.returnType}" }
            }
        }


        override fun limit(count: Long): Select2<E, List<R1>, List<R2>> {
            super.limit(count)
            return this
        }

        override fun <R : Any> order(prop: KProperty1<E, R>, direction: Direction): Select2<E, List<R1>, List<R2>> {
            super.order(prop, direction)
            return this
        }

        override fun <R : Any> where(prop: KProperty1<E, R>, block: Where<E, R>.(KProperty1<E, R>) -> Unit): Select2<E, List<R1>, List<R2>> {
            super.where(prop, block)
            return this
        }

    }

    internal open inner class Select3Impl<E : Any, R1 : Any?, R2 : Any?, R3 : Any?>(table: Table<E>, target: List<KProperty1<E, *>>)
        : Select2Impl<E, R1, R2>(table, target), Select3<E, List<R1>, List<R2>, List<R3>> {

        final override fun component3(): List<R3> {
            return checkNotNull(results[2] as? List<R3>) {
                table.fields[3].let { "Results for ${it.name} weren't ${it.returnType}" }
            }
        }


        override fun limit(count: Long): Select3<E, List<R1>, List<R2>, List<R3>> {
            super.limit(count)
            return this
        }

        override fun <R : Any> order(prop: KProperty1<E, R>, direction: Direction): Select3<E, List<R1>, List<R2>, List<R3>> {
            super.order(prop, direction)
            return this
        }

        override fun <R : Any> where(prop: KProperty1<E, R>, block: Where<E, R>.(KProperty1<E, R>) -> Unit): Select3<E, List<R1>, List<R2>, List<R3>> {
            super.where(prop, block)
            return this
        }

    }

    internal open inner class Select4Impl<E : Any, R1 : Any?, R2 : Any?, R3 : Any?, R4 : Any?>(table: Table<E>, target: List<KProperty1<E, *>>)
        : Select3Impl<E, R1, R2, R3>(table, target), Select4<E, List<R1>, List<R2>, List<R3>, List<R4>> {

        final override fun component4(): List<R4> {
            return checkNotNull(results[3] as List<R4>) {
                table.fields[4].let { "Results for ${it.name} weren't ${it.returnType}" }
            }
        }


        override fun limit(count: Long): Select4<E, List<R1>, List<R2>, List<R3>, List<R4>> {
            super.limit(count)
            return this
        }

        override fun <R : Any> order(prop: KProperty1<E, R>, direction: Direction): Select4<E, List<R1>, List<R2>, List<R3>, List<R4>> {
            super.order(prop, direction)
            return this
        }

        override fun <R : Any> where(prop: KProperty1<E, R>, block: Where<E, R>.(KProperty1<E, R>) -> Unit): Select4<E, List<R1>, List<R2>, List<R3>, List<R4>> {
            super.where(prop, block)
            return this
        }

    }

    internal open inner class Select5Impl<E : Any, R1 : Any?, R2 : Any?, R3 : Any?, R4 : Any?, R5 : Any?>(table: Table<E>, target: List<KProperty1<E, *>>)
        : Select4Impl<E, R1, R2, R3, R4>(table, target), Select5<E, List<R1>, List<R2>, List<R3>, List<R4>, List<R5>> {

        final override fun component5(): List<R5> {
            return checkNotNull(results[4] as List<R5>) {
                table.fields[5].let { "Results for ${it.name} weren't ${it.returnType}" }
            }
        }


        override fun limit(count: Long): Select5<E, List<R1>, List<R2>, List<R3>, List<R4>, List<R5>> {
            super.limit(count)
            return this
        }

        override fun <R : Any> order(prop: KProperty1<E, R>, direction: Direction): Select5<E, List<R1>, List<R2>, List<R3>, List<R4>, List<R5>> {
            super.order(prop, direction)
            return this
        }

        override fun <R : Any> where(prop: KProperty1<E, R>, block: Where<E, R>.(KProperty1<E, R>) -> Unit): Select5<E, List<R1>, List<R2>, List<R3>, List<R4>, List<R5>> {
            super.where(prop, block)
            return this
        }

    }
    //endregion


    //region Delete implementation
    internal inner class DeleteImpl<E : Any>(private val table: Table<E>) : Delete<E> {

        private val where = mutableListOf<Where.Clause>()

        private var executed = false

        override fun execute() {
            if (executed) return
            executed = true

            if (where.isEmpty()) {
                return kuery.logger.error("Attempting to run delete with no clauses, this will delete all rows, please use KueryTask#deleteAllRows")
            }

            push("DELETE FROM ${table.name} WHERE ${where.joinToString(" AND ")}") {

                var offset = 0
                where.forEachIndexed { index, it ->

                    this[index + 1 + offset] = it.data

                    if (it is Between) {
                        this[index + 1 + ++offset] = it.data
                    }

                }

            }
        }


        override fun <R : Any?> where(prop: KProperty1<E, R>, block: Where<E, R>.(KProperty1<E, R>) -> Unit) = apply {
            where.addAll(Where<E, R>().apply { block(prop) }.clauses)
        }

    }
    //endregion


    //region Update implementation
    internal inner class UpdateImpl<E : Any>(private val table: Table<E>) : Update<E> {

        private val value = mutableListOf<Value<E, *>>()
        private val where = mutableListOf<Where.Clause>()

        private var executed = false

        override fun execute() {
            if (executed) return
            executed = true

            if (value.isEmpty()) {
                return kuery.logger.error("Attempting to run update with no values, this will do nothing")
            }
            if (where.isEmpty()) {
                return kuery.logger.error("Attempting to run update with no clauses, this will update every row, please use KueryTask#updateAllRows")
            }

            push("UPDATE ${table.name} SET ${value.joinToString { "${it.prop.name}=?" }} WHERE ${where.joinToString(" AND ")}") {

                var index = 1
                value.forEach { this[index++] = it.value }

                var offset = 0
                where.forEachIndexed { index, it ->

                    this[index + 1 + offset] = it.data

                    if (it is Between) {
                        this[index + 1 + ++offset] = it.data
                    }

                }

            }
        }


        override fun <R : Any?> set(column: KProperty1<E, R>, value: R): Update<E> = apply {
            this.value.add(Value(column, value))
        }


        override fun <R : Any?> where(prop: KProperty1<E, R>, block: Where<E, R>.(KProperty1<E, R>) -> Unit) = apply {
            where.addAll(Where<E, R>().apply { block(prop) }.clauses)
        }

    }
    //endregion

}