package pascal.pokedex.presentation.pokemon_details.evolution

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import pascal.pokedex.R
import pascal.pokedex.databinding.FragmentPokemonDetailEvolutionBinding
import pascal.pokedex.domain.model.Pokemon

class PokemonDetailEvolutionFragment(pokemonDetails: Pokemon) : Fragment(R.layout.fragment_pokemon_detail_evolution) {
    private lateinit var mainBinding: FragmentPokemonDetailEvolutionBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainBinding = FragmentPokemonDetailEvolutionBinding.bind(view)
    }

}