# Kuery [![Version](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/sxtanna/db/Kuery/maven-metadata.xml.svg?style=for-the-badge)](http://repo1.maven.org/maven2/com/sxtanna/db/Kuery/) [![license](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=for-the-badge)](https://www.apache.org/licenses/LICENSE-2.0)

## How to use Kuery

### Gradle
```groovy
compile "com.sxtanna.db:Kuery:+"
```

### Maven
```xml
<dependency>
    <groupId>com.sxtanna.db</groupId>
    <artifactId>Kuery</artifactId>
    <version>LATEST</version>
</dependency>
```

## How it works

```kotlin

data class User(@PrimaryKey val id : Long, val name : String, @Tiny @Unsigned val age : Int)

val kuery = Kuery(config : KueryConfig)

val table = Table.of<User>()

kuery { // connection to database is opened here

    on(table) {
        insert(Ignore, User(1L, "Sxtanna", 18)) // inserts a new row, "ignoring" duplicate (via update primaryKey=primaryKey)
    }
    
    val (users) = select(table) // destructuring the result of a select call yields its contents
    
    users.forEach { user : User -> // returns entire objects as results
        println("Found user $user")
    }
    
    val (ids, ages) = select(table, User::id, User::age) // destructuring multi select call yields all results
    
    val result = select(table, User::id, User::age) // using the actual resulting Select, you may invoke forEach
    
    result.forEach { id : Long, age : Int -> // a Select forEach provides each result
        println("User with id $id is $age years old")    
    }

} // connection to database is closed here
```

### More examples are in [KueryTest](https://github.com/Sxtanna/KDatabases/blob/master/Kuery/src/test/kotlin/com/sxtanna/db/KueryTest.kt)
