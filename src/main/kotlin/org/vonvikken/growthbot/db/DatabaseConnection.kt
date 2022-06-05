package org.vonvikken.growthbot.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

private const val DB_FILE_PATH: String = "growth.db"

internal object DatabaseConnection {
    val db by lazy {
        Database.connect("jdbc:sqlite:$DB_FILE_PATH", "org.sqlite.JDBC")
    }

    init {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        TransactionManager.defaultDatabase = db

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Baby, Length, Weight)
        }
    }
}
