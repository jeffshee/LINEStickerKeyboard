package io.github.jeffshee.linestickerkeyboard.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import io.github.jeffshee.linestickerkeyboard.Model.HistoryPack;
import io.github.jeffshee.linestickerkeyboard.Model.Sticker;
import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;

public class SharedPrefHelper {
    private static final String SHARED_PREF = "linestickerkeyboard.pref";
    private static final String KEY_HISTORY = "linestickerkeyboard.pref.history";
    private static final String KEY_STICKERS = "linestickerkeyboard.pref.stickers";
    private static final String KEY_DISCLAIMER = "linestickerkeyboard.pref.disclaimer";

    public static ArrayList<StickerPack> getStickerPacksFromPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_STICKERS, "");
        ArrayList<StickerPack> stickerPacks;
        if (json.equals("")) {
            stickerPacks = new ArrayList<>();
        } else {
            try {
                stickerPacks = gson.fromJson(sharedPreferences.getString(KEY_STICKERS, null),
                        new TypeToken<ArrayList<StickerPack>>() {
                        }.getType());
            } catch (JsonSyntaxException e) {
                stickerPacks = new ArrayList<>();
            }
        }
        return stickerPacks;
    }

    public static HistoryPack getHistoryFromPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_HISTORY, "");
        HistoryPack historyPack;
        if (json.equals("")) {
            historyPack = new HistoryPack(new ArrayList<Sticker>());
        } else {
            try {
                historyPack = gson.fromJson(sharedPreferences.getString(KEY_HISTORY, null),
                        HistoryPack.class);
                if (historyPack == null) historyPack = new HistoryPack(new ArrayList<Sticker>());
            } catch (JsonSyntaxException e) {
                historyPack = new HistoryPack(new ArrayList<Sticker>());
            }
        }
        return historyPack;
    }

    public static void addNewStickerPack(Context context, StickerPack stickerPack) {
        ArrayList<StickerPack> stickerPacks;
        stickerPacks = getStickerPacksFromPref(context);
        boolean isContain = false;
        for (StickerPack s : stickerPacks) {
            if (s.getStoreId() == stickerPack.getStoreId()) {
                isContain = true;
                break;
            }
        }
        if (!isContain) {
            stickerPacks.add(stickerPack);
            saveNewStickerPacks(context, stickerPacks);
        }
    }


    public static void cleanHistory(Context context, StickerPack stickerPackToDelete) {
        HistoryPack historyPack = getHistoryFromPref(context);
        historyPack.removeAll(stickerPackToDelete.getIds());
        saveNewHistoryPack(context, historyPack);
    }

    public static void addStickerToHistory(Context context, Sticker sticker) {
        HistoryPack historyPack = getHistoryFromPref(context);
        historyPack.add(sticker);
        saveNewHistoryPack(context, historyPack);
    }

    public static void saveNewStickerPacks(Context context, ArrayList<StickerPack> stickerPacks) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_STICKERS, gson.toJson(stickerPacks));
        editor.apply();
    }

    public static void saveNewHistoryPack(Context context, HistoryPack historyPack) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_HISTORY, gson.toJson(historyPack));
        editor.apply();
    }

    public static void saveDisclaimerStatus(Context context, boolean agree) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_DISCLAIMER, agree);
        editor.apply();
    }

    public static boolean getDisclaimerStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_DISCLAIMER, false);
    }
}
