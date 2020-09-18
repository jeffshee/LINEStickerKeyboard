package io.github.jeffshee.linestickerkeyboard.Util;

import android.content.Context;
import android.util.Log;

import java.io.File;

import io.github.jeffshee.linestickerkeyboard.Model.Sticker;
import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;

public class FileHelper {
    public static File getPngFile(Context context, int id) {
        File pngDir = new File(context.getFilesDir(), "png");
        return new File(pngDir, id + ".png");
    }

    public static File getFile(Context context, Sticker sticker) {
        if (sticker.getType() == Sticker.Type.STATIC) {
            File pngDir = new File(context.getFilesDir(), "png");
            return new File(pngDir, sticker.getId() + ".png");
        } else {
            File gifDir = new File(context.getFilesDir(), "gif");
            return new File(gifDir, sticker.getId() + ".gif");
        }
    }

    public static void deleteFile(Context context, StickerPack stickerPack) {
        File pngDir = new File(context.getFilesDir(), "png");
        for (int id : stickerPack.getIds()) {
            File file = new File(pngDir, id + ".png");
            if (!file.delete()) {
                Log.d("FileHelper", id + ".png delete failed");
            }
            if (stickerPack.getType() != Sticker.Type.STATIC) {
                File gifDir = new File(context.getFilesDir(), "gif");
                file = new File(gifDir, id + ".gif");
                if (!file.delete()) {
                    Log.d("FileHelper", id + ".gif delete failed");
                }
            }
        }
    }
}
