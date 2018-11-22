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
        for (int i = stickerPack.getFirstId(); i < stickerPack.getFirstId() + stickerPack.getCount(); i++) {
            File file = new File(pngDir, i + ".png");
            if (!file.delete()) {
                Log.d("FileHelper", i + ".png delete failed");
            }
        }
        if (stickerPack.getType() != Sticker.Type.STATIC) {
            File gifDir = new File(context.getFilesDir(), "gif");
            for (int i = stickerPack.getFirstId(); i < stickerPack.getFirstId() + stickerPack.getCount(); i++) {
                File file = new File(gifDir, i + ".gif");
                if (!file.delete()) {
                    Log.d("FileHelper", i + ".gif delete failed");
                }
            }
        }
    }
}
