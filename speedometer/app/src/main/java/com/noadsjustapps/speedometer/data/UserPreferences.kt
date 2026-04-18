package com.noadsjustapps.speedometer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "speedometer_preferences")

class UserPreferences(private val context: Context) {
    
    companion object {
        private val SPEED_UNIT_KEY = stringPreferencesKey("speed_unit")
        private val KEEP_SCREEN_ON_KEY = booleanPreferencesKey("keep_screen_on")
    }

    val speedUnit: Flow<SpeedUnit> = context.dataStore.data.map { preferences ->
        val unitString = preferences[SPEED_UNIT_KEY] ?: SpeedUnit.METRIC.name
        try {
            SpeedUnit.valueOf(unitString)
        } catch (e: IllegalArgumentException) {
            SpeedUnit.METRIC
        }
    }

    val keepScreenOn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEEP_SCREEN_ON_KEY] ?: true
    }

    suspend fun setSpeedUnit(unit: SpeedUnit) {
        context.dataStore.edit { preferences ->
            preferences[SPEED_UNIT_KEY] = unit.name
        }
    }

    suspend fun setKeepScreenOn(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEEP_SCREEN_ON_KEY] = enabled
        }
    }
}
