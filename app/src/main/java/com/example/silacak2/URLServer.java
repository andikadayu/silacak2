package com.example.silacak2;

public class URLServer {
    public String server = "https://silacak.pt-ckit.com/"; //Server Domain Https Recommended

    public String getLogin() {
        return server + "/login.php";
    }

    public String getDaftar() {
        return server + "/daftar.php";
    }

    public String getAdmManage() {
        return server + "/api/manage_android.php";
    }

    public String getAdmTracking() {
        return server + "/getlokasi.php";
    }

    public String getAllLokasi() {
        return server + "/getAllLokasi.php";
    }

    public String   getAnggotaLokasi() {
        return server + "/getAnggotaLokasi.php";
    }

    public String getUserLokasi() {
        return server + "/getUserLokasi.php";
    }

    public String getAllAnggotaLokasi() {
        return server + "/getAllAnggotaLokasi.php";
    }

    public String getUserTracking() {
        return server + "/gpsdata.php";
    }

    public String getKonfirmAnggota() {
        return server + "/konfirmanggota.php";
    }

    public String getKonfirmUser() {
        return server + "/konfirmUser.php";
    }

    public String getBlokirUserBaru() {
        return server + "/bukaBlokirUserBaru.php";
    }

    public String getUserData() {
        return server + "/get_datauser.php";
    }

    public String getUserDataConfirm() {
        return server + "/get_datauserConfirm.php";
    }

    public String getAnggotaData() {
        return server + "/get_dataanggota.php";
    }

    public String getDataAnggota() {
        return server + "/get_anggota.php";
    }

    public String getDataAnggota2() {
        return server + "/get_anggota2.php";
    }

    public String sendOTP() {
        return server + "/send_email.php";
    }

    public String getPesanTampil() {
        return server + "/getPesanTampil.php";
    }

    public String getPesanTampilAnggota() {
        return server + "/getPesanTampil2.php";
    }

    public String getPesanTampil2() {
        return server + "/getPesanTampil2.php";
    }
    
    public String getDeleteUser() {
        return server + "/get_deleteuser.php";
    }

    public String getDataRequest() {
        return server + "/api/get_request.php";
    }

    public String getPerintah() {
        return server + "/getPerintah.php";
    }

    public String getAllPerintah() {
        return server + "/getLokasiPerintah.php";
    }

    public String getNotify() {
        return server + "/api/get_notify.php";
    }

    public String setAnggotaTugas() {
        return server + "/setAnggotaTugas.php";
    }

    public String getAnggotaPerintah() {
        return server + "/getAnggotaPerintah.php";
    }

    public String getUserPesan() {
        return server + "/get_userPesan.php";
    }

    public String getUserPesan2() {
        return server + "/get_userPesan2.php";
    }

    public String getAllListAnggota() {
        return server + "/distance/getListAnggota.php";
    }

    public String setPerintahNew() {
        return server + "/setPerintahNew.php";
    }

    public String setPesan() {
        return server + "/setPesan.php";
    }

    public String setPerintahSelesai() {
        return server + "/setPerintahSelesai.php";
    }

    public String getNotifBerkumpul(){
        return  server + "/distance/notify.php";
    }

    public String setNotifAnggota(){
        return  server + "/setNotifAnggota.php";
    }
}
