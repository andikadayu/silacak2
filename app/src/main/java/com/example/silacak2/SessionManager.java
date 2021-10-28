package com.example.silacak2;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;

public class SessionManager {
    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String NAMA = "nama";
    public static final String ID_GPS = "id_GPS";
    public static final String ID_USER = "id_user";
    public static final String ROLE = "role";
    Context contexts;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        this.contexts = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(contexts);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(LoginData user) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(NAMA, user.getNama());
        editor.putString(ID_GPS, user.getId_gps());
        editor.putString(ID_USER, user.getId_user());
        editor.putString(ROLE, user.getRole());
        editor.apply();
    }

    public HashMap<String, String> getUserDetail() {
        HashMap<String, String> user = new HashMap<>();
        user.put(ID_GPS, sharedPreferences.getString(ID_GPS, null));
        user.put(NAMA, sharedPreferences.getString(NAMA, null));
        user.put(ID_USER, sharedPreferences.getString(ID_USER, null));
        user.put(ROLE, sharedPreferences.getString(ROLE, null));
        return user;
    }

    public void logoutSession() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }
}
