package io.github.jeffshee.linestickerkeyboard.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;

import java.io.File;

import io.github.jeffshee.linestickerkeyboard.IMService;
import io.github.jeffshee.linestickerkeyboard.Model.Sticker;
import io.github.jeffshee.linestickerkeyboard.R;

public abstract class BasePackAdapter extends RecyclerView.Adapter {

    private Context context;
    private IMService service;

    BasePackAdapter(Context context) {
        this.context = context;
        if (context instanceof IMService)
            service = (IMService) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = View.inflate(viewGroup.getContext(), R.layout.item_sticker, null);
        return new StickerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final StickerViewHolder stickerViewHolder = (StickerViewHolder) holder;
        stickerViewHolder.itemView.setTag(getSticker(position));
        stickerViewHolder.textView.setText(R.string.loading);
        stickerViewHolder.textView.setTextColor(context.getResources().getColor(R.color.colorLoading));
        File png = getFile(context, position);
        RequestOptions requestOptions = new RequestOptions().error(R.drawable.error).signature(new ObjectKey(png.lastModified()));
        RequestBuilder<Drawable> requestBuilder = Glide.with(context).load(png);
        requestBuilder.listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                stickerViewHolder.textView.setText(R.string.error);
                stickerViewHolder.textView.setTextColor(context.getResources().getColor(R.color.colorError));
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                stickerViewHolder.textView.setVisibility(View.INVISIBLE);
                return false;
            }
        }).apply(requestOptions).transition(DrawableTransitionOptions.withCrossFade()).into(stickerViewHolder.imageView);
        stickerViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (service != null) {
                    service.postSticker((Sticker) stickerViewHolder.itemView.getTag(), saveHistory(), false);
                }
            }
        });
        // LongClick to force sending stickers as (A)PNG files
        stickerViewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (service != null) {
                    Toast.makeText(context, context.getString(R.string.force_png), Toast.LENGTH_SHORT).show();
                    service.postSticker((Sticker) stickerViewHolder.itemView.getTag(), saveHistory(), true);
                }
                return true;
            }
        });
    }


    protected abstract File getFile(Context context, int position);

    protected abstract Sticker getSticker(int position);

    protected abstract boolean saveHistory();

    // ViewHolder
    private class StickerViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        StickerViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.textView);
            textView = itemView.findViewById(R.id.status);
        }
    }
}
