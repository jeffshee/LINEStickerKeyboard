package io.github.jeffshee.linestickerkeyboard.View;

import android.content.Context;

import io.github.jeffshee.linestickerkeyboard.Adapter.StickerAdapter;
import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;

public class StickerPackView extends BasePackView {
    public StickerAdapter adapter;

    public StickerPackView(Context context) {
        super(context);
    }

    public StickerPackView(Context context, StickerPack stickerPack) {
        super(context);
        adapter = new StickerAdapter(context, stickerPack);
        recyclerView.setAdapter(adapter);
    }

}
