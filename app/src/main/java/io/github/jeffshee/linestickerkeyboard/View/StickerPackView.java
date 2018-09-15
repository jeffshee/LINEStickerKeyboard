package io.github.jeffshee.linestickerkeyboard.View;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import io.github.jeffshee.linestickerkeyboard.Adapter.StickerAdapter;
import io.github.jeffshee.linestickerkeyboard.R;

public class StickerPackView extends LinearLayout {
    public StickerPackView(Context context) {
        super(context);
    }

    public StickerPackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StickerPackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StickerPackView(Context context, int id, int count) {
        super(context);
        init(context, id, count);
    }

    private void init(Context context, int id, int count) {
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // https://stackoverflow.com/questions/36924481/you-need-to-use-a-theme-appcompat-theme-or-descendant-with-the-design-library
        ContextThemeWrapper ctx = new ContextThemeWrapper(context, R.style.MyTheme);
        LayoutInflater.from(ctx).inflate(R.layout.view_pack, this, true);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float viewWidth = (displayMetrics.widthPixels
                - getResources().getDimension(R.dimen.default_padding) * 2)
                / displayMetrics.density;
        float itemWidth = (getResources().getDimension(R.dimen.sticker_size)
                + getResources().getDimension(R.dimen.default_padding)) / displayMetrics.density;
        int span = (int) Math.floor(viewWidth / itemWidth);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, span);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(new StickerAdapter(id, count, context));
    }
}
