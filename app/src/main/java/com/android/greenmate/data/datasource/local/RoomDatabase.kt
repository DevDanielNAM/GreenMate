package com.android.greenmate.data.datasource.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.greenmate.data.datasource.local.converters.DateConverter
import com.android.greenmate.data.datasource.local.converters.ListConverter
import com.android.greenmate.data.datasource.local.dao.*
import com.android.greenmate.data.datasource.local.entity.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader

@Database(
    entities = [PlantEntity::class, DiseaseEntity::class, MyPlantEntity::class, ModuleEntity::class, RecordEntity::class, AlarmEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun myPlantDao(): MyPlantDao
    abstract fun moduleDao(): ModuleDao
    abstract fun recordDao(): RecordDao
    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "greenmate_database"
                ).addCallback(PlantDatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        class PlantDatabaseCallback(
            private val context: Context
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d("PlantDatabaseCallback", "onCreate called")
                // Populate the database in the background.
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(context)
                }
            }

            private suspend fun populateDatabase(context: Context) {
                val plantDao = getDatabase(context).plantDao()
                val jsonString = context.assets.open("greenmate_plants_database.json").bufferedReader().use { it.readText() }
                val gson = Gson()

                val plantMapType = object : TypeToken<Map<String, Map<String, Any>>>() {}.type
                val plantMap: Map<String, Map<String, Any>> = gson.fromJson(jsonString, plantMapType)

                plantMap.forEach { (_, value) ->
                    // PlantEntity 생성을 위해 diseases 필드 제외
                    val plantValue = value.filterKeys { it != "diseases" }
                    val plant = gson.fromJson(gson.toJson(plantValue), PlantEntity::class.java)
                    val plantId = plantDao.insertPlant(plant)

                    // DiseaseEntity 생성
                    @Suppress("UNCHECKED_CAST")
                    val diseases = (value["diseases"] as? List<List<String>>)?.map { disease ->
                        DiseaseEntity(
                            plantId = plantId,
                            title = disease[0],
                            descriptions = disease.drop(1)  // 첫 번째 요소(제목)를 제외한 나머지를 설명으로 사용
                        )
                    } ?: emptyList()

                    if (diseases.isNotEmpty()) {
                        plantDao.insertDiseases(diseases)
                    }
                }
            }
        }
    }
}