package com.android.greenmate.domain.repository

import com.android.greenmate.data.datasource.local.entity.MyPlantEntity
import com.android.greenmate.domain.model.MyPlant
import kotlinx.coroutines.flow.Flow

interface MyPlantRepository {
    suspend fun insertMyPlant(myPlant: MyPlant)
    suspend fun insertAndSetFavorite(myPlant: MyPlant)
    suspend fun getFavoriteMyPlant(): MyPlant?
    suspend fun deleteMyPlantByMyPlantId(myPlantId: Long)
    suspend fun updateMyPlantAlias(alias: String, myPlantId: Long)
    suspend fun updateMyPlantImage(image: String, myPlantId: Long)
    suspend fun updateAndSetFavorite(favorite: Boolean, myPlantId: Long)
//    suspend fun updateMyPlantRoom(roomId: Long, myPlantId: Long)
    suspend fun getMyPlantById(myPlantId: Long): MyPlant?
    suspend fun getAllMyPlants(): List<MyPlant>
//    suspend fun getMyPlantsByRoomId(roomId: Long): List<MyPlant>
}