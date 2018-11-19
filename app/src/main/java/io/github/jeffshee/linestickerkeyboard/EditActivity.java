package io.github.jeffshee.linestickerkeyboard;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.ArrayList;

import io.github.jeffshee.linestickerkeyboard.Adapter.AdapterCallback;
import io.github.jeffshee.linestickerkeyboard.Adapter.ListAdapter;
import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;
import io.github.jeffshee.linestickerkeyboard.Util.SharedPrefHelper;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPrefHelper helper = new SharedPrefHelper(this);
        ArrayList<StickerPack> stickerPacks = helper.getStickerPacksFromPref();
        ListAdapter listAdapter = new ListAdapter(this, stickerPacks);
        ItemTouchHelper.Callback callback = new AdapterCallback(listAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(listAdapter);
        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(this
                , R.dimen.item_offset_x, R.dimen.item_offset_y);
        recyclerView.addItemDecoration(itemOffsetDecoration);
    }

    class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffsetX;
        private int mItemOffsetY;

        private ItemOffsetDecoration(int itemOffsetX, int itemOffsetY) {
            mItemOffsetX = itemOffsetX;
            mItemOffsetY = itemOffsetY;
        }

        ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetIdX, @DimenRes int itemOffsetIdY) {
            this(context.getResources().getDimensionPixelSize(itemOffsetIdX),
                    context.getResources().getDimensionPixelSize(itemOffsetIdY));
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffsetX, mItemOffsetY, mItemOffsetX, mItemOffsetY / 2);
        }
    }
}
