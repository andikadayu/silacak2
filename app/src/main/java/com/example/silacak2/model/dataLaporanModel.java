package com.example.silacak2.model;

public class dataLaporanModel {
    public String idLaporan,detailLaporan,statusLaporan,namaLaporan,tglLaporan;

    public dataLaporanModel(String idLaporan, String detailLaporan, String statusLaporan, String namaLaporan, String tglLaporan) {
        this.idLaporan = idLaporan;
        this.detailLaporan = detailLaporan;
        this.statusLaporan = statusLaporan;
        this.namaLaporan = namaLaporan;
        this.tglLaporan = tglLaporan;
    }

    public String getIdLaporan() {
        return idLaporan;
    }

    public void setIdLaporan(String idLaporan) {
        this.idLaporan = idLaporan;
    }

    public String getDetailLaporan() {
        return detailLaporan;
    }

    public void setDetailLaporan(String detailLaporan) {
        this.detailLaporan = detailLaporan;
    }

    public String getStatusLaporan() {
        return statusLaporan;
    }

    public void setStatusLaporan(String statusLaporan) {
        this.statusLaporan = statusLaporan;
    }

    public String getNamaLaporan() {
        return namaLaporan;
    }

    public void setNamaLaporan(String namaLaporan) {
        this.namaLaporan = namaLaporan;
    }

    public String getTglLaporan() {
        return tglLaporan;
    }

    public void setTglLaporan(String tglLaporan) {
        this.tglLaporan = tglLaporan;
    }
}
