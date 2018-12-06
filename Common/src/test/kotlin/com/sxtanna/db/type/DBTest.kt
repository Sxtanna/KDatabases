package com.sxtanna.db.type

object DBTest {

    inline fun <T> measureTimeNanos(block : () -> T) : Pair<T, Long> {
        val time0 = System.nanoTime()
        val result = block()
        val time1 = System.nanoTime() - time0

        return result to time1
    }


    inline fun <T> measureTimeMillis(block : () -> T) : Pair<T, Long> {
        val time0 = System.currentTimeMillis()
        val result = block()
        val time1 = System.currentTimeMillis() - time0

        return result to time1
    }


}