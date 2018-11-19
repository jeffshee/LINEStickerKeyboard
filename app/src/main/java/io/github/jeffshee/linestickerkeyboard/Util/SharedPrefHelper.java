package io.github.jeffshee.linestickerkeyboard.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import io.github.jeffshee.linestickerkeyboard.Model.HistoryPack;
import io.github.jeffshee.linestickerkeyboard.Model.Sticker;
import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;

public class SharedPrefHelper {
    private static final String SHARED_PREF = "linestickerkeyboard.pref";
    private static final String KEY_HISTORY = "linestickerkeyboard.pref.history";
    private static final String KEY_STICKERS = "linestickerkeyboard.pref.stickers";
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    public SharedPrefHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
    }

    public ArrayList<StickerPack> getStickerPacksFromPref() {
        String json = sharedPreferences.getString(KEY_STICKERS, "");
        ArrayList<StickerPack> stickerPacks;
        if (json.equals("")) {
            stickerPacks = new ArrayList<>();
        } else {
            stickerPacks = gson.fromJson(sharedPreferences.getString(KEY_STICKERS, null),
                    new TypeToken<ArrayList<StickerPack>>() {
                    }.getType());
        }
        return stickerPacks;
    }

    public HistoryPack getHistoryFromPref() {
        String json = sharedPreferences.getString(KEY_HISTORY, "");
        HistoryPack historyPack;
        if (json.equals("")) {
            historyPack = new HistoryPack(new ArrayList<Sticker>());
        } else {
            historyPack = gson.fromJson(sharedPreferences.getString(KEY_HISTORY, null),
                    HistoryPack.class);
        }
        return historyPack;
    }


    public void addNewStickerPack(StickerPack stickerPack) {
        ArrayList<StickerPack> stickerPacks;
        stickerPacks = getStickerPacksFromPref();
        stickerPacks.add(stickerPack);
        saveNewStickerPacks(stickerPacks);
    }

    public void saveNewStickerPacks(ArrayList<StickerPack> stickerPacks) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_STICKERS, gson.toJson(stickerPacks));
        editor.apply();
    }

    public void saveNewHistoryPack(HistoryPack historyPack) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_HISTORY, gson.toJson(historyPack));
        editor.apply();
    }
}
