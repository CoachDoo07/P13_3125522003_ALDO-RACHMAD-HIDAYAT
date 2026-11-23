// Kelas Tagihan: mencatat jumlah tagihan dan status pelunasan
public class Tagihan {
    private double jumlahTagihan;
    private double jumlahDibayar;
    private boolean lunas;

    public Tagihan(double jumlahTagihan) {
        if (jumlahTagihan <= 0) {
            throw new IllegalArgumentException("Jumlah tagihan harus lebih dari 0.");
        }
        this.jumlahTagihan = jumlahTagihan;
        this.jumlahDibayar = 0;
        this.lunas = false;
    }

    public double getJumlahTagihan() { return jumlahTagihan; }
    public double getJumlahDibayar() { return jumlahDibayar; }
    public boolean isLunas()         { return lunas; }

    public void bayar(double jumlah) throws PembayaranTidakCukupException {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pembayaran harus lebih dari 0.");
        }
        double sisa = jumlahTagihan - jumlahDibayar;
        if (jumlah < sisa) {
            throw new PembayaranTidakCukupException(
                "Pembayaran kurang Rp " + (long)(sisa - jumlah) +
                " (tagihan Rp " + (long)jumlahTagihan +
                ", dibayar Rp " + (long)jumlah + ")"
            );
        }
        jumlahDibayar += jumlah;
        lunas = true;
    }

    public String getStatusTagihan() {
        return lunas ? "Lunas" : "Belum Lunas (sisa Rp " + (long)(jumlahTagihan - jumlahDibayar) + ")";
    }

    public String keCSV() {
        return jumlahTagihan + "|" + jumlahDibayar + "|" + lunas;
    }

    public static Tagihan dariCSV(String baris) {
        String[] bagian = baris.split("\\|", -1);
        if (bagian.length != 3) {
            throw new IllegalArgumentException("Format data tagihan tidak valid: " + baris);
        }
        Tagihan t = new Tagihan(Double.parseDouble(bagian[0]));
        t.jumlahDibayar = Double.parseDouble(bagian[1]);
        t.lunas = Boolean.parseBoolean(bagian[2]);
        return t;
    }

    @Override
    public String toString() {
        return "Tagihan: Rp " + (long)jumlahTagihan + " | " + getStatusTagihan();
    }
}
