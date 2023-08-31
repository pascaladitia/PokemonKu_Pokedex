package pascal.pokedex.data.remote.dto


import com.google.gson.annotations.SerializedName
import pascal.pokedex.domain.model.Type

data class TypeDto(
    @SerializedName("name")
    val name: String,
) {
    fun toType() = Type(
        name = name
    )
}