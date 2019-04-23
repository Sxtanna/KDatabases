package com.sxtanna.db.struct.statement

import com.sxtanna.db.struct.Table
import com.sxtanna.db.struct.base.Order.Direction
import com.sxtanna.db.struct.base.Order.Direction.ASCEND
import com.sxtanna.db.struct.base.Order.Direction.DESCEND
import com.sxtanna.db.struct.base.Where
import com.sxtanna.db.type.Executed
import kotlin.reflect.KProperty1

/**
 * Describes an SQL "SELECT" statement for one column
 */
interface Select1<T : Any, R1 : Any> : Executed {

    /**
     * The first component result of this "SELECT" statement
     */
    operator fun component1(): R1


    fun limit(count: Long): Select1<T, R1>


    /**
     * Define the "ORDER BY" clauses for the returned results
     */
    fun <R : Any> order(prop: KProperty1<T, R>, direction: Direction = ASCEND): Select1<T, R1>

    /**
     * Define the "WHERE" clauses for this statement
     */
    fun <R : Any> where(prop: KProperty1<T, R>, block: Where<T, R>.(KProperty1<T, R>) -> Unit): Select1<T, R1>


    /**
     * Shorthand for defining "Ascending" ordering
     */
    fun <R : Any> ascend(prop: KProperty1<T, R>) = order(prop, ASCEND)

    /**
     * Shorthand for defining "Descending" ordering
     */
    fun <R : Any> descend(prop: KProperty1<T, R>) = order(prop, DESCEND)

}

/**
 * Describes an SQL "SELECT" statement for two columns
 */
interface Select2<T : Any, R1 : Any, R2 : Any> : Select1<T, R1> {

    /**
     * The second component result of this "SELECT" statement
     */
    operator fun component2(): R2


    override fun limit(count: Long): Select2<T, R1, R2>

    override fun <R : Any> order(prop: KProperty1<T, R>, direction: Direction): Select2<T, R1, R2>

    override fun <R : Any> where(prop: KProperty1<T, R>, block: Where<T, R>.(KProperty1<T, R>) -> Unit): Select2<T, R1, R2>

}

/**
 * Describes an SQL "SELECT" statement for three columns
 */
interface Select3<T : Any, R1 : Any, R2 : Any, R3 : Any> : Select2<T, R1, R2> {

    /**
     * The third component result of this "SELECT" statement
     */
    operator fun component3(): R3


    override fun limit(count: Long): Select3<T, R1, R2, R3>

    override fun <R : Any> order(prop: KProperty1<T, R>, direction: Direction): Select3<T, R1, R2, R3>

    override fun <R : Any> where(prop: KProperty1<T, R>, block: Where<T, R>.(KProperty1<T, R>) -> Unit): Select3<T, R1, R2, R3>

}

/**
 * Describes an SQL "SELECT" statement for four columns
 */
interface Select4<T : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any> : Select3<T, R1, R2, R3> {

    /**
     * The fourth component result of this "SELECT" statement
     */
    operator fun component4(): R4


    override fun limit(count: Long): Select4<T, R1, R2, R3, R4>

    override fun <R : Any> order(prop: KProperty1<T, R>, direction: Direction): Select4<T, R1, R2, R3, R4>

    override fun <R : Any> where(prop: KProperty1<T, R>, block: Where<T, R>.(KProperty1<T, R>) -> Unit): Select4<T, R1, R2, R3, R4>

}

/**
 * Describes an SQL "SELECT" statement for five columns
 */
interface Select5<T : Any, R1 : Any, R2 : Any, R3 : Any, R4 : Any, R5 : Any> : Select4<T, R1, R2, R3, R4> {

    /**
     * The fifth component result of this "SELECT" statement
     */
    operator fun component5(): R5


    override fun limit(count: Long): Select5<T, R1, R2, R3, R4, R5>

    override fun <R : Any> order(prop: KProperty1<T, R>, direction: Direction): Select5<T, R1, R2, R3, R4, R5>

    override fun <R : Any> where(prop: KProperty1<T, R>, block: Where<T, R>.(KProperty1<T, R>) -> Unit): Select5<T, R1, R2, R3, R4, R5>

}


/**
 * An object that can select rows and columns from a table
 */
interface DBSelector {

    /**
     * Select all rows from the table
     *  * Destructs to a list of [T]
     */
    fun <T : Any> select(table: Table<T>): Select1<T, List<T>>

    fun <T : Any, R1 : Any>
            selectOne(table: Table<T>,
                      prop: KProperty1<T, R1>): Select1<T, R1>


