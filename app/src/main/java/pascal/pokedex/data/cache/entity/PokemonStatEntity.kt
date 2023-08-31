package pascal.pokedex.data.cache.entity


import androidx.room.Entity
import pascal.pokedex.domain.model.Stats
import pascal.pokedex.domain.model.StatsDetail

@Entity(tableName = "tbPokemonStat", primaryKeys = ["statDetailName", "pokemonId"])
data class PokemonStatEntity(
    val pokemonId: Int,
    val baseStat: Int,
    val effort: Int,
    val statDetailName: String,
    val statDetailUrl: String,
) {
    fun toPokemonStatus() = Stats(
        baseStat = baseStat,
        effort = effort,
        stat = StatsDetail(statDetailName, statDetailUrl)
    )
}