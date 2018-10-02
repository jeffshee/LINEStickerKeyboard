package io.github.jeffshee.linestickerkeyboard.Adapter;

import android.content.Context;

import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;

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
    protected String getPreviewUrl(int position) {
        return URL_F + String.valueOf(stickerPack.getFirstId() + position) + URL_B;
    }

    @Override
    protected int getIdForTag(int position) {
        return stickerPack.getFirstId() + position;
    }

    @Override
    protected boolean saveHistory() {
        return true;
    }

}
