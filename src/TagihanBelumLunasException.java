// Exception: tagihan belum lunas saat check-out
public class TagihanBelumLunasException extends Exception {
    public TagihanBelumLunasException(String pesan) {
        super(pesan);
    }
}