    /**
     * Select [R1] rows from the table
     *  * Destructs to a list of [R1]
     */
    fun <T : Any, R1 : Any?>
            select(table: Table<T>,
                   prop: KProperty1<T, R1>): Select1<T, List<R1>>

    /**
     * Select [R1] and [R2] rows from the table
     *  * Destructs to lists of [R1] and [R2]
     */
    fun <T : Any, R1 : Any?, R2 : Any?>
            select(table: Table<T>,
                   prop1: KProperty1<T, R1>,
                   prop2: KProperty1<T, R2>): Select2<T, List<R1>, List<R2>>

    /**
     * Select [R1], [R2], and [R3] rows from the table
     *  * Destructs to lists of [R1], [R2], and [R3]
     */
    fun <T : Any, R1 : Any?, R2 : Any?, R3 : Any?>
            select(table: Table<T>,
                   prop1: KProperty1<T, R1>,
                   prop2: KProperty1<T, R2>,
                   prop3: KProperty1<T, R3>): Select3<T, List<R1>, List<R2>, List<R3>>

    /**
     * Select [R1], [R2], [R3], and [R4] rows from the table
     *  * Destructs to lists of [R1], [R2], [R3], and [R4]
     */
    fun <T : Any, R1 : Any?, R2 : Any?, R3 : Any?, R4 : Any?>
            select(table: Table<T>,
                   prop1: KProperty1<T, R1>,
                   prop2: KProperty1<T, R2>,
                   prop3: KProperty1<T, R3>,
                   prop4: KProperty1<T, R4>): Select4<T, List<R1>, List<R2>, List<R3>, List<R4>>

    /**
     * Select [R1], [R2], [R3], [R4] and [R5] rows from the table
     *  * Destructs to lists of [R1], [R2], [R3], [R4], and [R5]
     */
    fun <T : Any, R1 : Any?, R2 : Any?, R3 : Any?, R4 : Any?, R5 : Any?>
            select(table: Table<T>,
                   prop1: KProperty1<T, R1>,
                   prop2: KProperty1<T, R2>,
                   prop3: KProperty1<T, R3>,
                   prop4: KProperty1<T, R4>,
                   prop5: KProperty1<T, R5>): Select5<T, List<R1>, List<R2>, List<R3>, List<R4>, List<R5>>


    /**
     * An object that can select rows and columns from its table
     */
    interface TableSelector<T : Any> {

        /**
         * Select all rows from the table
         *  * Destructs to a list of [T]
         */
        fun select(): Select1<T, List<T>>

        /**
         * Select [R1] rows from the table
         *  * Destructs to a list of [R1]
         */
        fun <R1 : Any?>
                select(prop: KProperty1<T, R1>): Select1<T, List<R1>>

        /**
         * Select [R1] and [R2] rows from the table
         *  * Destructs to lists of [R1] and [R2]
         */
        fun <R1 : Any?, R2 : Any?>
                select(prop1: KProperty1<T, R1>,
                       prop2: KProperty1<T, R2>): Select2<T, List<R1>, List<R2>>

        /**
         * Select [R1], [R2], and [R3] rows from the table
         *  * Destructs to lists of [R1], [R2], and [R3]
         */
        fun <R1 : Any?, R2 : Any?, R3 : Any?>
                select(prop1: KProperty1<T, R1>,
                       prop2: KProperty1<T, R2>,
                       prop3: KProperty1<T, R3>): Select3<T, List<R1>, List<R2>, List<R3>>

        /**
         * Select [R1], [R2], [R3], and [R4] rows from the table
         *  * Destructs to lists of [R1], [R2], [R3], and [R4]
         */
        fun <R1 : Any?, R2 : Any?, R3 : Any?, R4 : Any?>
                select(prop1: KProperty1<T, R1>,
                       prop2: KProperty1<T, R2>,
                       prop3: KProperty1<T, R3>,
                       prop4: KProperty1<T, R4>): Select4<T, List<R1>, List<R2>, List<R3>, List<R4>>

        /**
         * Select [R1], [R2], [R3], [R4] and [R5] rows from the table
         *  * Destructs to lists of [R1], [R2], [R3], [R4], and [R5]
         */
        fun <R1 : Any?, R2 : Any?, R3 : Any?, R4 : Any?, R5 : Any?>
                select(prop1: KProperty1<T, R1>,
                       prop2: KProperty1<T, R2>,
                       prop3: KProperty1<T, R3>,
                       prop4: KProperty1<T, R4>,
                       prop5: KProperty1<T, R5>): Select5<T, List<R1>, List<R2>, List<R3>, List<R4>, List<R5>>

    }

}