package io.github.jeffshee.linestickerkeyboard.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;
import io.github.jeffshee.linestickerkeyboard.R;
import io.github.jeffshee.linestickerkeyboard.Util.FileHelper;
import io.github.jeffshee.linestickerkeyboard.Util.SharedPrefHelper;

import static io.github.jeffshee.linestickerkeyboard.FetchService.BROADCAST_ACTION;

public class ListAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {
    private Context context;
    private ArrayList<StickerPack> stickerPacks;

    public ListAdapter(Context context, ArrayList<StickerPack> stickerPacks) {
        this.context = context;
        this.stickerPacks = stickerPacks;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // https://stackoverflow.com/questions/30691150/match-parent-width-does-not-work-in-recyclerview
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_edit, viewGroup, false);
        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final ListViewHolder stickerViewHolder = (ListViewHolder) viewHolder;
        stickerViewHolder.itemView.setTag(stickerPacks.get(i));
        stickerViewHolder.textView.setText(stickerPacks.get(i).getTitle());
        File png = FileHelper.getPngFile(context, stickerPacks.get(i).getFirstId());
        Glide.with(context).load(png).into(stickerViewHolder.imageView);
        stickerViewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = stickerPacks.indexOf((StickerPack) stickerViewHolder.itemView.getTag());
                delete(i);
            }
        });
        stickerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = stickerPacks.indexOf((StickerPack) stickerViewHolder.itemView.getTag());
                Toast.makeText(context,
                        context.getString(R.string.store_id)+" "+
                                stickerPacks.get(i).getStoreId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void delete(final int index) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.delete_sticker))
                .setPositiveButton(context.getString(R.string.positive_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Remove deleted sticker from history
                        SharedPrefHelper.cleanHistory(context, stickerPacks.get(index));
                        // Delete files
                        FileHelper.deleteFile(context, stickerPacks.get(index));
                        // Save changes
                        stickerPacks.remove(index);
                        SharedPrefHelper.saveNewStickerPacks(context, stickerPacks);
                        // Notify adapter itself
                        notifyItemRemoved(index);
                        // Notify IMServer only
                        Intent intent = new Intent();
                        intent.setAction(BROADCAST_ACTION);
                        intent.putExtra("message", "delete");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                })
                .setNegativeButton(context.getString(R.string.negative_cancel), null);
        builder.show();
    }

    public void setData(ArrayList<StickerPack> stickerPacks) {
        this.stickerPacks = stickerPacks;
    }

    @Override
    public int getItemCount() {
        return stickerPacks.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(stickerPacks, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(stickerPacks, i, i - 1);
            }
        }
        SharedPrefHelper.saveNewStickerPacks(context, stickerPacks);
        notifyItemMoved(fromPosition, toPosition);
        // Notify IMServer only
        Intent intent = new Intent();
        intent.setAction(BROADCAST_ACTION);
        intent.putExtra("message", "reorder");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    // ViewHolder
    private class ListViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private Button button;

        ListViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            button = itemView.findViewById(R.id.btnDelete);
        }
    }
}
