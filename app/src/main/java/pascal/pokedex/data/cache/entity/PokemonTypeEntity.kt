package pascal.pokedex.data.cache.entity


import androidx.room.Entity
import pascal.pokedex.domain.model.Type
import pascal.pokedex.domain.model.Types

@Entity(tableName = "tbPokemonType", primaryKeys = ["typeName", "pokemonId"])
data class PokemonTypeEntity(
    val typeName: String,
    val pokemonId: Int,
    val slot: Int,
) {
    fun toPokemonTypes() = Types(
        slot = slot,
        type = Type(name = typeName)
    )
}