package io.github.jeffshee.linestickerkeyboard.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;
import io.github.jeffshee.linestickerkeyboard.R;

public class ListAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {
    private static final String URL_F = "https://sdl-stickershop.line.naver.jp/stickershop/v1/sticker/";
    private static final String URL_B = "/android/sticker.png;compress=true";
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
        Glide.with(context).load(URL_F + String.valueOf(stickerPacks.get(i).getFirstId()) + URL_B).into(stickerViewHolder.imageView);
        stickerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditorDialog(context, view);
            }
        });
        stickerViewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = stickerPacks.indexOf((StickerPack) stickerViewHolder.itemView.getTag());
                stickerPacks.remove(i);
                notifyItemRemoved(i);
            }
        });
    }

    private void showEditorDialog(Context context, final View itemView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_editor, null, false);
        builder.setView(view);
        final EditText etId = view.findViewById(R.id.etId);
        final EditText etCount = view.findViewById(R.id.etCount);
        etId.setText(String.valueOf(((StickerPack) itemView.getTag()).getFirstId()));
        etCount.setText(String.valueOf(((StickerPack) itemView.getTag()).getCount()));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int id, count;
                try {
                    id = Integer.parseInt(etId.getText().toString());
                    count = Integer.parseInt(etCount.getText().toString());
                } catch (NumberFormatException e) {
                    return;
                }
                int index = stickerPacks.indexOf((StickerPack) itemView.getTag());
                stickerPacks.remove(index);
                StickerPack stickerPack = new StickerPack(id, count);
                stickerPacks.add(index, stickerPack);
                itemView.setTag(stickerPack);
                notifyItemChanged(index);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
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
