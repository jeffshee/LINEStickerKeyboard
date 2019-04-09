package io.github.jeffshee.linestickerkeyboard.View;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import io.github.jeffshee.linestickerkeyboard.Adapter.HistoryAdapter;
import io.github.jeffshee.linestickerkeyboard.Adapter.StickerAdapter;
import io.github.jeffshee.linestickerkeyboard.R;
import io.github.jeffshee.linestickerkeyboard.SnapHelper.GravitySnapHelper;

public class BasePackView extends LinearLayout {
    protected RecyclerView recyclerView;
    protected int span;

    public BasePackView(Context context) {
        super(context);
        init(context);
    }

    protected void init(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        ContextThemeWrapper ctx = new ContextThemeWrapper(context, R.style.MyTheme);
        LayoutInflater.from(ctx).inflate(R.layout.view_pack, this, true);
        recyclerView = findViewById(R.id.recycler);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float viewWidth = (displayMetrics.widthPixels
                - getResources().getDimension(R.dimen.default_padding) * 2)
                / displayMetrics.density;
        float itemWidth = (getResources().getDimension(R.dimen.sticker_size)
                + getResources().getDimension(R.dimen.default_padding)) / displayMetrics.density;
        span = (int) Math.floor(viewWidth / itemWidth);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, span);
        recyclerView.setLayoutManager(gridLayoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    protected void setUpRecyclerViewForHistory(HistoryAdapter adapter) {
        recyclerView.setAdapter(adapter);
        GravitySnapHelper gravitySnapHelper = new GravitySnapHelper(Gravity.TOP, span);
        gravitySnapHelper.attachToRecyclerView(recyclerView);
    }

    protected void setUpRecyclerViewForSticker(StickerAdapter adapter) {
        //LinearLayoutManager layoutManagerStart = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        //recyclerView.setLayoutManager(layoutManagerStart);
        recyclerView.setAdapter(adapter);
        //SnapHelper snapHelperStart = new StartSnapHelper();
        //snapHelperStart.attachToRecyclerView(recyclerView);
        GravitySnapHelper gravitySnapHelper = new GravitySnapHelper(Gravity.TOP, span);
        gravitySnapHelper.attachToRecyclerView(recyclerView);
    }

}
