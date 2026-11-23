# P13 — Integrasi Aplikasi Berbasis Menu dan Pengujian Fungsional

**Mata Kuliah**: Praktikum Pemrograman Berorientasi Obyek  
**Pertemuan**: 13  
**Topik**: Integrasi Aplikasi Berbasis Menu dan Pengujian Fungsional  
**Mahasiswa**: Aldo Rachmad Hidayat  
**NRP**: 3125522003  
**Program Studi**: D3 Teknik Informatika — PENS PSDKU Sumenep  

---

## 1. Deskripsi

Pada Pertemuan 13, aplikasi **SiKos (Sistem Informasi Kos)** dan program latihan **Perpustakaan** diintegrasikan menjadi aplikasi konsol interaktif berbasis menu. Fitur-fitur yang sebelumnya diuji secara sekuensial (P10–P12) kini dihubungkan melalui perulangan menu utama dengan proteksi terhadap data yang belum disimpan (*unsaved changes protection*).

Prasyarat praktikum: Collection (P11), Exception Handling (P10), dan File I/O NIO2 (P12).

---

## 2. Prinsip Desain & Pembagian Tanggung Jawab

| Komponen | Tanggung Jawab |
|---|---|
| `Main` | Menampilkan menu, membaca input konsol (`nextLine()`), navigasi, dan proteksi variabel `berubah`. Memisahkan logika menu ke method terpisah (`menuTambah`, `menuCari`, dll) agar `main()` tetap ringkas. |
| `Kamar` / `Buku` | Menjaga validitas data satu unit objek. **Perbaikan Wajib Modul**: Karakter pemisah `\|`, `\n`, dan `\r` divalidasi langsung di constructor saat objek dibentuk, sehingga data invalid ditolak sejak awal. |
| `Kos` / `Perpustakaan` | Mengelola koleksi (`List`) dan menegakkan aturan domain koleksi (seperti keunikan nomor kamar / kode buku). |
| `SiKosRepository` / `simpan()` & `muat()` | Menyimpan dan membaca file menggunakan `java.nio.file` (NIO2) dengan prinsip *rollback* (koleksi lama tetap aman jika file rusak). |

---

## 3. Struktur Folder

```
P13_3125522003_ALDO RACHMAD HIDAYAT/
├── src/
│   ├── DapatDicekStatus.java
│   ├── KamarTidakTersediaException.java
│   ├── PembayaranTidakCukupException.java
│   ├── TagihanBelumLunasException.java
│   ├── Pengguna.java
│   ├── Kamar.java                     # Validasi '|' di constructor
│   ├── Tagihan.java
│   ├── Pembayaran.java
│   ├── Penyewa.java
│   ├── Kos.java                       # Aturan keunikan kamar
│   ├── PemilikKos.java
│   ├── SiKosRepository.java           # File I/O + rollback
│   └── Main.java                      # Menu interaktif & proteksi unsaved changes
├── latihan/
│   └── Main.java                      # Buku + Perpustakaan + Menu (Modul P13)
├── README.md
├── README.pdf
└── UML-SiKos-P13.pdf
```

---

## 4. Fitur Utama & Proteksi Unsaved Changes

Variabel `boolean berubah = false` digunakan untuk memantau status modifikasi:
- Operasi penambahan atau penghapusan berhasil → `berubah = true`.
- Operasi penyimpanan ke file atau pemuatan ulang berhasil → `berubah = false`.
- Operasi gagal (misal validasi ditolak) → status `berubah` sebelumnya dipertahankan.
- Saat memilih **Keluar (0)** atau **Muat Ulang (6)** ketika `berubah == true`, sistem meminta konfirmasi eksplisit:  
  `"Perubahan belum disimpan. Abaikan? Ketik YA: "`  
  Jika pengguna tidak mengetik `YA`, operasi dibatalkan dan pengguna kembali ke menu.

---

## 5. Tabel Pengujian Fungsional

### A. Pengujian Tugas Proyek — `src/Main.java` (SiKos)

| No | Skenario Pengujian | Input | Hasil yang Diharapkan | Status |
|:--:|---|---|---|:--:|
| 1 | Input pilihan menu tidak valid | `99` atau `abc` | Pesan: `Pilihan tidak tersedia. Silakan masukkan nomor menu yang benar (0-7).` Menu ditampilkan kembali. | ✓ |
| 2 | Tambah kamar baru valid | Nomor: `K01`, Tipe: `Single`, Harga: `500000` | SUKSES: Kamar K01 ditambahkan. `berubah = true`. Muncul indikator unsaved changes. | ✓ |
| 3 | Tambah kamar kode duplikat | Nomor: `K01`, Tipe: `Single`, Harga: `500000` | DATA DITOLAK: Nomor kamar K01 sudah terdaftar di Kos Sejahtera. Koleksi tidak bertambah. | ✓ |
| 4 | Tambah kamar dengan karakter pemisah `\|` terlarang | Nomor: `K02\|A` atau Tipe: `VIP\|Lux` | DATA DITOLAK: Mengandung karakter pemisah terlarang '\|'. Ditolak sejak awal saat objek dibentuk. | ✓ |
| 5 | Tampilkan seluruh daftar kamar | Menu `2` | Menampilkan seluruh unit kamar terdaftar beserta status, tipe, dan harga sewanya. | ✓ |
| 6 | Cari kamar terdaftar vs tidak terdaftar | `K01` (ada) dan `K99` (tidak ada) | `K01` ditemukan detailnya; `K99` menghasilkan pesan tidak ditemukan. | ✓ |
| 7 | Hapus kamar yang tidak terdaftar | `K99` | GAGAL: Kamar K99 tidak ditemukan. Koleksi tetap aman. | ✓ |
| 8 | Keluar sebelum simpan, jawab TIDAK | Menu `0` → Jawab: `TIDAK` | Konfirmasi muncul; keluar dibatalkan, aplikasi kembali ke menu utama. | ✓ |
| 9 | Simpan seluruh data kamar ke file | Menu `5` | SUKSES: Data kamar disimpan ke `kamar.txt`. Indikator unsaved direset (`berubah = false`). | ✓ |
| 10 | Tambah kamar K03, lalu Muat Ulang (jawab YA) | Tambah `K03` → Menu `6` → Jawab: `YA` | Pemuatan ulang menimpa memori dengan data file `kamar.txt`. K03 dibatalkan. Total kamar kembali ke 1. | ✓ |
| 11 | Rekapitulasi laporan kos | Menu `7` | Menampilkan laporan pemilik kos, jumlah kamar terdaftar, kamar tersedia, dan potensi pendapatan. | ✓ |
| 12 | Keluar program lalu jalankan kembali (Auto-load) | Menu `0` → Jalankan kembali → Menu `2` | Program otomatis memuat `kamar.txt` di awal eksekusi. Data K01 tetap ada dan utuh. | ✓ |

