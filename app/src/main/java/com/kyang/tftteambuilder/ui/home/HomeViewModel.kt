package com.kyang.tftteambuilder.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyang.tftteambuilder.data.model.ActiveTrait
import com.kyang.tftteambuilder.data.model.BoardChampion
import com.kyang.tftteambuilder.data.model.BoardModel
import com.kyang.tftteambuilder.data.model.ChampionTrait
import com.kyang.tftteambuilder.data.model.EmptyBoardSpace
import com.kyang.tftteambuilder.data.model.TraitModel
import com.kyang.tftteambuilder.repository.DataRepository
import com.kyang.tftteambuilder.util.findHighestOf
import com.kyang.tftteambuilder.util.getEmblemTraitName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            val data = dataRepository.getUnitBox()
            val items = dataRepository.loadItems()
            _uiState.update {
                it.copy(box = data, items = items)
            }
        }
    }

    fun swapChampionView() {
        _uiState.update {
            it.copy(showChampions = !it.showChampions)
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

    fun addItemToChampion(championIndex: Pair<Int, Int>, itemIndex: Int) {
        if (_uiState.value.items.items[itemIndex].name.contains("Emblem")) {
            updateTraitFromItem(championIndex, itemIndex)
        }
        _uiState.update { currState ->
            val toUpdate = currState.board.rows.toMutableList()
            val updateRow = toUpdate[championIndex.first].toMutableList()

            val itemToAdd = currState.items.items[itemIndex]

            val boardSpace = updateRow[championIndex.second]
            if (boardSpace is BoardChampion) {
                //update emblems in separate function becase need knowledge of traits
                if (boardSpace.items.size < 3 && !itemToAdd.name.contains("Emblem")) {
                    updateRow[championIndex.second] =
                        boardSpace.copy(items = boardSpace.items + itemToAdd)
                }
            }
            toUpdate[championIndex.first] = updateRow
            currState.copy(
                board = BoardModel(toUpdate)
            )
        }
    }

    fun removeItemFromChampion(championIndex: Pair<Int, Int>, itemIndex: Int) {
        _uiState.update { currState ->
            val toUpdate = currState.board.rows.toMutableList()
            val updateRow = toUpdate[championIndex.first].toMutableList()

            val space = updateRow[championIndex.second]
            if (space is BoardChampion) {
                val item = space.items[itemIndex]
                if (item.name.contains("Emblem")) {
                    val traits = dataRepository.getTraitData()
                    updateTraitData(
                        traits.filter { it.name == item.name.getEmblemTraitName() },
                        false
                    )
                }

                updateRow[championIndex.second] =
                    space.copy(items = space.items.filterIndexed { index, _ -> index != itemIndex })
            }
            toUpdate[championIndex.first] = updateRow
            currState.copy(
                board = BoardModel(toUpdate)
            )
        }
    }

    private fun updateTraitFromItem(championIndex: Pair<Int, Int>, itemIndex: Int) {
        val currentChampion = _uiState.value.board.rows[championIndex.first][championIndex.second]
        if (currentChampion is BoardChampion && currentChampion.items.size >= 3) {
            return
        }
        val item = _uiState.value.items.items[itemIndex]
        val traitName = item.name.getEmblemTraitName()
        if (currentChampion is BoardChampion && !currentChampion.items.contains(item) && currentChampion.traits.all { it.name != traitName }) {
            viewModelScope.launch {
                val traits = dataRepository.getTraitData()
                traits.firstOrNull {it.name == traitName}?.let { trait ->
                    _uiState.update { currentState ->
                        //add emblem trait to champion traits for removal later
                        val updatedBoard = currentState.board.rows.toMutableList()
                        val updatedRow = updatedBoard[championIndex.first].toMutableList()
                        updatedRow[championIndex.second] = currentChampion.copy(
                            traits = currentChampion.traits + trait,
                            items = currentChampion.items + item
                        )
                        updatedBoard[championIndex.first] = updatedRow
                        currentState.copy(
                            board = BoardModel(updatedBoard)
                        )
                    }
                    updateTraitData(listOf(trait), added = true)
                }
            }
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