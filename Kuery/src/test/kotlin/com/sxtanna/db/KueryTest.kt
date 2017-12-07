package com.sxtanna.db

import com.sxtanna.db.config.KueryConfig
import com.sxtanna.db.config.KueryConfig.*
import com.sxtanna.db.ext.PrimaryKey
import com.sxtanna.db.ext.Size
import com.sxtanna.db.struct.Table
import com.sxtanna.db.struct.base.Duplicate.Ignore
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.lang.System.getProperty
import java.math.BigDecimal
import java.sql.SQLException
import java.util.*
import java.util.UUID.randomUUID

/**
 * Within these tests, the connection is not immediately returned as it would be in
 * @sample [useConnection]
 */
@TestInstance(PER_CLASS)
class KueryTest {

    data class Bank(@PrimaryKey val id : Int, @Size(10, 2) val balance : BigDecimal)
    data class User(@PrimaryKey val uuid : UUID, val name : String, val auth : String?)

    private val config = KueryConfig(OptionsData(address, port, database), OptionsPool(), OptionsUser(userName, passWord))

    private val kuery = Kuery(config)

    private val banks = Table.of<Bank>()
    private val users = Table.of<User>()


    //region Init/DeInit
    @BeforeAll
    internal fun load() {
        kuery.load()
    }

    @AfterAll
    internal fun unload() {
        kuery.unload()
    }


    /**
     * Don't ask, I needed to do some stuff
     */
    @AfterEach
    fun delayAfter() {
        //Thread.sleep(500)
    }
    //endregion


    //region Tests
    /**
     * Create
     */
    @Test
    internal fun test0() {
        kuery(banks).create()
        kuery(users).create()
    }

    /**
     * Insert
     */
    @Test
    internal fun test1() {
        val new = (0..9).map { User(randomUUID(), "Ranald", "Password") }

        val (all) = kuery(users) {
            insert(Ignore, new)
            select()
        }

        val allList = all.sortedBy { it.uuid.toString() }
        val newList = new.sortedBy { it.uuid.toString() }

        assertEquals(allList, newList) { "The users in the database do not match the new ones" }

        kuery(users).insert(User(randomUUID(), "First", "Second"))
    }

    /**
     * Delete
     */
    @Test
    internal fun test2() {

        val (all) = kuery(users) {
            delete().where(User::name) {
                it equals "First"
            }.execute()
            select()
        }

        assertEquals(10, all.size) { "Failed to delete the row" }
    }

    /**
     * Select
     */
    @Test
    internal fun test3() {

        val (all) = kuery(users) {
            select()
        }

        assertEquals(10, all.size, "All users weren't inserted")
    }

    /**
     * Update
     */
    @Test
    internal fun test4() {

        kuery(users) {

            val uuid = randomUUID()

            val user = User(uuid, "Sxtanna", "Password")
            insert(user)

            val copy = user.copy(auth = "NewPassword")
            update(copy)

            val (auth) = select(User::auth).where(User::uuid) {
                it equals uuid
            }

            assertEquals(1, auth.size) { "Auth result count was wrong" }
            assertEquals("NewPassword", auth[0]) { "Auth result was wrong" }

        }

    }

    /**
     * Multiple tables
     */
    @Test
    internal fun test5() {

        kuery {
            create(banks)
            create(users)

            val (banks) = on(banks) {
                insert(Ignore, Bank(21, BigDecimal(1000)))

                select().where(Bank::balance) {
                    it moreThan BigDecimal(500)
                }
            }

            banks.forEach {
                println("Found member with more than $500 at $${it.balance.toInt()}")
            }


            on(users) {
                insert(Ignore, User(randomUUID(), "Sxtanna", "Password"))

                delete().where(User::name) {
                    it equals "Sxtanna"
                }
            }

        }

    }

    /**
     * Truncate
     */
    @Test
    internal fun test6() {
        kuery {
            truncate(banks)
            val (res0) = select(banks)
            assertEquals(0, res0.size) { "Data was not truncated from Banks" }

            truncate(users)
            val (res1) = select(users)
            assertEquals(0, res1.size) { "Data was not truncated from Users" }
        }
    }

    /**
     * Drop
     */
    @Test
    internal fun test7() {
        kuery {
            drop(banks)
            drop(users)

            assertThrows<SQLException> {
                val (res0) = select(banks)
            }

            assertThrows<SQLException> {
                val (res1) = select(users)
            }
        }
    }
    //endregion


    /**
     * Example
     */
    private fun useConnection() {

        kuery() // connection opened here, closed via timeout

        kuery { // connection opened here

            on(users) {

            }

        } // connection closed here

    }


    /**
     * These are purely internal, don't try to use this in your code unless you set them as well..
     */
    private companion object {

        val userName = checkNotNull(getProperty("kuery.user")) // -Dkuery.user
        val passWord = checkNotNull(getProperty("kuery.pass")) // -Dkuery.pass

        val address  = checkNotNull(getProperty("kuery.address"))                // -Dkuery.address
        val port     = checkNotNull(getProperty("kuery.port", "3306")).toShort() // -Dkuery.port
        val database = checkNotNull(getProperty("kuery.db"))                     // -Dkuery.db

    }

}