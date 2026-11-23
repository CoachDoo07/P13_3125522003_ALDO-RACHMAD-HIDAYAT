// Kelas abstrak Pengguna: induk dari Penyewa dan PemilikKos
public abstract class Pengguna {
    protected String nama;
    protected String noTelepon;

    public Pengguna(String nama, String noTelepon) {
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama tidak boleh kosong.");
        }
        if (noTelepon == null || noTelepon.isBlank()) {
            throw new IllegalArgumentException("Nomor telepon tidak boleh kosong.");
        }
        if (nama.contains("|") || nama.contains("\n") || nama.contains("\r")) {
            throw new IllegalArgumentException("Nama mengandung karakter pemisah terlarang.");
        }
        if (noTelepon.contains("|") || noTelepon.contains("\n") || noTelepon.contains("\r")) {
            throw new IllegalArgumentException("Nomor telepon mengandung karakter pemisah terlarang.");
        }
        this.nama = nama.trim();
        this.noTelepon = noTelepon.trim();
    }

    public String getNama()      { return nama; }
    public String getNoTelepon() { return noTelepon; }

    // Setiap subclass wajib implementasi
    public abstract String getPeran();

    @Override
    public String toString() {
        return getPeran() + ": " + nama + " (" + noTelepon + ")";
    }
}
