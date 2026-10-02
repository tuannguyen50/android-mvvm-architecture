/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Tuan Thanh Nguyen created this file.

package tuanthanhnguyen.androidmvvmarchitecture.data.datasource.local

import androidx.room.RoomDatabase
import tuanthanhnguyen.androidmvvmarchitecture.data.api.ApiConfig
import tuanthanhnguyen.androidmvvmarchitecture.data.db.dao.PhotoDao
import tuanthanhnguyen.androidmvvmarchitecture.data.db.entity.PhotoEntity
import io.reactivex.Single
import tuanthanhnguyen.androidmvvmarchitecture.data.db.entity.PhotoEntityWithUserEntity
import javax.inject.Inject

class PhotoLocalDataSourceImpl @Inject constructor(
    private val roomDatabase: RoomDatabase,
    private val photoDao: PhotoDao
) : PhotoLocalDataSource {

    override fun getPhotoEntityWithUserEntityListPage(
        page: Int
    ): Single<List<PhotoEntityWithUserEntity>> {
        return photoDao.getPhotoEntityWithUserEntityListPage(
            ApiConfig.THIS_APPLICATION_DEFAULT_PER_PAGE,
            (page - 1) * ApiConfig.THIS_APPLICATION_DEFAULT_PER_PAGE
        )
    }

    override fun upsertPhotoEntity(photoEntity: PhotoEntity) {
        return photoDao.upsertPhotoEntity(photoEntity)
    }
}