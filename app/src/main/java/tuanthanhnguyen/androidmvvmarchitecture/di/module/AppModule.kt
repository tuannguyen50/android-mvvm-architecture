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
import android.content.Context
import tuanthanhnguyen.androidmvvmarchitecture.data.datasource.local.PhotoLocalDataSource
import tuanthanhnguyen.androidmvvmarchitecture.data.repository.PhotoRepositoryImpl
import tuanthanhnguyen.androidmvvmarchitecture.data.repository.PhotoRepository
import tuanthanhnguyen.androidmvvmarchitecture.util.scheduler.AppSchedulerProvider
import tuanthanhnguyen.androidmvvmarchitecture.util.scheduler.SchedulerProvider
import dagger.Module
import dagger.Provides
import tuanthanhnguyen.androidmvvmarchitecture.data.datasource.local.UserLocalDataSource
import tuanthanhnguyen.androidmvvmarchitecture.data.datasource.remote.PhotoRemoteDataSource
import tuanthanhnguyen.androidmvvmarchitecture.data.db.PhotoDb
import javax.inject.Singleton

@Module
object AppModule {

    @Singleton
    @Provides
    @JvmStatic
    fun provideContext(application: Application): Context = application

    @Singleton
    @Provides
    @JvmStatic
    fun provideSchedulerProvider(): SchedulerProvider = AppSchedulerProvider()

    @Singleton
    @Provides
    @JvmStatic
    fun providePhotoRepository(
        photoDb: PhotoDb,
        photoRemoteDataSource: PhotoRemoteDataSource,
        photoLocalDataSource: PhotoLocalDataSource,
        userLocalDataSource: UserLocalDataSource
    ): PhotoRepository =
        PhotoRepositoryImpl(
            photoDb,
            photoRemoteDataSource,
            photoLocalDataSource,
            userLocalDataSource
        )
}