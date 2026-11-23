// Kelas Penyewa: merepresentasikan penyewa yang menempati kamar kos
public class Penyewa extends Pengguna {
    private String idPenyewa;
    private Kamar kamarDitempati;
    private Tagihan tagihan;
    private boolean aktif;

    public Penyewa(String idPenyewa, String nama, String noTelepon, Kamar kamar)
            throws KamarTidakTersediaException {
        super(nama, noTelepon);
        if (idPenyewa == null || idPenyewa.isBlank()) {
            throw new IllegalArgumentException("ID Penyewa tidak boleh kosong.");
        }
        if (idPenyewa.contains("|") || idPenyewa.contains("\n") || idPenyewa.contains("\r")) {
            throw new IllegalArgumentException("ID Penyewa mengandung karakter pemisah terlarang '|'.");
        }
        if (kamar == null) {
            throw new IllegalArgumentException("Objek kamar tidak boleh null.");
        }
        if (kamar.isTerisi()) {
            throw new KamarTidakTersediaException(
                "Kamar " + kamar.getNomorKamar() + " sudah terisi, tidak dapat dihuni."
            );
        }

        this.idPenyewa = idPenyewa.trim();
        this.kamarDitempati = kamar;
        this.tagihan = new Tagihan(kamar.getHargaSewa());
        this.aktif = true;
        kamar.setTerisi(true);
    }

    public String  getIdPenyewa()      { return idPenyewa; }
    public Kamar   getKamarDitempati() { return kamarDitempati; }
    public Tagihan getTagihan()        { return tagihan; }
    public boolean isAktif()           { return aktif; }

    @Override
    public String getPeran() { return "Penyewa"; }

    public Pembayaran bayarTagihan(double jumlah) throws PembayaranTidakCukupException {
        tagihan.bayar(jumlah);
        return new Pembayaran(idPenyewa, jumlah, "Pembayaran sewa kamar " + kamarDitempati.getNomorKamar());
    }

    public void checkOut() throws TagihanBelumLunasException {
        if (!tagihan.isLunas()) {
            throw new TagihanBelumLunasException(
                "Tagihan " + idPenyewa + " belum lunas. Selesaikan pembayaran sebelum check-out."
            );
        }
        aktif = false;
        kamarDitempati.setTerisi(false);
    }

    @Override
    public String toString() {
        return idPenyewa + " - " + nama + " (Menempati: " + kamarDitempati.getNomorKamar() + ")";
    }
}
