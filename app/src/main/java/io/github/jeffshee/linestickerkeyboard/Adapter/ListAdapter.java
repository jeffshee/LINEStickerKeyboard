package io.github.jeffshee.linestickerkeyboard.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;
import io.github.jeffshee.linestickerkeyboard.R;
import io.github.jeffshee.linestickerkeyboard.Util.FileHelper;
import io.github.jeffshee.linestickerkeyboard.Util.SharedPrefHelper;

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
        stickerViewHolder.textView.setText("ID: " + String.valueOf(stickerPacks.get(i).getFirstId()));
        File png = FileHelper.getPngFile(context, stickerPacks.get(i).getFirstId());
        Glide.with(context).load(png).into(stickerViewHolder.imageView);
        stickerViewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = stickerPacks.indexOf((StickerPack) stickerViewHolder.itemView.getTag());
                stickerPacks.remove(i);
                SharedPrefHelper helper = new SharedPrefHelper(context);
                helper.saveNewStickerPacks(stickerPacks);
                notifyItemRemoved(i);
            }
        });
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
        SharedPrefHelper helper = new SharedPrefHelper(context);
        helper.saveNewStickerPacks(stickerPacks);
        notifyItemMoved(fromPosition, toPosition);
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
