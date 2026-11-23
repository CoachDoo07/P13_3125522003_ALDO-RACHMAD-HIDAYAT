// Kelas Pembayaran: merekam satu transaksi pembayaran yang dilakukan penyewa
public class Pembayaran {
    private String idPenyewa;
    private double jumlah;
    private String keterangan;

    public Pembayaran(String idPenyewa, double jumlah, String keterangan) {
        if (idPenyewa == null || idPenyewa.isBlank()) {
            throw new IllegalArgumentException("ID Penyewa tidak boleh kosong.");
        }
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pembayaran harus lebih dari 0.");
        }
        this.idPenyewa = idPenyewa.trim();
        this.jumlah = jumlah;
        this.keterangan = (keterangan != null) ? keterangan.trim() : "";
    }

    public String getIdPenyewa()   { return idPenyewa; }
    public double getJumlah()      { return jumlah; }
    public String getKeterangan()  { return keterangan; }

    @Override
    public String toString() {
        return "[Pembayaran] " + idPenyewa + " | Rp " + (long)jumlah + " | " + keterangan;
    }
}
