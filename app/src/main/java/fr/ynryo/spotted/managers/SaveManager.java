package fr.ynryo.spotted.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import fr.ynryo.spotted.managers.favorite.Favorite;

/**
 * Classe gérant le stockage des préférences de l'utilisateur
 */
public class SaveManager {
    private final static String TAG = "SaveManager";
    private static final String PREFS_NAME = "spotted-prefs";
    private static final String KEY_PREFIX_NETWORK = "network_";
    private static final String KEY_FAVORITE = "favorite";
    private static final String KEY_POSITION = "position";
    private final SharedPreferences prefs;
    private final Gson gson;

    public SaveManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveNetworkFilter(String networkRef, boolean isVisible) {
        String key = KEY_PREFIX_NETWORK + networkRef;
        prefs.edit().putBoolean(key, isVisible).apply();
    }

    public boolean loadNetworkFilter(String networkRef) {
        String key = KEY_PREFIX_NETWORK + networkRef;
        return prefs.getBoolean(key, true);
    }

    public void saveAllNetworksVisibility(List<String> networkRefs, boolean isVisible) {
        SharedPreferences.Editor editor = prefs.edit();
        for (String networkRef : networkRefs) {
            String key = KEY_PREFIX_NETWORK + networkRef;
            editor.putBoolean(key, isVisible);
        }
        editor.apply();
    }

    public boolean isAllNetworksVisible() {
        Map<String, ?> entries = prefs.getAll();
        if (entries.isEmpty()) return true;
        for (Map.Entry<String, ?> entry : entries.entrySet()) {
            if (entry.getKey().startsWith(KEY_PREFIX_NETWORK) && entry.getValue().equals(false)) {
                return false;
            }
        }
        return true;
    }

    public void saveFavoriteLines(List<Favorite> favoriteLines) {
        favoriteLines.sort(Comparator
                .comparing(Favorite::getLineText, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(Favorite::getDestination, Comparator.nullsLast(String::compareToIgnoreCase))
        );
        String json = gson.toJson(favoriteLines);
        prefs.edit().putString(KEY_FAVORITE, json).apply();
    }

    public List<Favorite> loadFavoriteLines() {
        String json = prefs.getString(KEY_FAVORITE, null);
        if (json == null || json.isEmpty()) return new ArrayList<>();

        Type type = new TypeToken<List<Favorite>>() {}.getType();
        List<Favorite> list = null;
        try {
            list = gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            prefs.edit().remove(KEY_FAVORITE).apply();
            Log.e(TAG, "Erreur de parsing des favoris", e);
        }

        return list != null ? list : new ArrayList<>();
    }

    public void saveCameraPosition(CameraPosition position) {
        if (position == null) return;
        String json = gson.toJson(position);
        prefs.edit().putString(KEY_POSITION, json).apply();
    }

    public CameraPosition loadCameraPosition() {
        String json = prefs.getString(KEY_POSITION, null);
        if (json == null || json.isEmpty()) return null;

        Type type = new TypeToken<CameraPosition>() {
        }.getType();
        CameraPosition position = null;
        try {
            position = gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            prefs.edit().remove(KEY_POSITION).apply();
            Log.e(TAG, "Erreur de parsing de la position de caméra", e);
        }

        return position;
    }

    public void saveMapType(int mapType) {
        if (mapType == 0) return;

        prefs.edit().putInt("map_type", mapType).apply();
    }

    public int loadMapType() {
        return prefs.getInt("map_type", 0);
    }
}