import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Latihan P13 — Integrasi Aplikasi Berbasis Menu dan Pengujian Fungsional
 *
 * Mengikuti Modul Praktikum P13:
 *   - Class Buku: Validasi satu buku (kode & judul tidak boleh null/blank, tidak boleh mengandung | atau \n/\r)
 *   - Class Perpustakaan: Mengelola koleksi buku, keunikan kode, dan persistensi file
 *   - Class Main: Antarmuka berbasis menu konsol dengan proteksi unsaved changes (variabel berubah)
 *
 * Mahasiswa : Aldo Rachmad Hidayat
 * NRP       : 3125522003
 */

// ── Kelas Buku ──────────────────────────────────────────────────────────────
class Buku {
    private String kode;
    private String judul;

    public Buku(String kode, String judul) {
        if (kode == null || kode.isBlank()) {
            throw new IllegalArgumentException("Kode dan judul wajib diisi.");
        }
        if (judul == null || judul.isBlank()) {
            throw new IllegalArgumentException("Kode dan judul wajib diisi.");
        }
        if (kode.contains("|") || kode.contains("\n") || kode.contains("\r")) {
            throw new IllegalArgumentException("Kode/judul mengandung pemisah terlarang.");
        }
        if (judul.contains("|") || judul.contains("\n") || judul.contains("\r")) {
            throw new IllegalArgumentException("Kode/judul mengandung pemisah terlarang.");
        }
        this.kode = kode.trim();
        this.judul = judul.trim();
    }

    public String getKode() {
        return kode;
    }

    public String getJudul() {
        return judul;
    }

    @Override
    public String toString() {
        return kode + " - " + judul;
    }
}

// ── Kelas Perpustakaan ───────────────────────────────────────────────────────
class Perpustakaan {
    private List<Buku> daftarBuku = new ArrayList<>();

    public void tambahBuku(Buku buku) {
        if (buku == null) {
            throw new IllegalArgumentException("Objek buku tidak boleh null.");
        }
        for (Buku b : daftarBuku) {
            if (b.getKode().equalsIgnoreCase(buku.getKode())) {
                throw new IllegalArgumentException("Kode buku sudah terdaftar: " + buku.getKode());
            }
        }
        daftarBuku.add(buku);
    }

    public void tampilkanBuku() {
        if (daftarBuku.isEmpty()) {
            System.out.println("Koleksi masih kosong.");
            return;
        }
        for (Buku b : daftarBuku) {
            System.out.println(b);
        }
        System.out.println("Total buku: " + daftarBuku.size());
    }

    public Buku cariBuku(String kode) {
        for (Buku b : daftarBuku) {
            if (b.getKode().equalsIgnoreCase(kode)) {
                return b;
            }
        }
        return null;
    }

    public boolean hapusBuku(String kode) {
        Buku b = cariBuku(kode);
        if (b == null) {
            return false;
        }
        return daftarBuku.remove(b);
    }

    public int jumlahBuku() {
        return daftarBuku.size();
    }

    public void simpan(Path lokasi) throws IOException {
        List<String> baris = new ArrayList<>();
        for (Buku buku : daftarBuku) {
            String kode = buku.getKode();
            String judul = buku.getJudul();
            if (kode.contains("|") || judul.contains("|")) {
                throw new IllegalArgumentException("Kode/judul mengandung pemisah terlarang.");
            }
            baris.add(kode + "|" + judul);
        }
        Files.write(lokasi, baris, StandardCharsets.UTF_8);
    }

    public void muat(Path lokasi) throws IOException {
        List<String> baris = Files.readAllLines(lokasi, StandardCharsets.UTF_8);
        Perpustakaan sementara = new Perpustakaan();
        for (int i = 0; i < baris.size(); i++) {
            String b = baris.get(i);
            if (b.isBlank()) continue;
            String[] bagian = b.split("\\|", 2);
            if (bagian.length != 2) {
                throw new IOException("Format salah pada baris " + (i + 1));
            }
            try {
                sementara.tambahBuku(new Buku(bagian[0], bagian[1]));
            } catch (IllegalArgumentException e) {
                throw new IOException("Data tidak valid pada baris " + (i + 1) + ": " + e.getMessage(), e);
            }
        }
        daftarBuku.clear();
        daftarBuku.addAll(sementara.daftarBuku);
    }
}

// ── Kelas Main (Aplikasi Konsol Berbasis Menu) ───────────────────────────────
public class Main {
    private static String baca(Scanner input, String pesan) {
        System.out.print(pesan);
        return input.nextLine().trim();
    }

    private static boolean konfirmasi(Scanner input) {
        return baca(
            input,
            "Perubahan belum disimpan. Abaikan? Ketik YA: "
        ).equalsIgnoreCase("YA");
    }

    public static void main(String[] args) {
        Perpustakaan perpustakaan = new Perpustakaan();
        Path lokasi = Paths.get("buku.txt");
        boolean berubah = false;

        // Muat data lama sebelum pengguna mulai bekerja.
        if (Files.exists(lokasi)) {
            try {
                perpustakaan.muat(lokasi);
            } catch (IOException e) {
                System.out.println("Data awal gagal dimuat: " + e.getMessage());
                return;
            }
        }

        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== PERPUSTAKAAN ===");
                System.out.println("1. Tambah       2. Tampilkan");
                System.out.println("3. Cari         4. Hapus");
                System.out.println("5. Simpan       6. Muat ulang");
                System.out.println("0. Keluar");

                String pilihan = baca(input, "Pilihan: ");

                try {
                    switch (pilihan) {
                        case "1": {
                            String kode = baca(input, "Kode: ");
                            String judul = baca(input, "Judul: ");
                            perpustakaan.tambahBuku(new Buku(kode, judul));
                            berubah = true;
                            System.out.println("Buku ditambahkan.");
                            break;
                        }
                        case "2":
                            perpustakaan.tampilkanBuku();
                            break;
                        case "3": {
                            Buku hasil = perpustakaan.cariBuku(
                                baca(input, "Kode yang dicari: ")
                            );
                            System.out.println(
                                hasil == null
                                    ? "Buku tidak ditemukan."
                                    : "Judul: " + hasil.getJudul()
                            );
                            break;
                        }
                        case "4": {
                            String kode = baca(input, "Kode yang dihapus: ");
                            if (perpustakaan.hapusBuku(kode)) {
                                berubah = true;
                                System.out.println("Buku dihapus.");
                            } else {
                                System.out.println("Buku tidak ditemukan.");
                            }
                            break;
                        }
                        case "5":
                            perpustakaan.simpan(lokasi);
                            berubah = false;
                            System.out.println("Data disimpan.");
                            break;
                        case "6":
                            if (berubah && !konfirmasi(input)) {
                                break;
                            }
                            perpustakaan.muat(lokasi);
                            berubah = false;
                            System.out.println("Data dimuat ulang.");
                            break;
                        case "0":
                            if (berubah && !konfirmasi(input)) {
                                break;
                            }
                            System.out.println("Program selesai.");
                            return;
                        default:
                            System.out.println("Pilihan tidak tersedia.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Data ditolak: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("Operasi file gagal: " + e.getMessage());
                }
            }
        }
    }
}
