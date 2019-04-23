package com.sxtanna.db

import com.sxtanna.db.config.KueryConfig
import com.sxtanna.db.config.KueryConfig.*
import com.sxtanna.db.ext.PrimaryKey
import com.sxtanna.db.ext.Size
import com.sxtanna.db.ext.forEach
import com.sxtanna.db.struct.Database
import com.sxtanna.db.struct.Table
import com.sxtanna.db.struct.base.Duplicate.Ignore
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.lang.System.getProperty
import java.math.BigDecimal
import java.sql.SQLException
import java.util.UUID
import java.util.UUID.randomUUID

/**
 * Within these tests, the connection is not immediately returned as it would be in
 * @sample [useConnection]
 */
@TestInstance(PER_CLASS)
@Suppress("ConstantConditionIf") // ignore this, I just hate the warning...
class KueryTest {

    data class Bank(@PrimaryKey val uuid: UUID, @Size(10, 2) val balance: BigDecimal)
    data class User(@PrimaryKey val uuid: UUID, val name: String, val auth: String?)


    object TestingDB : Database() {

        override val name = "kuerytesting"

        val BANKS = table<Bank>()
        val USERS = table<User>()

    }


    private val config = KueryConfig(OptionsData(address, port, database), OptionsPool(), OptionsUser(userName, passWord))

    private val kuery = Kuery(config)


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

        kuery {

            if (USE_OLD) create(TestingDB, true)
            else {
                use(TestingDB)

                create(TestingDB.BANKS)
                create(TestingDB.USERS)
            }
        }

    }

    /**
     * Insert
     */
    @Test
    internal fun test1() {
        val new = (0..9).map { User(randomUUID(), "Ranald", "Password") }

        val (all) = kuery(TestingDB.USERS) {
            insert(Ignore, new)
            select()
        }

        val allList = all.sortedBy { it.uuid.toString() }
        val newList = new.sortedBy { it.uuid.toString() }

        assertEquals(allList, newList) { "The users in the database do not match the new ones" }

        kuery(TestingDB.USERS).insert(User(randomUUID(), "First", "Second"))
    }

    /**
     * Delete
     */
    @Test
    internal fun test2() {

        val (all) = kuery(TestingDB.USERS) {

            if (USE_OLD.not()) task.use(TestingDB)

            delete().where(User::name) { it equals "First" }.execute()
            select()
        }

        assertEquals(10, all.size) { "Failed to delete the row" }
    }

    /**
     * Select
     */
    @Test
    internal fun test3() {

        val (all) = kuery(TestingDB.USERS) {
            select()
        }

        assertEquals(10, all.size, "All TestingDB.USERS weren't inserted")
    }

    /**
     * Update
     */
    @Test
    internal fun test4() {

        kuery(TestingDB.USERS) {

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
            create(TestingDB.BANKS)
            create(TestingDB.USERS)

            val (banks) = on(TestingDB.BANKS) {
                insert(Ignore, Bank(randomUUID(), BigDecimal(1000)))

                select().where(Bank::balance) {
                    it moreThan BigDecimal(500)
                }
            }

            banks.forEach {
                println("Found member with more than $500 at $${it.balance.toInt()}")
            }


            on(TestingDB.USERS) {
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
            truncate(TestingDB.BANKS)
            val (res0) = select(TestingDB.BANKS)
            assertEquals(0, res0.size) { "Data was not truncated from Banks" }

            truncate(TestingDB.USERS)
            val (res1) = select(TestingDB.USERS)
            assertEquals(0, res1.size) { "Data was not truncated from Users" }
        }
    }

    /**
     * Drop
     */
    @Test
    internal fun test7() {
        kuery {
            drop(TestingDB.BANKS)
            drop(TestingDB.USERS)

            assertThrows<SQLException> {
                val (res0) = select(TestingDB.BANKS)
            }

            assertThrows<SQLException> {
                val (res1) = select(TestingDB.USERS)
            }
        }
    }
    //endregion


    data class Users(@PrimaryKey @Size(10) val id: String, @Size(50) val name: String, val cityId: Int?)

    data class Cities(@PrimaryKey val id: Int, @Size(50) val name: String)

    @Test
    internal fun jetbrainsExposed() {

        val users = Table.of<Users>()
        val cities = Table.of<Cities>()

        kuery {

            if (USE_OLD.not()) use(TestingDB)

            create(users)
            create(cities)

            insert(cities, Cities(0, "St. Petersburg"))
            val (saintPetersburgId) = selectOne(cities, Cities::id).where(Cities::name) {
                it equals "St. Petersburg"
            }

            insert(cities, Cities(1, "Munich"))
            val (munichId) = selectOne(cities, Cities::id).where(Cities::name) {
                it equals "Munich"
            }


            insert(users, Users("andrey", "Andrey", saintPetersburgId))

            insert(users, Users("sergey", "Sergey", munichId))

            insert(users, Users("eugene", "Eugene", munichId))

            insert(users, Users("alex", "Alex", null))

            insert(users, Users("smth", "Something", null))


            update(users)
                .where(Users::id) { it equals "alex" }
                .set(Users::name, "Alexey")


            delete(users).where(Users::name) { it endsWith "thing" }

            select(cities).forEach {
                println("${it.id}: ${it.name}")
            }


            // no joins

            drop(users)
            drop(cities)
        }
    }


    /**
     * Example
     */
    private fun useConnection() {

        kuery() // connection opened here, closed via timeout

        kuery {
            // connection opened here

            on(TestingDB.USERS) {

            }

        } // connection closed here

    }


    /**
     * These are purely internal, don't try to use this in your code unless you set them as well..
     */
    private companion object {

        private val USE_OLD = false // true points to a pre selected database

        val userName = checkNotNull(getProperty("kuery${if (USE_OLD) ".old" else ""}.user")) // -Dkuery.user
        val passWord = checkNotNull(getProperty("kuery${if (USE_OLD) ".old" else ""}.pass")) // -Dkuery.pass

        val address = checkNotNull(getProperty("kuery${if (USE_OLD) ".old" else ""}.address"))                // -Dkuery.address
        val port = checkNotNull(getProperty("kuery${if (USE_OLD) ".old" else ""}.port", "3306")).toShort() // -Dkuery.port
        val database = checkNotNull(getProperty("kuery${if (USE_OLD) ".old" else ""}.db", ""))                 // -Dkuery.db

    }

}