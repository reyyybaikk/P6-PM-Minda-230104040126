# 🧠 Minda — Your Mind, in One Place

> Praktikum Mobile Programming #6 — **Menggunakan Database Lokal (SQLite – Room ORM)**  
> Disusun oleh: **M. Reyhan** (NIM: **230104040126**)  
> Email: **mraihan.app1@gmail.com**  
> Dosen Pengampu: **Muhayat, M.IT**  
> Program Studi Teknologi Informasi – Universitas Islam Negeri Antasari Banjarmasin

---

## 📘 Deskripsi Singkat

**Minda** adalah aplikasi **jurnal harian digital** berbasis **Android (Kotlin + Jetpack Compose)** yang dirancang untuk membantu pengguna mencatat pikiran dan perasaannya secara **pribadi, aman, dan offline**.  
Seluruh data disimpan secara lokal di perangkat menggunakan **Room ORM (SQLite)** dan preferensi pengguna dikelola melalui **DataStore Preferences**.

Aplikasi ini dikembangkan sebagai bagian dari **Praktikum Mobile Programming #6**, dengan fokus pada penerapan **arsitektur modern Android** berbasis **Jetpack Compose** tanpa penggunaan XML legacy.

---

## 🎯 Tujuan Praktikum

1. Menerapkan database lokal menggunakan **Room (SQLite)** untuk operasi CRUD.
2. Menghubungkan **UI (Jetpack Compose)** dengan database melalui **Repository Pattern**.
3. Menerapkan **DataStore Preferences** untuk menyimpan nama pengguna dan status onboarding.
4. Membangun **alur onboarding multi-step** dengan start screen yang bersifat dinamis.
5. Menggunakan **Navigation Compose** untuk berpindah antar layar (Home, Detail, Edit, New).
6. Mengimplementasikan **UI modern dan responsif** sesuai standar **Material 3**.
7. Menjalankan prinsip **Privacy by Design**, di mana seluruh data disimpan secara lokal.
8. Menyusun struktur aplikasi yang **modular, rapi, dan sesuai arsitektur MVVM**.

---

## 🧩 Arsitektur Aplikasi

Aplikasi **Minda** menerapkan pola **MVVM (Model–View–ViewModel)** dengan pemisahan yang jelas antara lapisan **UI**, **ViewModel**, dan **Data**.

- **Model**: Entity Room dan DataStore Preferences
- **View**: Jetpack Compose UI
- **ViewModel**: Mengelola state, business logic, dan komunikasi dengan repository
- **Repository**: Abstraksi akses data dari Room dan DataStore

Pendekatan ini membuat aplikasi lebih **terstruktur, mudah diuji, dan mudah dikembangkan**.

---

## ⚙️ Fitur Aplikasi

### 1. Onboarding Flow
- Multi-step onboarding: **Welcome → Input Nama → Hello → Start Journaling**
- Nama pengguna disimpan menggunakan **DataStore Preferences**
- Onboarding hanya muncul satu kali (dynamic start screen)

### 2. CRUD Lokal (Room ORM)
- **Create**: Menambahkan catatan baru melalui Floating Action Button (FAB)
- **Read**: Menampilkan daftar dan detail catatan
- **Update**: Mengedit catatan yang sudah ada
- **Delete**: Menghapus catatan dengan dialog konfirmasi

### 3. Navigation Compose
- Navigasi sepenuhnya menggunakan **Navigation Compose**
- Tidak menggunakan `Activity` tambahan
- Mendukung parameter navigasi seperti `detail/{entryId}`

### 4. Bottom Navigation Bar
- Empat tab utama: **Home**, **Calendar**, **Insights**, **Settings**
- FAB tetap aktif sebagai aksi utama (tambah catatan)

### 5. DataStore Preferences
- Menyimpan **nama pengguna**
- Menyimpan status **onboarding_completed**
- Seluruh data tetap bersifat lokal dan privat

### 6. Calendar & Insights
- **Calendar**: Menampilkan catatan berdasarkan tanggal dengan indikator
- **Insights**: Statistik jumlah catatan dan mood mingguan

### 7. Settings
- Tampilan pengaturan dengan beberapa seksi:
    - Personal
    - My Data
    - Reminders
    - Other
- Disiapkan sebagai placeholder pengembangan lanjutan

---

## 💡 Arti “Minda”

Kata **Minda** berasal dari bahasa Melayu/Indonesia yang berarti **pikiran** atau **mind**.  
Nama ini dipilih karena aplikasi berfungsi sebagai **ruang pribadi untuk menyimpan pikiran, perasaan, dan refleksi diri**, sesuai dengan tagline:

> _“Your mind, in one place.”_

---

## 🧱 Teknologi yang Digunakan

| Komponen | Fungsi |
|--------|--------|
| **Kotlin 2.x** | Bahasa pemrograman utama |
| **Jetpack Compose** | UI deklaratif modern |
| **Material 3** | Desain UI modern Android |
| **Room ORM (SQLite)** | Database lokal |
| **DataStore Preferences** | Penyimpanan preferensi pengguna |
| **Coroutines** | Proses asynchronous |
| **Navigation Compose** | Navigasi antar layar |
| **Lifecycle ViewModel** | Manajemen state aplikasi |
| **Gradle + KSP** | Build system & annotation processing |

---

## 🖼️ Cuplikan Tampilan (Screenshots)

| Onboarding | Home | Calendar | Insights | Settings |
|-----------|------|----------|----------|----------|
| Welcome → Ask Name → Hello → Start | Daftar catatan + FAB | Catatan per tanggal | Statistik catatan | Pengaturan pengguna |

---

## 📂 Struktur Folder Praktikum


```
.
└── id.antasari.p6minda_230104040126/
├── MainActivity.kt
│
├── data/
│ ├── DiaryEntry.kt
│ ├── DiaryDao.kt
│ ├── DiaryRepository.kt
│ └── UserPrefsRepository.kt
│
└── ui/
├── HomeScreen.kt
├── NewEntryScreen.kt
├── NoteDetailScreen.kt
├── EditEntryScreen.kt
├── OnboardingScreens.kt
├── BottomNav.kt
│
├── calendar/
│ ├── CalendarScreen.kt
│ └── CalendarViewModel.kt
│
└── navigation/
├── AppNavHost.kt
└── Routes.kt
```

---

## 🚀 Cara Menjalankan Aplikasi

1. Buka project di **Android Studio (Narwhal 2025.1.1)**
2. Pastikan **JDK 17** aktif
3. Lakukan **Gradle Sync**
4. Jalankan aplikasi di emulator atau perangkat (API 34+)
5. Pastikan:
  - Onboarding muncul pertama kali
  - Home menampilkan daftar catatan
  - FAB dapat digunakan untuk menambah catatan

---

## 📜 Lisensi

Proyek ini dibuat untuk kepentingan **akademik** sebagai bagian dari  
**Praktikum Mobile Programming** di bawah bimbingan **Muhayat, M.IT**.

Kode bersifat **edukatif** dan tidak digunakan untuk kepentingan komersial.

---

**© 2025 — M. Reyhan**  
_Teknologi Informasi — UIN Antasari Banjarmasin_

