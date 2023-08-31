package pascal.pokedex.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pascal.pokedex.data.cache.PokedexDao
import pascal.pokedex.data.cache.PokedexDatabase
import pascal.pokedex.data.remote.PokedexApi
import pascal.pokedex.data.remote.PokedexApi.Companion.API_BASE_URL
import pascal.pokedex.data.repository.PokedexRepositoryImpl
import pascal.pokedex.domain.repository.PokedexRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PokedexModule {
    @Provides
    @Singleton
    fun providePokedexApi(): PokedexApi {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build().create(PokedexApi::class.java)
    }

    @Provides
    @Singleton
    fun providePokedexRepository(
        pokedexApi: PokedexApi,
        pokedexDao: PokedexDao,
    ): PokedexRepository = PokedexRepositoryImpl(
        pokedexApi = pokedexApi,
        pokedexDao = pokedexDao
    )

    @Provides
    @Singleton
    fun providePokedexDAO(pokedexDb: PokedexDatabase) = pokedexDb.pokedexDao

    @Provides
    @Singleton
    fun providePokedexDatabase(app: Application) =
        Room.databaseBuilder(app, PokedexDatabase::class.java, "dbPokedex").build()
}