### B. Pengujian Latihan — `latihan/Main.java` (Perpustakaan)

| No | Skenario Pengujian | Hasil yang Diharapkan | Status |
|:--:|---|---|:--:|
| 1 | Masukkan pilihan menu `abc` | Pesan: `Pilihan tidak tersedia.`; menu muncul kembali. | ✓ |
| 2 | Tambahkan buku berkode baru (`B001` - `Dasar Java`) | Buku tersimpan dalam collection; `berubah = true`. | ✓ |
| 3 | Tambahkan kode yang sama (`B001`) | Ditolak: `Kode buku sudah terdaftar: B001`; data sebelumnya tetap ada. | ✓ |
| 4 | Cari kode yang terdaftar (`B001`) | Judul buku ditampilkan (`Judul: Dasar Java`). | ✓ |
| 5 | Hapus kode yang tidak terdaftar (`B999`) | Pesan: `Buku tidak ditemukan.`; collection tetap. | ✓ |
| 6 | Keluar sebelum menyimpan, lalu jawab `TIDAK` | Ditanya konfirmasi; kembali ke menu utama. | ✓ |
| 7 | Simpan, keluar, lalu jalankan kembali | Data tersimpan dimuat kembali secara otomatis saat program dibuka. | ✓ |
| 8 | Hapus buku, simpan, lalu jalankan kembali | Buku yang telah dihapus tidak muncul kembali saat program dibuka ulang. | ✓ |
| 9 | Tambah buku, pilih muat ulang, lalu jawab `TIDAK` | Pemuatan ulang dibatalkan; perubahan baru tetap tersedia dalam collection. | ✓ |

---

## 6. Pertanyaan Evaluasi & Jawaban (Modul 13)

**1. Mengapa aturan kode unik tetap ditempatkan di Perpustakaan (atau Kos)?**  
> **Jawaban**: Aturan keunikan kode memerlukan pengetahuan tentang seluruh objek yang telah terdaftar di dalam sistem. Objek `Buku` atau `Kamar` individual hanya mengetahui atribut dirinya sendiri dan tidak memegang daftar objek lainnya. Class pengelola (`Perpustakaan` / `Kos`) memegang `List` dari seluruh objek, sehingga class inilah yang memiliki informasi lengkap (*Information Expert*) untuk memeriksa apakah suatu kode sudah terdaftar atau belum sebelum menambahkan objek baru.

**2. Mengapa status `berubah` direset setelah penyimpanan berhasil?**  
> **Jawaban**: Variabel `berubah` berfungsi menandai adanya selisih (inkonsistensi) antara data sementara di memori RAM dan data permanen di file penyimpanan. Setelah operasi penyimpanan berhasil dieksekusi, data di memori telah disinkronkan sepenuhnya ke dalam file fisik, sehingga status modifikasi kembali bersih (`berubah = false`). Hal ini mencegah aplikasi memunculkan peringatan konfirmasi yang tidak perlu saat pengguna ingin keluar.

**3. Apa akibatnya jika status tersebut direset ketika penyimpanan gagal?**  
> **Jawaban**: Jika status `berubah` terlanjur direset menjadi `false` padahal penyimpanan ke file gagal (misalnya karena `IOException`), program akan menganggap data sudah aman di disk. Akibatnya, saat pengguna menutup aplikasi, program tidak akan memberikan peringatan konfirmasi *unsaved changes*, dan seluruh perubahan data baru di memori akan hilang permanen tanpa disadari oleh pengguna.

**4. Mengapa pengujian setiap fitur perlu dilengkapi pengujian alur penggunaan secara utuh?**  
> **Jawaban**: Pengujian per fitur secara terisolasi hanya membuktikan bahwa metode tertentu berfungsi dalam kondisi ideal. Pengujian alur utuh (*end-to-end*) membuktikan bahwa integrasi antar komponen berjalan konsisten dalam skenario nyata: mulai dari penolakan data tidak valid sejak awal pembentukan objek, perlindungan data saat berpindah menu, pencegahan kehilangan data saat keluar, hingga persistensi data yang terbukti dapat dimuat kembali pada eksekusi program berikutnya setelah aplikasi ditutup.

---

## 7. Panduan Menjalankan Program

### Menjalankan Tugas Proyek (SiKos Berbasis Menu)
```bash
cd src
javac -encoding UTF-8 *.java
java -cp . Main
```

### Menjalankan Latihan Modul (Perpustakaan Berbasis Menu)
```bash
cd latihan
javac -encoding UTF-8 Main.java
java -cp . Main
```
