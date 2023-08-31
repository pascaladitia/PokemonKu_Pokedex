package pascal.pokedex.data.remote

import pascal.pokedex.data.remote.dto.PokemonDetailResponseDto
import pascal.pokedex.data.remote.dto.PokemonResponseDto
import pascal.pokedex.data.remote.dto.PokemonSpecieResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PokedexApi {
    @GET("pokemon")
    suspend fun getPokemons(): PokemonResponseDto

    @GET("pokemon/{pokemonName}")
    suspend fun getPokemonDetail(
        @Path("pokemonName") pokemonName: String
    ): PokemonDetailResponseDto

    @GET("pokemon-species/{pokemonId}")
    suspend fun getPokemonSpecie(
        @Path("pokemonId") pokemonId: Int
    ): PokemonSpecieResponseDto

    companion object {
        const val API_BASE_URL = "https://pokeapi.co/api/v2/"
    }
}