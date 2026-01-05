package id.antasari.p6minda_nimanda.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import id.antasari.p6minda_nimanda.data.DiaryEntry
// Tidak perlu DiaryRepository lagi
// import id.antasari.p6minda_nimanda.data.DiaryRepository
import id.antasari.p6minda_nimanda.data.MindaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TestRoomScreen() {
    val context = LocalContext.current

    // --- PERBAIKAN 1: Gunakan DAO secara langsung ---
    val dao = remember { MindaDatabase.getInstance(context).diaryDao() }
    // val repo = remember { DiaryRepository(db.diaryDao()) }
    // ----------------------------------------------

    val scope = rememberCoroutineScope()

    var lastInsertedId by remember { mutableStateOf<Int?>(null) }
    var fetchedEntry by remember { mutableStateOf<DiaryEntry?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tombol Insert
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    val newEntry = DiaryEntry(
                        id = 0,
                        title = "Test Insert",
                        content = "This is a test entry from TestRoomScreen",
                        mood = "😊",
                        timestamp = System.currentTimeMillis()
                    )

                    // --- PERBAIKAN 2: Panggil dao.insert ---
                    val newId = dao.insert(newEntry) // returns Long
                    // ------------------------------------

                    withContext(Dispatchers.Main) {
                        lastInsertedId = newId.toInt()
                    }
                }
            }
        ) {
            Text("Insert Sample Entry")
        }

        // Tombol Load data terakhir
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    val idToLoad = lastInsertedId

                    // --- PERBAIKAN 3: Panggil dao.getById ---
                    val loaded = if (idToLoad != null) {
                        dao.getById(idToLoad)
                    } else null
                    // ------------------------------------

                    withContext(Dispatchers.Main) {
                        fetchedEntry = loaded
                    }
                }
            },
            enabled = lastInsertedId != null
        ) {
            Text("Load Last Inserted Entry")
        }

        // Menampilkan ID terakhir
        Text(
            text = "LastInsertedId: ${lastInsertedId ?: "-"}"
        )

        // Menampilkan hasil fetch dari DB
        Text(
            text = buildString {
                append("FetchedEntry:\n")
                val e = fetchedEntry
                if (e != null) {
                    append("id=${e.id}\n")
                    append("title=${e.title}\n")
                    append("mood=${e.mood}\n")
                    append("content=${e.content.take(50)} ...\n")
                } else {
                    append("null")
                }
            }
        )
    }
}