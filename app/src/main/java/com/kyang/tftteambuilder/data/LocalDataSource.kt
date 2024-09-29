package com.kyang.tftteambuilder.data

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import com.kyang.tftteambuilder.data.model.BoardChampion
import com.kyang.tftteambuilder.data.model.BoxModel
import com.kyang.tftteambuilder.data.model.BoxTier
import com.kyang.tftteambuilder.data.model.ChampionCost
import com.kyang.tftteambuilder.data.model.ChampionTrait
import dagger.Provides
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor() {

    fun getUnitBox(context: Context): BoxModel {
        val data = readFile(context, "units.html", ::parseUnitString)
        val boxModel = createTiers(data)
        return boxModel
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
            //
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