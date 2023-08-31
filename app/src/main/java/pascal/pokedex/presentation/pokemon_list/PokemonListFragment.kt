package pascal.pokedex.presentation.pokemon_list

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import pascal.pokedex.R
import pascal.pokedex.databinding.FragmentPokemonListBinding
import pascal.pokedex.presentation.adapter.PokemonListAdapter
import pascal.pokedex.util.PokemonUtil
import pascal.pokedex.util.PokemonUtil.createSnackBar
import pascal.pokedex.util.PokemonUtil.safeNavigate
import pascal.pokedex.util.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pascal.pokedex.domain.model.Pokemon

@AndroidEntryPoint
class PokemonListFragment : Fragment(R.layout.fragment_pokemon_list) {
    private lateinit var pokemonListBinding: FragmentPokemonListBinding
    private val pokemonListViewModel: PokemonListViewModel by viewModels()
    private lateinit var pokemonListAdapter: PokemonListAdapter
    private var listPokemon = listOf<Pokemon>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pokemonListBinding = FragmentPokemonListBinding.bind(view)
        createAdapterAndItemClick()
        getPokemons(PokemonUtil.isNetworkAvailable(requireActivity()))
        searchPokemons()
        pokemonListBinding.apply {
            rvPokemon.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pokemonListAdapter
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pokemonListViewModel.pokemons.collectLatest { pokemonState ->
                    when (pokemonState) {
                        is Resource.Error -> {
                            pokemonListBinding.apply {
                                pokemonListBinding.apply {
                                    rvPokemon.visibility = VISIBLE
                                    pbLoading.visibility = GONE
                                }
                                root.createSnackBar(
                                    message = pokemonState.message ?: ""
                                )
                            }
                        }
                        is Resource.Loading -> {
                            pokemonListBinding.apply {
                                rvPokemon.visibility = INVISIBLE
                                pbLoading.visibility = VISIBLE
                            }
                        }
                        is Resource.Success -> {
                            pokemonListBinding.apply {
                                rvPokemon.visibility = VISIBLE
                                pbLoading.visibility = GONE
                            }
                            listPokemon = pokemonState.data!!
                            pokemonListAdapter.submitList(pokemonState.data)
                        }
                    }
                }
            }
        }
    }

    private fun getPokemons(fetchFromRemote: Boolean) {
        pokemonListViewModel.onEvent(PokemonListEvent.GetPokemons(fetchFromRemote))
    }

    private fun createAdapterAndItemClick() {
        pokemonListAdapter = PokemonListAdapter(
            onItemClick = { pokemon ->
                findNavController().safeNavigate(
                    PokemonListFragmentDirections.actionPokemonListFragmentToPokemonDetailFragment(pokemon)
                )
            }
        )
    }

    private fun searchPokemons() {
        pokemonListBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = listPokemon.filter { it.name.contains(newText ?: "", true) }
                displaySearchResults(filteredList)
                return true
            }
        })
    }

    private fun displaySearchResults(results: List<Pokemon>) {
        Log.e("tag result", results.toString())
        if (results.isNotEmpty()) {
            pokemonListAdapter.notifyDataSetChanged()
            pokemonListAdapter.submitList(results)
        }
    }
}