// Kelas PemilikKos: merepresentasikan pemilik sistem kos
public class PemilikKos extends Pengguna {
    public PemilikKos(String nama, String noTelepon) {
        super(nama, noTelepon);
    }

    @Override
    public String getPeran() { return "Pemilik Kos"; }

    public void tampilkanLaporan(Kos kos) {
        System.out.println("=== Laporan " + kos.getNamaKos() + " ===");
        System.out.println("Pemilik Kos        : " + nama + " (" + noTelepon + ")");
        System.out.println("Kamar terdaftar    : " + kos.getDaftarKamar().size());
        System.out.println("Kamar tersedia     : " + kos.jumlahKamarTersedia());
        System.out.println("Penyewa terdaftar  : " + kos.getDaftarPenyewa().size());
        System.out.println("Potensi pendapatan : Rp " + (long) kos.hitungPotensiPendapatan() + " / bulan");
        System.out.println("============================================");
    }
}
