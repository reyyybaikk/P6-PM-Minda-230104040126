// Package sudah disesuaikan dengan ID Anda
package id.antasari.p6minda_nimanda.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Ganti nama variabel 'dataStore' menjadi 'userPrefsDataStore' agar lebih jelas
// (Sesuai dengan Langkah 4.1 di modul Anda)
private val Context.userPrefsDataStore by preferencesDataStore(name = "user_prefs")

class UserPrefsRepository(private val context: Context) {

    companion object {
        private val KEY_USER_NAME: Preferences.Key<String> =
            stringPreferencesKey("user_name")

        // FLAG BARU: onboarding selesai
        private val KEY_ONBOARD_DONE: Preferences.Key<Boolean> =
            booleanPreferencesKey("onboarding_completed")
    }

    // ==== Expose Flows ====
    // PERBAIKAN: Tambahkan .data
    val userNameFlow: Flow<String?> = context.userPrefsDataStore.data
        .map { prefs ->
            prefs[KEY_USER_NAME]
        }

    // PERBAIKAN: Tambahkan .data
    val onboardingCompletedFlow: Flow<Boolean> = context.userPrefsDataStore.data
        .map { prefs ->
            prefs[KEY_ONBOARD_DONE] ?: false
        }

    // ==== Write APIs ====
    suspend fun saveUserName(name: String) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = name
        }
    }

    suspend fun setOnboardingCompleted(done: Boolean) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[KEY_ONBOARD_DONE] = done
        }
    }

    // (opsional) reset untuk debugging
    suspend fun clearAll() {
        context.userPrefsDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}