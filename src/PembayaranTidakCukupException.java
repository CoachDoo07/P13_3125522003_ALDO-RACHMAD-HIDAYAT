// Exception: pembayaran tidak mencukupi tagihan
public class PembayaranTidakCukupException extends Exception {
    public PembayaranTidakCukupException(String pesan) {
        super(pesan);
    }
}
