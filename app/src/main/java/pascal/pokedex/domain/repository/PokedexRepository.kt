package pascal.pokedex.domain.repository

import pascal.pokedex.domain.model.Pokemon
import pascal.pokedex.domain.model.PokemonSpecie
import pascal.pokedex.util.Resource
import kotlinx.coroutines.flow.Flow

interface PokedexRepository {
    suspend fun getPokemons(fetchFromRemote: Boolean): Flow<Resource<List<Pokemon>>>
    suspend fun getPokemonSpecie(fetchFromRemote: Boolean, pokemonId: Int): Flow<Resource<PokemonSpecie>>
}