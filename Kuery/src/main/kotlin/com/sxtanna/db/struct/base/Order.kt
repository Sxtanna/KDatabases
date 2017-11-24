package com.sxtanna.db.struct.base

import com.sxtanna.db.struct.base.Order.Direction.ASCEND
import com.sxtanna.db.struct.base.Order.Direction.DESCEND

/**
 * Describes the ordering of the results returned from a query
 */
sealed class Order(private val direction : Direction) {

    internal abstract val column : String


    override fun toString() = "$column $direction"


    internal class Ascend(override val column : String) : Order(ASCEND)

    internal class Descend(override val column : String) : Order(DESCEND)


    /**
     * The direction for ordering results
     */
    enum class Direction(private val value : String) {

        /**
         * Ascending ordering
         *  * [A > Z]
         */
        ASCEND("ASC"),
        /**
         * Descending ordering
         *  * [Z > A]
         */
        DESCEND("DESC");

        override fun toString() = value

    }

}