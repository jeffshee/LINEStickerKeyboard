package io.github.jeffshee.linestickerkeyboard;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.jeffshee.linestickerkeyboard.Adapter.AdapterCallback;
import io.github.jeffshee.linestickerkeyboard.Adapter.ListAdapter;
import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;
import io.github.jeffshee.linestickerkeyboard.Util.NewSharedPrefHelper;

import static io.github.jeffshee.linestickerkeyboard.FetchService.BROADCAST_ACTION;

public class EditActivity extends AppCompatActivity {

    Activity activity;
    ListAdapter listAdapter;
    ArrayList<StickerPack> stickerPacks;
    Receiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        stickerPacks = NewSharedPrefHelper.getStickerPacksFromPref(this);
        listAdapter = new ListAdapter(this, stickerPacks);
        ItemTouchHelper.Callback callback = new AdapterCallback(listAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(listAdapter);
        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(this
                , R.dimen.item_offset_x, R.dimen.item_offset_y);
        recyclerView.addItemDecoration(itemOffsetDecoration);

        // Broadcast Receiver
        IntentFilter filter = new IntentFilter(BROADCAST_ACTION);
        receiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        activity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manual_add:
                manualAdd();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void manualAdd() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_editor, null);
        final EditText editText = view.findViewById(R.id.etId);
        builder.setView(view)
                .setPositiveButton(getString(R.string.positive_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FetchService.startActionFetchManual(activity, Integer.parseInt(editText.getText().toString()));
                        Toast.makeText(activity, getString(R.string.fetch_activity_toast), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.negative_cancel), null);
        builder.show();
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

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if("add".equals(intent.getStringExtra("message"))){
                stickerPacks = NewSharedPrefHelper.getStickerPacksFromPref(activity);
                listAdapter.setData(stickerPacks);
                listAdapter.notifyDataSetChanged();
            }
        }
    }

}
