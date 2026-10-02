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

// Tuan Thanh Nguyen refactored this file.

package tuanthanhnguyen.androidmvvmarchitecture.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import tuanthanhnguyen.androidmvvmarchitecture.data.db.DatabaseConfig

@Entity(tableName = DatabaseConfig.PHOTOS_TABLE_NAME)
data class PhotoEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @Embedded(prefix = "url_") val photoUrlsEmbedded: PhotoUrlsEmbedded,
    @ColumnInfo(name = "user_id") val userId: String
)