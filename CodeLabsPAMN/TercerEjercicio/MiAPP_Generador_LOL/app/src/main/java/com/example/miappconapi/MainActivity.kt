package com.example.miappconapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import org.json.JSONObject
import java.io.InputStream
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val championsData = loadJSONFromAsset("champion.json")

        setContent {
            ChampionSelectorApp(championsData)
        }
    }

    private fun loadJSONFromAsset(fileName: String): JSONObject {
        val json: String
        try {
            val inputStream: InputStream = assets.open(fileName)
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
        return JSONObject(json).getJSONObject("data")
    }
}

data class Champion(
    val id: String,
    val name: String,
    val tags: List<String>,
    val partype: String,
    val image: ChampionImage
)

data class ChampionImage(
    val full: String
)

@Composable
fun ChampionSelectorApp(championsData: JSONObject) {
    var selectedChampion by remember { mutableStateOf<Champion?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            selectedChampion = getRandomChampion(championsData)
        }) {
            Text(text = "Generate Random Champion")
        }

        selectedChampion?.let { champion ->
            Spacer(modifier = Modifier.height(16.dp))
            
            Image(
                painter = rememberAsyncImagePainter(model = "file:///android_asset/${champion.image.full}"),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre del campeón
            Text(text = champion.name)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Role: ${getRandomRole()}")
            Text(text = "AD/AP: ${getADorAP(champion.tags)}")

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Runes: ${getRandomRunes()}")
        }
    }
}

fun getRandomChampion(championsData: JSONObject): Champion {
    val keys = championsData.keys().asSequence().toList()
    val randomKey = keys[Random.nextInt(keys.size)]
    val championData = championsData.getJSONObject(randomKey)

    val gson = Gson()
    return gson.fromJson(championData.toString(), Champion::class.java)
}

fun getRandomRole(): String {
    val roles = listOf("Top", "Jungle", "Mid", "ADC", "Support")
    return roles[Random.nextInt(roles.size)]
}

fun getADorAP(tags: List<String>): String {
    return if (tags.contains("Mage") || tags.contains("Assassin")) "AP" else "AD"
}

fun getRandomRunes(): String {
    val runes = listOf("Flash", "Ignite", "Barrier", "Exhaust", "Teleport", "Clear", "Ghost", "Smite")
    var final_value = runes[Random.nextInt(runes.size)];
    final_value +=  ", " + runes[Random.nextInt(runes.size)];
    return final_value;
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ChampionSelectorApp(JSONObject())
}
