package io.github.jeffshee.linestickerkeyboard.View;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import io.github.jeffshee.linestickerkeyboard.R;

public class BasePackView extends LinearLayout {
    protected RecyclerView recyclerView;

    public BasePackView(Context context) {
        super(context);
        init(context);
    }

    protected void init(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        LayoutInflater.from(context).inflate(R.layout.view_pack, this, true);
        recyclerView = findViewById(R.id.recycler);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float viewWidth = (displayMetrics.widthPixels
                - getResources().getDimension(R.dimen.default_padding) * 2)
                / displayMetrics.density;
        float itemWidth = (getResources().getDimension(R.dimen.sticker_size)
                + getResources().getDimension(R.dimen.default_padding)) / displayMetrics.density;
        int span = (int) Math.floor(viewWidth / itemWidth);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, span);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

}
