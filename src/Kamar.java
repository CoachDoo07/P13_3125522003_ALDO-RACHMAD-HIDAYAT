// Kelas Kamar: merepresentasikan satu unit kamar kos
// P13: Validasi karakter pemisah terlarang '|' dipindahkan ke constructor
// sehingga data yang tidak dapat disimpan ditolak sejak awal saat objek dibentuk.
public class Kamar implements DapatDicekStatus {
    private String nomorKamar;
    private String tipe;        // "Single" / "Double" / "VIP"
    private double hargaSewa;   // per bulan
    private boolean terisi;

    public Kamar(String nomorKamar, String tipe, double hargaSewa) {
        if (nomorKamar == null || nomorKamar.isBlank()) {
            throw new IllegalArgumentException("Nomor kamar tidak boleh kosong.");
        }
        if (tipe == null || tipe.isBlank()) {
            throw new IllegalArgumentException("Tipe kamar tidak boleh kosong.");
        }
        // Validasi pemisah terlarang | dan newline
        if (nomorKamar.contains("|") || nomorKamar.contains("\n") || nomorKamar.contains("\r")) {
            throw new IllegalArgumentException("Nomor kamar mengandung karakter pemisah terlarang '|'.");
        }
        if (tipe.contains("|") || tipe.contains("\n") || tipe.contains("\r")) {
            throw new IllegalArgumentException("Tipe kamar mengandung karakter pemisah terlarang '|'.");
        }
        if (hargaSewa <= 0) {
            throw new IllegalArgumentException("Harga sewa harus lebih dari 0, bukan: " + hargaSewa);
        }

        this.nomorKamar = nomorKamar.trim();
        this.tipe = tipe.trim();
        this.hargaSewa = hargaSewa;
        this.terisi = false;
    }

    // ── Getter & Setter ──────────────────────────────────────────
    public String  getNomorKamar() { return nomorKamar; }
    public String  getTipe()       { return tipe; }
    public double  getHargaSewa()  { return hargaSewa; }
    public boolean isTerisi()      { return terisi; }

    public void setTerisi(boolean terisi) {
        this.terisi = terisi;
    }

    @Override
    public String getStatus() {
        return terisi ? "Terisi" : "Tersedia";
    }

    // ── Serialisasi CSV: "nomorKamar|tipe|hargaSewa|terisi" ─────
    public String keCSV() {
        return nomorKamar + "|" + tipe + "|" + hargaSewa + "|" + terisi;
    }

    public static Kamar dariCSV(String baris) {
        String[] bagian = baris.split("\\|", -1);
        if (bagian.length != 4) {
            throw new IllegalArgumentException("Format data kamar salah, memerlukan 4 kolom: " + baris);
        }
        Kamar k = new Kamar(bagian[0], bagian[1], Double.parseDouble(bagian[2]));
        k.setTerisi(Boolean.parseBoolean(bagian[3]));
        return k;
    }

    @Override
    public String toString() {
        return nomorKamar + " (" + tipe + ", " + getStatus() + ") seharga Rp " + (long) hargaSewa;
    }
}
