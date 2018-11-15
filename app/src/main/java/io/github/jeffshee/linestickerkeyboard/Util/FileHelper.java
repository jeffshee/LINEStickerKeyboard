package io.github.jeffshee.linestickerkeyboard.Util;

import android.content.Context;

import java.io.File;

import io.github.jeffshee.linestickerkeyboard.Model.Sticker;

public class FileHelper {
    public static File getPngFile(Context context, int id){
        File pngDir = new File(context.getFilesDir(), "png");
        return new File(pngDir, id + ".png");
    }

    public static File getFile(Context context, Sticker sticker){
        if(sticker.getType() == Sticker.Type.STATIC){
            File pngDir = new File(context.getFilesDir(), "png");
            return new File(pngDir, sticker.getId() + ".png");
        }else{
            File gifDir = new File(context.getFilesDir(), "gif");
            return new File(gifDir, sticker.getId() + ".gif");
        }
    }
}
