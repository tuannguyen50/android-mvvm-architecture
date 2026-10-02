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

package tuanthanhnguyen.androidmvvmarchitecture.di.module

import android.app.Application
import androidx.room.Room
import tuanthanhnguyen.androidmvvmarchitecture.data.datasource.local.PhotoLocalDataSource
import tuanthanhnguyen.androidmvvmarchitecture.data.datasource.local.PhotoLocalDataSourceImpl
import tuanthanhnguyen.androidmvvmarchitecture.data.db.DatabaseConfig
import tuanthanhnguyen.androidmvvmarchitecture.data.db.PhotoDb
import tuanthanhnguyen.androidmvvmarchitecture.data.datasource.local.UserLocalDataSource
import tuanthanhnguyen.androidmvvmarchitecture.data.datasource.local.UserLocalDataSourceImpl
import tuanthanhnguyen.androidmvvmarchitecture.data.db.dao.PhotoDao
import tuanthanhnguyen.androidmvvmarchitecture.data.db.dao.UserDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providePhotoDb(application: Application): PhotoDb =
        Room.databaseBuilder(
            application,
            PhotoDb::class.java,
            DatabaseConfig.DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun providePhotoDao(photoDb: PhotoDb): PhotoDao = photoDb.photoDao()

    @Singleton
    @Provides
    fun provideUserDao(photoDb: PhotoDb): UserDao = photoDb.userDao()

    @Singleton
    @Provides
    fun providePhotoLocalDataSource(
        photoDb: PhotoDb,
        photoDao: PhotoDao
    ): PhotoLocalDataSource =
        PhotoLocalDataSourceImpl(photoDb, photoDao)

    @Singleton
    @Provides
    fun provideUserLocalDataSource(photoDb: PhotoDb, userDao: UserDao): UserLocalDataSource =
        UserLocalDataSourceImpl(photoDb, userDao)
}