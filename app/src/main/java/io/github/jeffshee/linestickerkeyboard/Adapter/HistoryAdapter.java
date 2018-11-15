package io.github.jeffshee.linestickerkeyboard.Adapter;

import android.content.Context;

import java.io.File;

import io.github.jeffshee.linestickerkeyboard.Model.HistoryPack;
import io.github.jeffshee.linestickerkeyboard.Model.Sticker;
import io.github.jeffshee.linestickerkeyboard.Util.FileHelper;

public class HistoryAdapter extends BasePackAdapter {
    private HistoryPack historyPack;

    public HistoryAdapter(Context context, HistoryPack historyPack) {
        super(context);
        this.historyPack = historyPack;
    }

    @Override
    protected File getStickerPng(Context context, int position) {
        return FileHelper.getPngFile(context, historyPack.getId(position));
    }

    @Override
    protected Sticker getSticker(int position) {
        return historyPack.getSticker(position);
    }

    @Override
    protected boolean saveHistory() {
        return false;
    }

    @Override
    public int getItemCount() {
        if (historyPack != null)
            return historyPack.size();
        else return 0;
    }

    public void update(HistoryPack historyPack) {
        this.historyPack = historyPack;
        notifyDataSetChanged();
    }
}
