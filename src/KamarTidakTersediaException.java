// Exception: kamar tidak tersedia (sudah terisi)
public class KamarTidakTersediaException extends Exception {
    public KamarTidakTersediaException(String pesan) {
        super(pesan);
    }
}
