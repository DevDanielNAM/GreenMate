package com.android.greenmate.di


import android.content.Context
import androidx.room.Room
import com.android.greenmate.data.ble.BleManager
import com.android.greenmate.data.datasource.local.AppDatabase
import com.android.greenmate.data.datasource.local.InferenceLocalDataSource
import com.android.greenmate.data.datasource.local.dao.AlarmDao
import com.android.greenmate.data.datasource.local.dao.ModuleDao
import com.android.greenmate.data.datasource.local.dao.MyPlantDao
import com.android.greenmate.data.datasource.local.dao.PlantDao
import com.android.greenmate.data.datasource.local.dao.RecordDao
import com.android.greenmate.data.repository.AlarmRepositoryImpl
import com.android.greenmate.domain.repository.ImageInferenceRepository
import com.android.greenmate.data.repository.ModuleRepositoryImpl
import com.android.greenmate.data.repository.MyPlantRepositoryImpl
import com.android.greenmate.data.repository.PlantRepositoryImpl
import com.android.greenmate.data.repository.ImageInferenceRepositoryImpl
import com.android.greenmate.data.repository.RecordRepositoryImpl
import com.android.greenmate.domain.repository.AlarmRepository
import com.android.greenmate.domain.repository.ModuleRepository
import com.android.greenmate.domain.repository.MyPlantRepository
import com.android.greenmate.domain.repository.PlantRepository
import com.android.greenmate.domain.repository.RecordRepository
import com.android.greenmate.domain.usecase.RunDiseaseInferenceUseCase
import com.android.greenmate.domain.usecase.RunInferenceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideInferenceLocalDataSource(@ApplicationContext context: Context): InferenceLocalDataSource {
        return InferenceLocalDataSource(context)
    }

    @Provides
    @Singleton
    fun provideImageInferenceRepository(
        remoteDataSource: InferenceLocalDataSource
    ): ImageInferenceRepository {
        return ImageInferenceRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideRunInferenceUseCase(
        repository: ImageInferenceRepository
    ): RunInferenceUseCase {
        return RunInferenceUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRunDiseaseInferenceUseCase(
        repository: ImageInferenceRepository
    ): RunDiseaseInferenceUseCase {
        return RunDiseaseInferenceUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideBleManager(@ApplicationContext context: Context): BleManager {
        return BleManager(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "greenmate_database"
        ).addCallback(AppDatabase.Companion.PlantDatabaseCallback(context))
        .build()
    }

    @Provides
    fun provideModuleDao(appDatabase: AppDatabase): ModuleDao {
        return appDatabase.moduleDao()
    }

    @Provides
    fun provideModuleRepository(moduleDao: ModuleDao): ModuleRepository {
        return ModuleRepositoryImpl(moduleDao)
    }

    @Provides
    fun providePlantDao(appDatabase: AppDatabase): PlantDao {
        return appDatabase.plantDao()
    }

    @Provides
    fun providePlantRepository(plantDao: PlantDao): PlantRepository {
        return PlantRepositoryImpl(plantDao)
    }

    @Provides
    fun provideMyPlantDao(appDatabase: AppDatabase): MyPlantDao {
        return appDatabase.myPlantDao()
    }

    @Provides
    fun provideMyPlantRepository(myPlantDao: MyPlantDao): MyPlantRepository {
        return MyPlantRepositoryImpl(myPlantDao)
    }

    @Provides
    fun provideAlarmDao(appDatabase: AppDatabase): AlarmDao {
        return appDatabase.alarmDao()
    }

    @Provides
    fun provideAlarmRepository(alarmDao: AlarmDao): AlarmRepository {
        return AlarmRepositoryImpl(alarmDao)
    }

    @Provides
    fun provideRecordDao(appDatabase: AppDatabase): RecordDao {
        return appDatabase.recordDao()
    }

    @Provides
    fun provideRecordRepository(recordDao: RecordDao): RecordRepository {
        return RecordRepositoryImpl(recordDao)
    }
}
