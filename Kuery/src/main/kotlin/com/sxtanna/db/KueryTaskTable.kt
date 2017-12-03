package com.sxtanna.db

import com.sxtanna.db.ext.Value
import com.sxtanna.db.struct.Table
import com.sxtanna.db.struct.base.Duplicate
import com.sxtanna.db.struct.statement.*
import kotlin.reflect.KProperty1

/**
 * A [KueryTask] specific for the supplied [Table]
 */
class KueryTaskTable<E : Any>(private val table : Table<E>, private val task : KueryTask) : Creator.TableCreator<E>, Deleter.TableDeleter<E>, Dropper.TableDropper<E>, Inserter.TableInserter<E>, Selector.TableSelector<E>, Truncater.TableTruncater<E>, Updater.TableUpdater<E> {

    //region Create statement
    override fun create() = task.create(table)
    //endregion


    //region Delete statements
    override fun delete() : Delete<E> {
        return task.delete(table)
    }

    override fun delete(rows : Collection<E>) {
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
    override fun insert(rows : Collection<E>) {
        task.insert(table, rows)
    }

    override fun insert(duplicate : Duplicate<Table<E>>, rows : Collection<E>) {
        task.insert(table, duplicate, rows)
    }
    //endregion


    //region Select statements
    override fun select() = task.select(table)

    override fun <R1 : Any?>
          select(prop : KProperty1<E, R1>) = task.select(table, prop)

    override fun <R1 : Any?, R2 : Any?>
          select(prop1 : KProperty1<E, R1>,
                 prop2 : KProperty1<E, R2>) = task.select(table, prop1, prop2)

    override fun <R1 : Any?, R2 : Any?, R3 : Any?>
          select(prop1 : KProperty1<E, R1>,
                 prop2 : KProperty1<E, R2>,
                 prop3 : KProperty1<E, R3>) = task.select(table, prop1, prop2, prop3)

    override fun <R1 : Any?, R2 : Any?, R3 : Any?, R4 : Any?>
          select(prop1 : KProperty1<E, R1>,
                 prop2 : KProperty1<E, R2>,
                 prop3 : KProperty1<E, R3>,
                 prop4 : KProperty1<E, R4>) = task.select(table, prop1, prop2, prop3, prop4)

    override fun <R1 : Any?, R2 : Any?, R3 : Any?, R4 : Any?, R5 : Any?>
          select(prop1 : KProperty1<E, R1>,
                 prop2 : KProperty1<E, R2>,
                 prop3 : KProperty1<E, R3>,
                 prop4 : KProperty1<E, R4>,
                 prop5 : KProperty1<E, R5>) = task.select(table, prop1, prop2, prop3, prop4, prop5)
    //endregion


    //region Truncate statement
    override fun truncate() {
        task.truncate(table)
    }
    //endregion


    //region Update statements
    override fun update() : Update<E> = task.update(table)

    override fun update(rows : Collection<E>) = task.update(table, rows)

    override fun update(vararg values : Value<E, *>) = task.update(table, *values)

    override fun updateAllRows(row : E) {
        task.updateAllRows(table, row)
    }

    override fun updateAllRows(vararg values : Value<E, *>) {
        task.updateAllRows(table, *values)
    }
    //endregion

}