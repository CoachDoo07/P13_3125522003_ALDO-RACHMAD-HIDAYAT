import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * SiKosRepository — Menangani persistensi data kamar ke file teks.
 *
 * Menerapkan prinsip Single Responsibility (SRP):
 *   - Memisahkan operasi baca/tulis file dari class Kos dan Main.
 *   - Memastikan seluruh data valid sebelum mengganti data yang ada (rollback).
 *
 * Format baris: nomorKamar|tipe|hargaSewa|terisi
 */
public class SiKosRepository {

    public static void simpanKamar(List<Kamar> daftarKamar, Path lokasi) throws IOException {
        List<String> baris = new ArrayList<>();
        for (Kamar k : daftarKamar) {
            baris.add(k.keCSV());
        }
        Files.write(lokasi, baris, StandardCharsets.UTF_8);
    }

    public static void muatKamar(List<Kamar> targetKoleksi, Path lokasi) throws IOException {
        List<String> baris = Files.readAllLines(lokasi, StandardCharsets.UTF_8);
        List<Kamar> sementara = new ArrayList<>();

        for (int i = 0; i < baris.size(); i++) {
            String b = baris.get(i);
            if (b.isBlank()) continue;

            String[] bagian = b.split("\\|", -1);
            if (bagian.length != 4) {
                throw new IOException(
                    "Format salah pada baris " + (i + 1) + ": butuh 4 kolom, ditemukan " + bagian.length
                );
            }

            try {
                double harga = Double.parseDouble(bagian[2]);
                boolean terisi = Boolean.parseBoolean(bagian[3]);
                Kamar k = new Kamar(bagian[0], bagian[1], harga);
                k.setTerisi(terisi);

                // Cek duplikat antar baris file
                for (Kamar ada : sementara) {
                    if (ada.getNomorKamar().equalsIgnoreCase(k.getNomorKamar())) {
                        throw new IllegalArgumentException("Nomor kamar duplikat di file: " + k.getNomorKamar());
                    }
                }
                sementara.add(k);
            } catch (IllegalArgumentException e) {
                throw new IOException(
                    "Data tidak valid pada baris " + (i + 1) + ": " + e.getMessage(), e
                );
            }
        }

        // Ganti collection HANYA jika seluruh file valid (Rollback terjaga)
        targetKoleksi.clear();
        targetKoleksi.addAll(sementara);
    }

    public static boolean fileAda(Path lokasi) {
        return Files.exists(lokasi);
    }
}
