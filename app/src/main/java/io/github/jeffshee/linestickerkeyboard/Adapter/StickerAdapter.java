package io.github.jeffshee.linestickerkeyboard.Adapter;

import android.content.Context;

import java.io.File;

import io.github.jeffshee.linestickerkeyboard.Model.Sticker;
import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;
import io.github.jeffshee.linestickerkeyboard.Util.FileHelper;

public class StickerAdapter extends BasePackAdapter {
    private StickerPack stickerPack;

    public StickerAdapter(Context context, StickerPack stickerPack) {
        super(context);
        this.stickerPack = stickerPack;
    }

    @Override
    public int getItemCount() {
        if (stickerPack != null)
            return stickerPack.getCount();
        else return 0;
    }

    @Override
    protected File getStickerPng(Context context, int position) {
        return FileHelper.getPngFile(context, stickerPack.getId(position));
    }

    @Override
    protected Sticker getSticker(int position) {
        return stickerPack.getSticker(position);
    }

    @Override
    protected boolean saveHistory() {
        return true;
    }

    public void update(StickerPack stickerPack) {
        this.stickerPack = stickerPack;
        notifyDataSetChanged();
    }
}
