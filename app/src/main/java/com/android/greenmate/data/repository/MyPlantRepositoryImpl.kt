package com.android.greenmate.data.repository

import com.android.greenmate.data.datasource.local.dao.MyPlantDao
import com.android.greenmate.data.datasource.local.entity.MyPlantEntity
import com.android.greenmate.data.mapper.toDomainModel
import com.android.greenmate.data.mapper.toEntity
import com.android.greenmate.domain.model.MyPlant
import com.android.greenmate.domain.repository.MyPlantRepository


class MyPlantRepositoryImpl(private val myPlantDao: MyPlantDao) : MyPlantRepository {
    override suspend fun insertMyPlant(myPlant: MyPlant) {
        myPlantDao.insertMyPlant(myPlant.toEntity())
    }

    override suspend fun insertAndSetFavorite(myPlant: MyPlant) {
        myPlantDao.insertAndSetFavorite(myPlant.toEntity())
    }

    override suspend fun getFavoriteMyPlant(): MyPlant? {
        return myPlantDao.getFavoriteMyPlant()?.toDomainModel()
    }

    override suspend fun deleteMyPlantByMyPlantId(myPlantId: Long) {
        myPlantDao.deleteMyPlantByMyPlantId(myPlantId)
    }

    override suspend fun updateMyPlantAlias(alias: String, myPlantId: Long) {
        myPlantDao.updateMyPlantAlias(alias, myPlantId)
    }

    override suspend fun updateMyPlantImage(image: String, myPlantId: Long) {
        myPlantDao.updateMyPlantImage(image, myPlantId)
    }

    override suspend fun updateAndSetFavorite(favorite: Boolean, myPlantId: Long) {
        myPlantDao.updateAndSetFavorite(favorite, myPlantId)
    }

//    override suspend fun updateMyPlantRoom(roomId: Long, myPlantId: Long) {
//        myPlantDao.updateMyPlantRoom(roomId, myPlantId)
//    }

    override suspend fun getMyPlantById(myPlantId: Long): MyPlant? {
        return myPlantDao.getMyPlantById(myPlantId)?.toDomainModel()
    }

    override suspend fun getAllMyPlants(): List<MyPlant> {
        return myPlantDao.getAllMyPlants().map { it.toDomainModel() }
    }

//    override suspend fun getMyPlantsByRoomId(roomId: Long): List<MyPlant> {
//        return  myPlantDao.getMyPlantsByRoomId(roomId).map { it.toDomainModel() }
//    }
}