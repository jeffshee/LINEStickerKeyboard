package io.github.jeffshee.linestickerkeyboard.Adapter;

import android.content.Context;

import io.github.jeffshee.linestickerkeyboard.Model.HistoryPack;

public class HistoryAdapter extends BasePackAdapter {
    private HistoryPack historyPack;

    public HistoryAdapter(Context context, HistoryPack historyPack) {
        super(context);
        this.historyPack = historyPack;
    }

    @Override
    protected String getPreviewUrl(int position) {
        return URL_F + String.valueOf(historyPack.getId(position)) + URL_B;
    }

    @Override
    protected int getIdForTag(int position) {
        return historyPack.getId(position);
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
