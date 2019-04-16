package io.github.jeffshee.linestickerkeyboard.View;

import android.content.Context;

import io.github.jeffshee.linestickerkeyboard.Adapter.HistoryAdapter;
import io.github.jeffshee.linestickerkeyboard.Model.HistoryPack;

public class HistoryPackView extends BasePackView {
    public HistoryAdapter adapter;

    public HistoryPackView(Context context) {
        super(context);
    }

    public HistoryPackView(Context context, HistoryPack historyPack) {
        super(context);
        adapter = new HistoryAdapter(context, historyPack);
        setUpRecyclerViewForHistory(adapter);
    }

}
