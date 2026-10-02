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

package tuanthanhnguyen.androidmvvmarchitecture.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import tuanthanhnguyen.androidmvvmarchitecture.data.db.dao.PhotoDao
import tuanthanhnguyen.androidmvvmarchitecture.data.db.dao.UserDao
import tuanthanhnguyen.androidmvvmarchitecture.data.db.entity.PhotoEntity
import tuanthanhnguyen.androidmvvmarchitecture.data.db.entity.UserEntity

@Database(
    entities = [
        PhotoEntity::class,
        UserEntity::class],
    version = 1,
    exportSchema = true
)
abstract class PhotoDb : RoomDatabase() {

    abstract fun photoDao(): PhotoDao

    abstract fun userDao(): UserDao
}