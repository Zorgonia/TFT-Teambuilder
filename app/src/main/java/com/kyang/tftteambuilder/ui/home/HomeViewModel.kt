package com.kyang.tftteambuilder.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyang.tftteambuilder.data.model.ActiveTrait
import com.kyang.tftteambuilder.data.model.BoardChampion
import com.kyang.tftteambuilder.data.model.BoardModel
import com.kyang.tftteambuilder.data.model.ChampionTrait
import com.kyang.tftteambuilder.data.model.EmptyBoardSpace
import com.kyang.tftteambuilder.data.model.TraitModel
import com.kyang.tftteambuilder.repository.UnitRepository
import com.kyang.tftteambuilder.util.findHighestOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    fun loadData(context: Context) {
        viewModelScope.launch {
            val data = unitRepository.getUnitBox(context)
            _uiState.update {
                it.copy(box = data)
            }
        }
    }

    fun addChampion(champion: BoardChampion) {
        updateTraits(champion, true)
        _uiState.update {
            val toUpdate = it.board.rows.toMutableList()
            for (i in 0 until toUpdate.size) {
                val firstEmptyIndex = toUpdate[i].indexOfFirst { it is EmptyBoardSpace }
                if (firstEmptyIndex > -1) {
                    val temp = toUpdate[i].toMutableList()
                    temp[firstEmptyIndex] = champion
                    toUpdate[i] = temp
                    break
                }
            }

            it.copy(
                board = BoardModel(rows = toUpdate)
            )
        }
    }

    fun swapSpaces(index: Pair<Int, Int>) {
        if (_uiState.value.swapIndex == Pair(-1, -1)) {
            _uiState.update {
                it.copy(swapIndex = index)
            }
        } else {
            swapChampions(_uiState.value.swapIndex, index)
        }
    }

    private fun swapChampions(from: Pair<Int, Int>, to: Pair<Int, Int>) {
        _uiState.update {
            val toUpdate = it.board.rows.toMutableList()

            val space = toUpdate[to.first][to.second]

            //update destination with from
            val updatedDest = toUpdate[to.first].toMutableList()
            updatedDest[to.second] = toUpdate[from.first][from.second]
            toUpdate[to.first] = updatedDest

            //update start with to
            val updatedFrom = toUpdate[from.first].toMutableList()
            updatedFrom[from.second] = space
            toUpdate[from.first] = updatedFrom

            it.copy(
                board = BoardModel(toUpdate), swapIndex = Pair(-1, -1)
            )
        }
    }

    fun addFromBox(boxIndex: Pair<Int, Int>, boardIndex: Pair<Int, Int>) {
        updateTraits(uiState.value.box.tiers[boxIndex.first].champions[boxIndex.second], true)
        _uiState.update {
            val toUpdate = it.board.rows.toMutableList()

            val updatedDest = toUpdate[boardIndex.first].toMutableList()
            updatedDest[boardIndex.second] = it.box.tiers[boxIndex.first].champions[boxIndex.second]
            toUpdate[boardIndex.first] = updatedDest

            it.copy(
                board = BoardModel(toUpdate)
            )

        }
    }

    fun removeChampion(index: Pair<Int, Int>) {
        val space = _uiState.value.board.rows[index.first][index.second]
        if (space is BoardChampion) {
            updateTraits(space, added = false)
        }
        _uiState.update {
            val toUpdate = it.board.rows.toMutableList()

            val updatedDest = toUpdate[index.first].toMutableList()
            updatedDest[index.second] = EmptyBoardSpace
            toUpdate[index.first] = updatedDest

            it.copy(
                board = BoardModel(toUpdate)
            )
        }
    }

    private fun updateTraits(champion: BoardChampion, added: Boolean) {
        val current = _uiState.value
        if (added && current.board.rows.all { row -> row.all { space -> champion != space } }) {
            updateTraitData(champion.traits, added = true)
        } else if (!added && current.board.rows.sumOf { row -> row.count { space -> space == champion } } == 1) {
            updateTraitData(champion.traits, added = false)
        }
    }

    private fun updateTraitData(traits: List<ChampionTrait>, added: Boolean) {
        val current = _uiState.value
        _uiState.update { state ->
            val currentTraits = current.traits.traits.toMutableList()

            for (trait in traits) {
                val indexOfTrait = currentTraits.indexOfFirst { it.trait.name == trait.name }
                if (indexOfTrait > -1) {
                    val diff = if (added) 1 else -1
                    val newNumUnits = currentTraits[indexOfTrait].numOfUnits + diff
                    val highestBreakpoint =
                        currentTraits[indexOfTrait].trait.breakpoints.map { it.breakpoint }
                            .findHighestOf(newNumUnits)
                    currentTraits[indexOfTrait] = currentTraits[indexOfTrait].copy(
                        breakpoint = trait.breakpoints.firstOrNull { it.breakpoint == highestBreakpoint },
                        numOfUnits = newNumUnits
                    )

                } else if (added) {
                    val firstBreakpoint = trait.breakpoints.first()
                    currentTraits.add(
                        ActiveTrait(
                            trait = trait,
                            numOfUnits = 1,
                            breakpoint = if (firstBreakpoint.breakpoint == 1) firstBreakpoint else null
                        )
                    )
                }
                currentTraits.removeAll { it.numOfUnits == 0 }
            }

            state.copy(
                traits = TraitModel(
                    currentTraits.sortedWith(compareByDescending<ActiveTrait> { it.breakpoint?.tier?.compareValue }
                        .thenByDescending { it.numOfUnits }
                        .thenBy { it.trait.name })
                )
            )
        }

    }
}