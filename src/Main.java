import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Main.java — SiKos P13: Integrasi Aplikasi Berbasis Menu dan Pengujian Fungsional
 *
 * Mengikuti Petunjuk Tugas Proyek Kelompok Modul 13:
 *   1. Menu interaktif berbasis konsol (Tambah, Tampilkan, Cari, Hapus, Simpan, Muat Ulang, Keluar)
 *   2. Validasi domain ditempatkan di class yang bertanggung jawab (Kamar, Kos, dll)
 *   3. Pesan kesalahan deskriptif saat input tidak valid
 *   4. Proteksi unsaved changes (variabel berubah + konfirmasi keluar/muat ulang)
 *   5. Kode menu modular di method terpisah (menuTambah, menuCari, dll) agar main() ringkas
 *   6. Operasi file melalui SiKosRepository dengan auto-rollback
 *
 * Mahasiswa : Aldo Rachmad Hidayat
 * NRP       : 3125522003
 * Prodi     : D3 Teknik Informatika — PENS PSDKU Sumenep
 */
public class Main {

    private static final Path LOKASI_FILE = Paths.get("kamar.txt");
    private static boolean berubah = false;

    // Helper pembaca input konsol yang konsisten menggunakan nextLine()
    public static String baca(Scanner input, String pesan) {
        System.out.print(pesan);
        return input.nextLine().trim();
    }

    // Helper konfirmasi ketika ada perubahan yang belum disimpan
    public static boolean konfirmasi(Scanner input) {
        return baca(
            input,
            "Perubahan belum disimpan. Abaikan? Ketik YA: "
        ).equalsIgnoreCase("YA");
    }

    // ── Method Modular Menu (Poin 5 Tugas Proyek) ─────────────────
    public static void menuTambah(Scanner input, Kos kos) {
        System.out.println("\n--- [1] Tambah Kamar Baru ---");
        String nomorKamar = baca(input, "Nomor Kamar : ");
        String tipe       = baca(input, "Tipe Kamar  (Single/Double/VIP): ");
        String strHarga   = baca(input, "Harga Sewa  (Rp): ");

        double harga;
        try {
            harga = Double.parseDouble(strHarga);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Harga sewa harus berupa angka yang valid.");
        }

        Kamar kamarBaru = new Kamar(nomorKamar, tipe, harga);
        kos.tambahKamar(kamarBaru);
        berubah = true;
        System.out.println("SUKSES: Kamar " + nomorKamar + " berhasil ditambahkan.");
    }

    public static void menuTampilkan(Kos kos) {
        System.out.println("\n--- [2] Daftar Seluruh Kamar ---");
        kos.tampilkanDaftarKamar();
    }

    public static void menuCari(Scanner input, Kos kos) {
        System.out.println("\n--- [3] Cari Kamar ---");
        String nomorKamar = baca(input, "Masukkan Nomor Kamar yang dicari: ");
        Kamar k = kos.cariKamar(nomorKamar);
        if (k != null) {
            System.out.println("DITEMUKAN : " + k);
            System.out.println("  Status  : " + k.getStatus());
            System.out.println("  Tipe    : " + k.getTipe());
            System.out.println("  Harga   : Rp " + (long) k.getHargaSewa() + " / bulan");
        } else {
            System.out.println("HASIL : Kamar " + nomorKamar + " tidak ditemukan.");
        }
    }

    public static void menuHapus(Scanner input, Kos kos) {
        System.out.println("\n--- [4] Hapus Kamar ---");
        String nomorKamar = baca(input, "Masukkan Nomor Kamar yang ingin dihapus: ");
        if (kos.hapusKamar(nomorKamar)) {
            berubah = true;
            System.out.println("SUKSES: Kamar " + nomorKamar + " berhasil dihapus dari sistem.");
        } else {
            System.out.println("GAGAL: Kamar " + nomorKamar + " tidak ditemukan.");
        }
    }

