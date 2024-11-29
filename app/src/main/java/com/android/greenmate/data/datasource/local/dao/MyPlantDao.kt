package com.android.greenmate.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.android.greenmate.data.datasource.local.entity.MyPlantEntity

@Dao
interface MyPlantDao {
    @Insert
    suspend fun insertMyPlant(myPlant: MyPlantEntity)

    @Query("UPDATE myplant SET favorite = 0 WHERE favorite = 1")
    suspend fun updateFavoritesToFalse()

    @Transaction
    suspend fun insertAndSetFavorite(myPlant: MyPlantEntity) {
        updateFavoritesToFalse()  // 기존 favorite을 모두 false로 변경
        insertMyPlant(myPlant) //myPlant.copy(favorite = true))  // 새로운 myPlant를 favorite true로 삽입
    }

    @Query("SELECT * FROM myplant WHERE favorite = 1")
    suspend fun getFavoriteMyPlant(): MyPlantEntity?

    @Query("DELETE FROM myplant WHERE myPlantId =:myPlantId")
    suspend fun deleteMyPlantByMyPlantId(myPlantId: Long)

    @Query("UPDATE myplant SET alias =:alias  WHERE myPlantId =:myPlantId")
    suspend fun updateMyPlantAlias(alias: String, myPlantId: Long)

    @Query("UPDATE myplant SET image =:image  WHERE myPlantId =:myPlantId")
    suspend fun updateMyPlantImage(image: String, myPlantId: Long)

    @Query("UPDATE myplant SET favorite =:favorite  WHERE myPlantId =:myPlantId")
    suspend fun updateMyPlantFavorite(favorite: Boolean, myPlantId: Long)

    @Transaction
    suspend fun updateAndSetFavorite(favorite: Boolean, myPlantId: Long) {
        updateFavoritesToFalse()  // 기존 favorite을 모두 false로 변경
        updateMyPlantFavorite(favorite, myPlantId)
    }

    @Query("SELECT * FROM myplant WHERE myPlantId = :myPlantId")
    suspend fun getMyPlantById(myPlantId: Long): MyPlantEntity?

    @Query("SELECT * FROM myplant")
    suspend fun getAllMyPlants(): List<MyPlantEntity>
}