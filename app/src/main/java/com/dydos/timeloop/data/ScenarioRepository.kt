package com.dydos.timeloop.data

import android.content.Context
import kotlinx.serialization.json.Json

class ScenarioRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    fun loadScenario(): ScenarioConfig {
        val content = context.assets.open("scenario.json").bufferedReader().use { it.readText() }
        return json.decodeFromString<ScenarioConfig>(content)
    }
}
