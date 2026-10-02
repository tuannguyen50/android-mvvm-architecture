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

package tuanthanhnguyen.androidmvvmarchitecture.data.db.entity.mapper

import tuanthanhnguyen.androidmvvmarchitecture.data.db.entity.PhotoEntity
import tuanthanhnguyen.androidmvvmarchitecture.data.db.entity.PhotoEntityWithUserEntity
import tuanthanhnguyen.androidmvvmarchitecture.data.db.entity.UserEntity
import tuanthanhnguyen.androidmvvmarchitecture.data.model.PhotoDataModel
import tuanthanhnguyen.androidmvvmarchitecture.util.date.DateUtil

object PhotoEntityMapper {

    fun photoDataModelToPhotoEntity(photoDataModel: PhotoDataModel): PhotoEntity {
        return PhotoEntity(
            photoDataModel.id,
            photoDataModel.createdAt,
            photoDataModel.updatedAt,
            PhotoUrlsEmbeddedMapper.photoUrlsDataModelToPhotoUrlsEmbedded(
                photoDataModel.photoUrlsDataModel
            ),
            photoDataModel.userDataModel.id
        )
    }

    fun photoEntityWithUserEntityToPhotoDataModel(
        photoEntity: PhotoEntity,
        userEntity: UserEntity
    ): PhotoDataModel {
        return PhotoDataModel(
            photoEntity.id,
            photoEntity.createdAt,
            photoEntity.updatedAt,
            PhotoUrlsEmbeddedMapper.photoUrlsEmbeddedToPhotoUrlsDataModel(
                photoEntity.photoUrlsEmbedded
            ),
            UserEntityMapper.userEntityToUserDataModel(userEntity)
        )
    }

    fun photoEntityWithUserEntityListToPhotoDataModels(
        photoEntityWithUserEntityList: List<PhotoEntityWithUserEntity>
    ): List<PhotoDataModel> {
        return photoEntityWithUserEntityList.map {
            return@map photoEntityWithUserEntityToPhotoDataModel(it.photoEntity, it.userEntity)
        }
    }
}