    public static void menuSimpan(Kos kos, Path lokasi) throws IOException {
        System.out.println("\n--- [5] Simpan Data Kamar ---");
        SiKosRepository.simpanKamar(kos.getDaftarKamar(), lokasi);
        berubah = false;
        System.out.println("SUKSES: Seluruh data kamar (" + kos.getDaftarKamar().size() + " unit) berhasil disimpan ke " + lokasi + ".");
    }

    public static void menuMuatUlang(Scanner input, Kos kos, Path lokasi) throws IOException {
        System.out.println("\n--- [6] Muat Ulang Data dari File ---");
        if (berubah && !konfirmasi(input)) {
            System.out.println("Pemuatan ulang dibatalkan. Perubahan saat ini dipertahankan.");
            return;
        }
        SiKosRepository.muatKamar(kos.getDaftarKamar(), lokasi);
        berubah = false;
        System.out.println("SUKSES: Data kamar berhasil dimuat ulang dari " + lokasi + ". Total kamar: " + kos.getDaftarKamar().size());
    }

    public static void menuLaporan(PemilikKos pemilik, Kos kos) {
        System.out.println("\n--- [7] Rekapitulasi Laporan Kos ---");
        pemilik.tampilkanLaporan(kos);
    }

    // ── MAIN METHOD ──────────────────────────────────────────────
    public static void main(String[] args) {
        Kos kos = new Kos("Kos Sejahtera");
        PemilikKos pemilik = new PemilikKos("Haidar", "0812-3456-7890");

        System.out.println("=================================================");
        System.out.println("  SiKos P13 — Integrasi Aplikasi Berbasis Menu   ");
        System.out.println("  Aldo Rachmad Hidayat | NRP: 3125522003          ");
        System.out.println("=================================================");

        // Pemuatan data otomatis di awal program jika file ada
        if (Files.exists(LOKASI_FILE)) {
            try {
                SiKosRepository.muatKamar(kos.getDaftarKamar(), LOKASI_FILE);
                System.out.println("[INFO] Data awal berhasil dimuat (" + kos.getDaftarKamar().size() + " kamar ditemukan).");
            } catch (IOException e) {
                System.out.println("[PERINGATAN] Data awal gagal dimuat: " + e.getMessage());
                System.out.println("[PERINGATAN] Memulai aplikasi dengan koleksi kosong.");
            }
        } else {
            System.out.println("[INFO] File " + LOKASI_FILE + " belum ada. Memulai dengan koleksi baru.");
        }

        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=================================");
                System.out.println("        MENU UTAMA SIKOS         ");
                System.out.println("=================================");
                System.out.println("1. Tambah Kamar    2. Tampilkan Kamar");
                System.out.println("3. Cari Kamar      4. Hapus Kamar");
                System.out.println("5. Simpan Data     6. Muat Ulang Data");
                System.out.println("7. Laporan Kos     0. Keluar");
                System.out.println("---------------------------------");
                if (berubah) {
                    System.out.println("* Ada perubahan data yang belum disimpan!");
                }

                String pilihan = baca(input, "Pilihan Menu: ");

                try {
                    switch (pilihan) {
                        case "1":
                            menuTambah(input, kos);
                            break;
                        case "2":
                            menuTampilkan(kos);
                            break;
                        case "3":
                            menuCari(input, kos);
                            break;
                        case "4":
                            menuHapus(input, kos);
                            break;
                        case "5":
                            menuSimpan(kos, LOKASI_FILE);
                            break;
                        case "6":
                            menuMuatUlang(input, kos, LOKASI_FILE);
                            break;
                        case "7":
                            menuLaporan(pemilik, kos);
                            break;
                        case "0":
                            if (berubah && !konfirmasi(input)) {
                                System.out.println("Keluar dibatalkan. Silakan simpan data terlebih dahulu.");
                                break;
                            }
                            System.out.println("\nTerima kasih! Program SiKos selesai.");
                            return;
                        default:
                            System.out.println("Pilihan tidak tersedia. Silakan masukkan nomor menu yang benar (0-7).");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("DATA DITOLAK: " + e.getMessage());
                } catch (IllegalStateException e) {
                    System.out.println("OPERASI DITOLAK: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("OPERASI FILE GAGAL: " + e.getMessage());
                }
            }
        }
    }
}
