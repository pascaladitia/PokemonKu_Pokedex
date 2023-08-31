package pascal.pokedex.data.repository

import pascal.pokedex.data.cache.PokedexDao
import pascal.pokedex.data.remote.PokedexApi
import pascal.pokedex.data.remote.dto.PokemonDetailResponseDto
import pascal.pokedex.data.remote.dto.PokemonSpecieResponseDto
import pascal.pokedex.domain.model.Pokemon
import pascal.pokedex.domain.model.PokemonSpecie
import pascal.pokedex.domain.repository.PokedexRepository
import pascal.pokedex.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PokedexRepositoryImpl @Inject constructor(
    private val pokedexApi: PokedexApi,
    private val pokedexDao: PokedexDao,
) : PokedexRepository {
    override suspend fun getPokemons(fetchFromRemote: Boolean): Flow<Resource<List<Pokemon>>> =
        flow {
            emit(Resource.Loading(true))
            val pokemonDetails: List<PokemonDetailResponseDto>
            try {
                if (fetchFromRemote) {
                    coroutineScope {
                        pokemonDetails = pokedexApi.getPokemons().results.map { pokemon ->
                            async { pokedexApi.getPokemonDetail(pokemon.name) }
                        }.awaitAll()
                    }
                    if (pokemonDetails.isNotEmpty()) {
                        pokedexDao.insertPokemons(pokemons = pokemonDetails.map { it.toPokemonEntity() })
                        pokemonDetails.forEach { pokeDetail ->
                            pokedexDao.insertPokemonStats(pokemonStats = pokeDetail.stats.map {
                                it.toStatEntity(pokeDetail.id)
                            })
                            pokedexDao.insertPokemonTypes(pokemonTypes = pokeDetail.types.map {
                                it.toTypeEntity(pokeDetail.id)
                            })
                        }
                    }
                }
                emit(Resource.Success(pokedexDao.getPokemonWithStatsAndTypes()
                    .map { it.toPokemon() }))
            } catch (e: Exception) {
                emit(Resource.Error("An unexpected error has occurred.", emptyList()))
            }
        }

    override suspend fun getPokemonSpecie(fetchFromRemote: Boolean, pokemonId: Int): Flow<Resource<PokemonSpecie>> =
        flow {
            emit(Resource.Loading(true))
            val pokemonSpecie: PokemonSpecieResponseDto
            try {
                if (fetchFromRemote) {
                    pokemonSpecie = pokedexApi.getPokemonSpecie(pokemonId = pokemonId)
                    pokedexDao.insertPokemonSpecie(
                        specie = pokemonSpecie.toPokemonSpecieEntity(pokemonId))
                }
                emit(Resource.Success(pokedexDao.getPokemonSpecie(pokemonId = pokemonId).toPokemonSpecie(pokemonId = pokemonId)))
            } catch (e: Exception) {
                emit(Resource.Error("An unexpected error has occurred."))
            }
        }
}