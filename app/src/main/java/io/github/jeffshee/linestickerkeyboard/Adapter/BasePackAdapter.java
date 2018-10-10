package io.github.jeffshee.linestickerkeyboard.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import io.github.jeffshee.linestickerkeyboard.IMService;
import io.github.jeffshee.linestickerkeyboard.R;

public abstract class BasePackAdapter extends RecyclerView.Adapter {

    static final String URL_F = "https://sdl-stickershop.line.naver.jp/stickershop/v1/sticker/";
    static final String URL_B = "/android/sticker.png;compress=true";
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
        stickerViewHolder.itemView.setTag(getIdForTag(position));
        stickerViewHolder.textView.setText(R.string.loading);
        stickerViewHolder.textView.setTextColor(context.getResources().getColor(R.color.colorLoading));

        RequestOptions requestOptions = new RequestOptions().error(R.drawable.error);
        RequestBuilder<Drawable> requestBuilder = Glide.with(context).load(getPreviewUrl(position));
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
                    service.postSticker((int) stickerViewHolder.itemView.getTag(), saveHistory());
                }
            }
        });
    }

    protected abstract String getPreviewUrl(int position);

    protected abstract int getIdForTag(int position);

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
