package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.models.BloodDonorEntry
import com.example.models.IssueReportEntry
import com.example.models.ServiceEntry

@Database(
    entities = [
        ServiceEntry::class,
        BloodDonorEntry::class,
        IssueReportEntry::class
    ],
    version = 2,
    exportSchema = false
)
abstract class JamalpurDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao
    abstract fun bloodDonorDao(): BloodDonorDao
    abstract fun issueReportDao(): IssueReportDao

    companion object {
        @Volatile
        private var INSTANCE: JamalpurDatabase? = null

        fun getDatabase(context: Context): JamalpurDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JamalpurDatabase::class.java,
                    "jamalpur_sheba_database.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
