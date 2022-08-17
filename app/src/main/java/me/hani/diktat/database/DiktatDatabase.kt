package me.hani.diktat.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Word::class, Category::class], version = 1, exportSchema = false)
abstract class DiktatDatabase: RoomDatabase() {

    companion object{
        @Volatile
        private var instance: DiktatDatabase? = null

        fun getDatabaseInstance(context: Context): DiktatDatabase {

            if (instance != null){
                return instance as DiktatDatabase
            }

            synchronized(this){ // this --> DiktatDatabase object

                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    DiktatDatabase::class.java,
                    "diktat_database"
                ).build()
                instance = newInstance

                return newInstance
            }
        }
    }


    abstract fun diktatDao(): DiktatDao
}