package com.viwath.srulibrarymobile.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.viwath.srulibrarymobile.common.constant.Constant
import com.viwath.srulibrarymobile.data.api.AuthApi
import com.viwath.srulibrarymobile.data.api.AuthInterceptor
import com.viwath.srulibrarymobile.data.api.RemoteApi
import com.viwath.srulibrarymobile.data.repository.AuthRepositoryImp
import com.viwath.srulibrarymobile.data.repository.CoreRepositoryImp
import com.viwath.srulibrarymobile.utils.TokenManager
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.AuthUseCase
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.AuthenticateUseCase
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.RefreshTokenUseCase
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.RegisterUseCase
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.SigninUseCase
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.CheckExitingUseCase
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.EntryUseCase
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.GetRecentEntryUseCase
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.GetStudentByIDUseCase
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.SaveAttendUseCase
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.UpdateExitingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Inject API
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient() // Allows some leniency with JSON parsing
        .create()
    //// Auth API
    @Provides
    @Singleton
    fun provideRemoteApi(okHttpClient: OkHttpClient, gson: Gson): RemoteApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Constant.BASE_URL)
            .client(okHttpClient)
            .build()
            .create(RemoteApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(): AuthApi {
        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
    ///////////// OK Http client
    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    // Inject Auth Repository
    @Provides
    @Singleton
    fun provideAuthRepository(authApi: AuthApi, tokenManager: TokenManager): AuthRepository {
        return AuthRepositoryImp(authApi, tokenManager)
    }

    // Inject Core Repository
    @Provides
    @Singleton
    fun provideCoreRepository(api: RemoteApi): CoreRepository {
        return CoreRepositoryImp(api)
    }

    // Auth interceptor
    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager, authUseCase: Provider<AuthUseCase>): AuthInterceptor {
        return AuthInterceptor(tokenManager, lazy { authUseCase.get() })
    }


    /// Qr scan use case
    @Provides
    @Singleton
    fun provideQrUseCase(repository: CoreRepository): EntryUseCase {
        return EntryUseCase(
            GetStudentByIDUseCase(repository),
            SaveAttendUseCase(repository),
            GetRecentEntryUseCase(repository),
            UpdateExitingUseCase(repository),
            CheckExitingUseCase(repository)
        )
    }

    // provide Auth use case
    @Provides
    @Singleton
    fun provideAuthUseCase(repository: AuthRepository): AuthUseCase {
        return AuthUseCase(
            RegisterUseCase(repository),
            SigninUseCase(repository),
            AuthenticateUseCase(repository),
            RefreshTokenUseCase(repository)
        )
    }

    // token manger
    @Provides
    @Singleton
    fun provideTokenManger(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }
}