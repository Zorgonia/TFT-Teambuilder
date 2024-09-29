package com.kyang.tftteambuilder.data

import android.content.Context
import android.util.Log
import com.kyang.tftteambuilder.data.model.BoardChampion
import com.kyang.tftteambuilder.data.model.BoxModel
import com.kyang.tftteambuilder.data.model.BoxTier
import com.kyang.tftteambuilder.data.model.ChampionCost
import com.kyang.tftteambuilder.data.model.ChampionTrait
import com.kyang.tftteambuilder.data.model.TraitBreakpoint
import com.kyang.tftteambuilder.data.model.TraitTier
import com.kyang.tftteambuilder.util.substringBetween
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.HashMap
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor() {

    private val traitsMap: MutableMap<String, ChampionTrait> = mutableMapOf()

    fun getUnitBox(context: Context): BoxModel {
        loadTraitData(context)
        val data = readFile(context, "units.html", ::parseUnitString)
        val boxModel = createTiers(data)
        return boxModel
    }

    fun loadTraitData(context: Context) {
        val data = readFile(context, "traits.html", ::parseTraitString)

        data.forEach {
            traitsMap[it.name] = it
        }

        Log.d("LocalDataSource", "$traitsMap")
    }

    private fun <T> readFile(
        context: Context,
        fileName: String,
        parseFunction: (String) -> List<T>
    ): List<T> {
        var reader: BufferedReader? = null
        val returnedData = mutableListOf<T>()

        try {
            reader = BufferedReader(
                InputStreamReader(context.assets.open(fileName))
            )

            var line = reader.readLine()
            while (line != null) {
                val currentData = parseFunction(line)
                returnedData.addAll(currentData)
                line = reader.readLine()
            }
        } catch (e: IOException) {
            Log.e("IOException", "$e")
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    Log.e("IOException", "$e")
                }
            }
        }
        return returnedData
    }

    private fun parseUnitString(line: String): List<BoardChampion> {
        val champions = mutableListOf<BoardChampion>()
        var remainingString = line.substringAfter("/info/units/")
        //first couple of calls have some garbage data
        if (!remainingString.contains("/info/units/")) {
            return emptyList()
        }

        while(remainingString.contains("/info/units/")) {
            val parsedData = getDataBetweenDelimiters(remainingString, "</a>")
            champions.add(parseUnitData(parsedData))
            remainingString = remainingString.substringAfter("/info/units/")
        }
        //get last unit
        val parsedData = getDataBetweenDelimiters(remainingString, "</a>")
        champions.add(parseUnitData(parsedData))
        return champions
    }

    private fun getDataBetweenDelimiters(data: String, delimiter: String): List<String> {
        var line = data.substringBefore(delimiter)
        val returnData = mutableListOf<String>()
        while(line.contains(">")) {
            line = line.substringAfter(">")
            val contentBetweenBlocks = line.substringBefore("<")
            if (contentBetweenBlocks.isNotEmpty()) {
                returnData.add(contentBetweenBlocks)
            }
        }
        return returnData
    }

    private fun parseUnitData(data: List<String>): BoardChampion {
        val traits = mutableListOf<ChampionTrait>()
        for (i in 2 until data.size) {
            traitsMap[data.get(i)]?.let {
                traits.add(it)
            }
        }

        val updatedName = parseScrapedName(data[0])
        val champion = BoardChampion(
            traits = traits,
            name = updatedName,
            cost = ChampionCost.entries.firstOrNull {  it.cost == data[1] } ?: ChampionCost.ONE,
            image = getImage(updatedName)
        )
        return champion
    }

    private fun createTiers(data: List<BoardChampion>): BoxModel {
        val tiers = mutableListOf<BoxTier>()
        for (champion in data) {
            val tierIndex = tiers.indexOfFirst { it.cost == champion.cost }
            if (tierIndex == -1) {
                tiers.add(BoxTier(champion.cost, mutableListOf(champion)))
            } else {
                tiers[tierIndex] = BoxTier(champion.cost, tiers[tierIndex].champions.plus(champion))
            }
        }
        return BoxModel(tiers)
    }

    private fun parseTraitString(line: String): List<ChampionTrait> {
        val traits = mutableListOf<ChampionTrait>()
        var remainingString = line.substringAfter("/info/traits/")
        //first couple of calls have some garbage data
        if (!remainingString.contains("/info/traits/")) {
            return emptyList()
        }

        //hack to make arcana work, get (2) (3) (4) (5) into first set of data
        remainingString = remainingString.replaceFirst("</span></div></div>", "")
        while(remainingString.contains("/info/traits/")) {
            val parsedData = getDataBetweenDelimiters(remainingString, "</a>")
            traits.add(parseTraitData(parsedData))
            remainingString = remainingString.substringAfter("/info/traits/")
        }
        //get last trait
        val parsedData = getDataBetweenDelimiters(remainingString, "</a>")
        traits.add(parseTraitData(parsedData))
        return traits
    }

    private fun parseTraitData(data: List<String>): ChampionTrait {
        val updatedBreakpoints = mutableListOf<String>()
        var currentData = ""
        var description = ""
        // drop "("
        for (text in data.drop(1)) {
            if (text.contains("(") && text.contains(")") && text.substringBetween("(", ")").toIntOrNull() != null) {
                if (currentData.isNotEmpty()) {
                    updatedBreakpoints.add(currentData)
                }
                currentData = parseScrapedName(text)
            } else if (currentData.isNotEmpty()) {
                currentData += parseScrapedName(text)
            } else if (!text.contains("(")) {
                description += parseScrapedName(text)
            }
        }
        if (currentData.isNotEmpty() && currentData.contains("(")) {
            updatedBreakpoints.add(currentData)
        }

        return ChampionTrait(
            breakpoints = createBreakpoints(updatedBreakpoints),
            name = data[0],
            image = "https://ap.tft.tools/static/trait-icons/TFT12_${data[0].replace(" ", "")}.svg",
            description = description
        )
    }

    private fun createBreakpoints(lines: MutableList<String>): List<TraitBreakpoint> {
        val breakpoints = mutableListOf<TraitBreakpoint>()
        val breakpointNumbers = lines.map { it.substringBetween("(", ")").toIntOrNull() ?: -1 }
        val tiers = determineTiers(breakpointNumbers, breakpointNumbers.size)
        if (lines.isEmpty()) {
            breakpoints.add(TraitBreakpoint(breakpoint = 1, tier = TraitTier.UNIQUE, subtext = ""))
        }
        for ((index, line) in lines.withIndex()) {
            breakpoints.add(
                TraitBreakpoint(
                    breakpoint = breakpointNumbers[index],
                    tier = tiers[index],
                    subtext = line.substringAfter(")")
                )
            )
        }
        return breakpoints
    }

    private fun determineTiers(breakpointNumbers: List<Int>, size: Int): List<TraitTier> {
        return when {
            size == 1 && breakpointNumbers.first() == 1 -> listOf(TraitTier.UNIQUE)
            size == 2 && breakpointNumbers[1] == 3 -> listOf(TraitTier.SILVER, TraitTier.GOLD)
            size == 2 -> listOf(TraitTier.BRONZE, TraitTier.GOLD)
            size == 3 -> listOf(TraitTier.BRONZE, TraitTier.SILVER, TraitTier.GOLD)
            size == 4  && breakpointNumbers.last() < 10 -> listOf(TraitTier.BRONZE, TraitTier.SILVER, TraitTier.GOLD ,TraitTier.GOLD)
            size == 4 -> listOf(TraitTier.BRONZE, TraitTier.SILVER, TraitTier.GOLD, TraitTier.PRISMATIC)
            size == 0 -> listOf(TraitTier.UNIQUE)
            else -> listOf(TraitTier.NONE)
        }
    }

    private fun parseScrapedName(name: String): String {
        return name.replace("&#x27;", "\'".replace("&amp;", "&"))
    }

    private fun getImage(name: String): String {
        var updatedName = name.replace("'", "").replace(" ", "").lowercase(Locale.getDefault())
        // e.g. norra & yuumi -> norra
        if (name.contains("&")) {
            updatedName = updatedName.split("&").first().trim()
        }
        return "https://ap.tft.tools/img/s12up/face/TFT12_${updatedName}.jpg"
    }
}