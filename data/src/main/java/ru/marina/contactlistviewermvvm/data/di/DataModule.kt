package ru.marina.contactlistviewermvvm.data.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.marina.contactlistviewermvvm.BuildConfig
import ru.marina.contactlistviewermvvm.data.db.AppDb
import ru.marina.contactlistviewermvvm.data.db.dao.ContactsDao
import ru.marina.contactlistviewermvvm.data.executor.SchedulersProviderImpl
import ru.marina.contactlistviewermvvm.data.mapper.ContactsMapper
import ru.marina.contactlistviewermvvm.data.mapper.ContactsMapperImpl
import ru.marina.contactlistviewermvvm.data.network.ErrorInterceptor
import ru.marina.contactlistviewermvvm.data.network.api.ApiService
import ru.marina.contactlistviewermvvm.data.prefs.Prefs
import ru.marina.contactlistviewermvvm.data.prefs.PrefsImpl
import ru.marina.contactlistviewermvvm.data.remote.ContactsRemote
import ru.marina.contactlistviewermvvm.data.remote.ContactsRemoteImpl
import ru.marina.contactlistviewermvvm.data.repository.ContactsRepositoryImpl
import ru.marina.contactlistviewermvvm.domain.executor.SchedulersProvider
import ru.marina.contactlistviewermvvm.domain.repository.ContactsRepository
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class DataModule {

    @Singleton
    @Provides
    fun provideDb(app: Application): AppDb =
        Room
            .databaseBuilder(app, AppDb::class.java, "app.db")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideContactsDao(appDb: AppDb): ContactsDao = appDb.contactsDao()

    @Singleton
    @Provides
    fun provideApiService(okHttpClient: OkHttpClient): ApiService =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideOkHttpClient(app: Application): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(ErrorInterceptor(app))
            .connectTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    fun provideContactsRemote(
        service: ApiService,
        contactsMapper: ContactsMapper
    ): ContactsRemote {
        return ContactsRemoteImpl(service, contactsMapper)
    }

    @Provides
    fun provideContactsMapper(): ContactsMapper {
        return ContactsMapperImpl()
    }

    @Singleton
    @Provides
    fun provideContactsRepository(
        contactsRemote: ContactsRemote,
        contactsDao: ContactsDao,
        contactsMapper: ContactsMapper,
        prefs: Prefs
    ): ContactsRepository {
        return ContactsRepositoryImpl(contactsRemote, contactsDao, contactsMapper, prefs)
    }

    @Singleton
    @Provides
    fun providesPrefs(app: Application): Prefs = PrefsImpl(app)

    @Singleton
    @Provides
    fun provideSchedulersProvider(): SchedulersProvider = SchedulersProviderImpl()
}