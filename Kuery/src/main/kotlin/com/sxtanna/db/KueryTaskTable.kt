package com.sxtanna.db

import com.sxtanna.db.ext.Value
import com.sxtanna.db.struct.Table
import com.sxtanna.db.struct.base.Duplicate
import com.sxtanna.db.struct.statement.*
import kotlin.reflect.KProperty1

/**
 * A [KueryTask] specific for the supplied [Table]
 */
class KueryTaskTable<T : Any>(private val table: Table<T>, val task: KueryTask) : DBCreator.TableCreator<T>, DBDeleter.TableDeleter<T>, DBDropper.TableDropper<T>, DBInserter.TableInserter<T>, DBSelector.TableSelector<T>, DBTruncater.TableTruncater<T>, DBUpdater.TableUpdater<T> {

    //region Create statement
    override fun create() = task.create(table)
    //endregion


    //region Delete statements
    override fun delete(): Delete<T> {
        return task.delete(table)
    }

    override fun delete(rows: Collection<T>) {
        task.delete(table, rows)
    }

    override fun deleteAllRows() {
        task.deleteAllRows(table)
    }
    //endregion


    //region Drop statement
    override fun drop() {
        task.drop(table)
    }
    //endregion


    //region Insert statements
    override fun insert(rows: Collection<T>) {
        task.insert(table, rows)
    }

    override fun insert(duplicate: Duplicate<Table<T>>, rows: Collection<T>) {
        task.insert(table, duplicate, rows)
    }
    //endregion


    //region Select statements
    override fun select() = task.select(table)

    override fun <R1 : Any?>
            select(prop: KProperty1<T, R1>) = task.select(table, prop)

    override fun <R1 : Any?, R2 : Any?>
            select(prop1: KProperty1<T, R1>,
                   prop2: KProperty1<T, R2>) = task.select(table, prop1, prop2)

    override fun <R1 : Any?, R2 : Any?, R3 : Any?>
            select(prop1: KProperty1<T, R1>,
                   prop2: KProperty1<T, R2>,
                   prop3: KProperty1<T, R3>) = task.select(table, prop1, prop2, prop3)

    override fun <R1 : Any?, R2 : Any?, R3 : Any?, R4 : Any?>
            select(prop1: KProperty1<T, R1>,
                   prop2: KProperty1<T, R2>,
                   prop3: KProperty1<T, R3>,
                   prop4: KProperty1<T, R4>) = task.select(table, prop1, prop2, prop3, prop4)

    override fun <R1 : Any?, R2 : Any?, R3 : Any?, R4 : Any?, R5 : Any?>
            select(prop1: KProperty1<T, R1>,
                   prop2: KProperty1<T, R2>,
                   prop3: KProperty1<T, R3>,
                   prop4: KProperty1<T, R4>,
                   prop5: KProperty1<T, R5>) = task.select(table, prop1, prop2, prop3, prop4, prop5)
    //endregion


    //region Truncate statement
    override fun truncate() {
        task.truncate(table)
    }
    //endregion


    //region Update statements
    override fun update(): Update<T> = task.update(table)

    override fun update(rows: Collection<T>) = task.update(table, rows)

    override fun update(vararg values: Value<T, *>) = task.update(table, *values)

    override fun updateAllRows(row: T) {
        task.updateAllRows(table, row)
    }

    override fun updateAllRows(vararg values: Value<T, *>) {
        task.updateAllRows(table, *values)
    }
    //endregion

}