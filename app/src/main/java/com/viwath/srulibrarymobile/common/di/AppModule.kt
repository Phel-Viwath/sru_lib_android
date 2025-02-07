/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.common.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.viwath.srulibrarymobile.common.constant.Constant
import com.viwath.srulibrarymobile.data.api.AuthApi
import com.viwath.srulibrarymobile.data.api.AuthInterceptor
import com.viwath.srulibrarymobile.data.api.CoreApi
import com.viwath.srulibrarymobile.data.repository.AuthRepositoryImp
import com.viwath.srulibrarymobile.data.repository.CoreRepositoryImp
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.AuthUseCase
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.AuthenticateUseCase
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.ChangePasswordUseCase
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.RefreshTokenUseCase
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.RegisterUseCase
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.RequestOtpUseCase
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.SigninUseCase
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.VerifyOtpUseCase
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.AddBookUseCase
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.BookUseCase
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.GetBookInTrashUseCase
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.GetBooksUseCase
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.GetCollegeUseCase
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.GetLanguageUseCase
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.GetSummaryUseCase
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.RecoverBookUseCase
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.RemoveBookUseCase
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.SearchBookUseCase
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.UpdateBookUseCase
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.UploadBookUseCase
import com.viwath.srulibrarymobile.domain.usecase.borrow_usecase.BorrowBookUseCase
import com.viwath.srulibrarymobile.domain.usecase.borrow_usecase.BorrowUseCase
import com.viwath.srulibrarymobile.domain.usecase.borrow_usecase.ExtendBorrowUseCase
import com.viwath.srulibrarymobile.domain.usecase.borrow_usecase.GetActiveBorrowsDetailUseCase
import com.viwath.srulibrarymobile.domain.usecase.borrow_usecase.GetAllBorrowUseCase
import com.viwath.srulibrarymobile.domain.usecase.borrow_usecase.ReturnBookUseCase
import com.viwath.srulibrarymobile.domain.usecase.borrow_usecase.SearchBorrowUseCase
import com.viwath.srulibrarymobile.domain.usecase.donation_usecase.AddDonationUseCase
import com.viwath.srulibrarymobile.domain.usecase.donation_usecase.DonationUseCase
import com.viwath.srulibrarymobile.domain.usecase.donation_usecase.GetAllDonationUseCase
import com.viwath.srulibrarymobile.domain.usecase.donation_usecase.UpdateDonationUseCase
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.CheckExitingUseCase
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.EntryUseCase
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.GetRecentEntryUseCase
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.GetStudentByIDUseCase
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.SaveAttendUseCase
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.UpdateExitingUseCase
import com.viwath.srulibrarymobile.utils.TokenManager
import com.viwath.srulibrarymobile.utils.connectivity.ConnectivityObserver
import com.viwath.srulibrarymobile.utils.connectivity.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Main dependency injection module for the SRU Library Mobile application.
 * This module provides singleton instances of various components used throughout the application.
 *
 * @Module Indicates this is a Dagger module
 * @InstallIn(SingletonComponent::class) Specifies that these dependencies live for the entire application lifecycle
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Network & API Related Dependencies
     */

    /**
     * Provides a configured Gson instance for JSON parsing
     * @return Gson instance with lenient parsing enabled
     */
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient() // Allows some leniency with JSON parsing
        .create()

    /**
     * Provides the main API interface for core application features
     * @param okHttpClient Configured OkHttpClient with authentication interceptor
     * @param gson Gson instance for JSON parsing
     * @return CoreApi implementation
     */
    @Provides
    @Singleton
    fun provideRemoteApi(okHttpClient: OkHttpClient, gson: Gson): CoreApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Constant.BASE_URL)
            .client(okHttpClient)
            .build()
            .create(CoreApi::class.java)
    }

    /**
     * Provides the authentication API interface
     * Configured with extended timeouts (240 seconds) for handling lengthy authentication operations
     * @return AuthApi implementation
     */
    @Provides
    @Singleton
    fun provideAuthApi(): AuthApi {
        val authOkHttpClient = OkHttpClient.Builder()
            .connectTimeout(240, TimeUnit.SECONDS)
            .readTimeout(240, TimeUnit.SECONDS)
            .writeTimeout(240, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(authOkHttpClient)
            .build()
            .create(AuthApi::class.java)
    }

    /**
     * Provides configured OkHttpClient with authentication interceptor and extended timeouts
     * @param authInterceptor Interceptor for handling authentication
     * @return Configured OkHttpClient
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(240, TimeUnit.SECONDS)
            .readTimeout(240, TimeUnit.SECONDS)
            .writeTimeout(240, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Repository Dependencies
     */

    /**
     * Provides the authentication repository implementation
     * @param authApi Authentication API interface
     * @param tokenManager Manager for handling authentication tokens
     * @return AuthRepository implementation
     */
    @Provides
    @Singleton
    fun provideAuthRepository(authApi: AuthApi, tokenManager: TokenManager): AuthRepository {
        return AuthRepositoryImp(authApi, tokenManager)
    }


    /**
     * Provides the core repository implementation
     * @param api Core API interface
     * @return CoreRepository implementation
     */
    @Provides
    @Singleton
    fun provideCoreRepository(api: CoreApi): CoreRepository {
        return CoreRepositoryImp(api)
    }


    /**
     * Use Case Dependencies
     */

    /**
     * Provides entry/QR scanning related use cases
     * Handles student attendance and entry/exit management
     * @param repository Core repository instance
     * @return EntryUseCase containing all entry-related use cases
     */
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

    /**
     * Provides authentication related use cases
     * Handles user registration, signin, and token management
     * @param repository Auth repository instance
     * @return AuthUseCase containing all authentication-related use cases
     */
    @Provides
    @Singleton
    fun provideAuthUseCase(repository: AuthRepository): AuthUseCase {
        return AuthUseCase(
            RegisterUseCase(repository),
            SigninUseCase(repository),
            AuthenticateUseCase(repository),
            RefreshTokenUseCase(repository),
            RequestOtpUseCase(repository),
            VerifyOtpUseCase(repository),
            ChangePasswordUseCase(repository)
        )
    }


    /**
     * Provides book management related use cases
     * Handles CRUD operations for books, including trash management
     * @param repository Core repository instance
     * @return BookUseCase containing all book-related use cases
     */
    @Provides
    @Singleton
    fun provideBookUseCase(repository: CoreRepository): BookUseCase{
        return BookUseCase(
            AddBookUseCase(repository),
            GetBooksUseCase(repository),
            UpdateBookUseCase(repository),
            RemoveBookUseCase(repository),
            RecoverBookUseCase(repository),
            GetBookInTrashUseCase(repository),
            GetSummaryUseCase(repository),
            GetLanguageUseCase(repository),
            GetCollegeUseCase(repository),
            UploadBookUseCase(repository),
            GetStudentByIDUseCase(repository),
            SearchBookUseCase(repository)
        )
    }

    /**
     * Provides book borrowing related use cases
     * Handles borrowing, returning, and extending book loans
     * @param repository Core repository instance
     * @return BorrowUseCase containing all borrowing-related use cases
     */
    @Provides
    @Singleton
    fun provideBorrowUseCase(
        repository: CoreRepository
    ): BorrowUseCase = BorrowUseCase(
        GetAllBorrowUseCase(repository),
        BorrowBookUseCase(repository),
        GetActiveBorrowsDetailUseCase(repository),
        SearchBorrowUseCase(repository),
        ExtendBorrowUseCase(repository),
        ReturnBookUseCase(repository)
    )

    /**
     * Provides book donation related use cases
     * Handles donation management and tracking
     * @param repository Core repository instance
     * @return DonationUseCase containing all donation-related use cases
     */
    @Provides
    @Singleton
    fun provideDonationUseCase(repository: CoreRepository): DonationUseCase{
        return DonationUseCase(
            GetAllDonationUseCase(repository),
            AddDonationUseCase(repository),
            UpdateDonationUseCase(repository)
        )
    }

    /**
     * Utility Dependencies
     */

    /**
     * Provides network connectivity observer
     * Monitors the application's network state
     * @param context Application context
     * @return ConnectivityObserver implementation
     */
    @Provides
    @Singleton
    fun provideNetworkObserver(@ApplicationContext context: Context): ConnectivityObserver{
        return NetworkConnectivityObserver(context)
    }


    /**
     * Provides the authentication interceptor for HTTP requests
     * The interceptor is responsible for adding authentication tokens to requests and handling token refresh
     *
     * @param tokenManager Manager for retrieving and storing authentication tokens
     * @param authUseCase Provider of authentication use cases, lazily initialized to avoid circular dependencies
     * @return AuthInterceptor instance
     *
     * Note: Uses Provider<AuthUseCase> with lazy initialization to prevent circular dependency issues
     * that could occur between AuthInterceptor and AuthUseCase
     */
    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager, authUseCase: Provider<AuthUseCase>): AuthInterceptor {
        return AuthInterceptor(tokenManager, lazy { authUseCase.get() })
    }


    /**
     * Provides the token manager responsible for handling authentication tokens
     * The token manager handles storing and retrieving tokens securely using Android's context
     *
     * @param context Application context needed for accessing secure storage
     * @return TokenManager instance
     *
     * Note: Uses @ApplicationContext to ensure the correct context is injected for token storage
     */
    @Provides
    @Singleton
    fun provideTokenManger(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

}