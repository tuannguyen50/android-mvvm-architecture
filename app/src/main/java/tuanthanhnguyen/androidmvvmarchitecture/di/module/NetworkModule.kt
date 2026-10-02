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

import tuanthanhnguyen.androidmvvmarchitecture.data.api.ApiConfig
import tuanthanhnguyen.androidmvvmarchitecture.data.api.PhotoService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import tuanthanhnguyen.androidmvvmarchitecture.data.datasource.remote.PhotoRemoteDataSource
import tuanthanhnguyen.androidmvvmarchitecture.data.datasource.remote.PhotoRemoteDataSourceImpl
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(
            HttpLoggingInterceptor.Level.BODY
        )
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().apply {
            client(okHttpClient)
            baseUrl(ApiConfig.PHOTO_BASE_URL_UNSPLASH)
            addConverterFactory(GsonConverterFactory.create())
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        }.build()

    @Singleton
    @Provides
    fun providePhotoService(retrofit: Retrofit): PhotoService =
        retrofit.create(PhotoService::class.java)

    @Singleton
    @Provides
    fun providePhotoRemoteDataSource(photoService: PhotoService): PhotoRemoteDataSource =
        PhotoRemoteDataSourceImpl(photoService)
}