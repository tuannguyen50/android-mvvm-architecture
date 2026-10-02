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

// Tuan Thanh Nguyen modified this file.

package tuanthanhnguyen.androidmvvmarchitecture.data.repository

import tuanthanhnguyen.androidmvvmarchitecture.data.api.response.mapper.PhotoResponseMapper
import tuanthanhnguyen.androidmvvmarchitecture.data.datasource.local.PhotoLocalDataSource
import tuanthanhnguyen.androidmvvmarchitecture.data.model.PhotoDataModel
import io.reactivex.Single
import tuanthanhnguyen.androidmvvmarchitecture.data.datasource.local.UserLocalDataSource
import tuanthanhnguyen.androidmvvmarchitecture.data.datasource.remote.PhotoRemoteDataSource
import tuanthanhnguyen.androidmvvmarchitecture.data.db.PhotoDb
import tuanthanhnguyen.androidmvvmarchitecture.data.db.entity.mapper.PhotoEntityMapper
import tuanthanhnguyen.androidmvvmarchitecture.data.db.entity.mapper.UserEntityMapper
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val photoDb: PhotoDb,
    private val photoRemoteDataSource: PhotoRemoteDataSource,
    private val photoLocalDataSource: PhotoLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : PhotoRepository {

    override fun getPhotosWithOfflineFallback(page: Int): Single<List<PhotoDataModel>> {
        return photoRemoteDataSource.getPhotosPageNumber(page).map {
            return@map PhotoResponseMapper.photoResponsesToPhotoDataModels(it)
        }.doOnSuccess { photoDataModels ->
            photoDb.runInTransaction {
                photoDataModels.forEach { photoDataModel ->
                    userLocalDataSource.upsertUserEntity(
                        UserEntityMapper.userDataModelToUserEntity(photoDataModel.userDataModel)
                    )
                    photoLocalDataSource.upsertPhotoEntity(
                        PhotoEntityMapper.photoDataModelToPhotoEntity(photoDataModel)
                    )
                }
            }
        }.onErrorResumeNext { throwable ->
            if (throwable is java.net.UnknownHostException) {
                return@onErrorResumeNext photoLocalDataSource
                    .getPhotoEntityWithUserEntityListPage(page)
                    .map {
                        return@map PhotoEntityMapper
                            .photoEntityWithUserEntityListToPhotoDataModels(it)
                    }
                    .flatMap {
                        if (it.isNotEmpty()) {
                            Single.just(it)
                        } else {
                            Single.error(throwable)
                        }
                    }
            }
            return@onErrorResumeNext Single.error(throwable)
        }
    }
}