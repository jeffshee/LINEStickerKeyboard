package io.github.jeffshee.linestickerkeyboard.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class StickerAdapter extends RecyclerView.Adapter {

    private static final String URL_F = "https://sdl-stickershop.line.naver.jp/stickershop/v1/sticker/";
    private static final String URL_B = "/android/sticker.png;compress=true";
    private int id, count;
    private Context context;
    private IMService service;

    public StickerAdapter(int id, int count, Context context) {
        this.id = id;
        this.count = count;
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final StickerViewHolder stickerViewHolder = (StickerViewHolder) holder;
        stickerViewHolder.itemView.setTag(position);
        String s = URL_F + String.valueOf(id + position) + URL_B;
        stickerViewHolder.textView.setText(R.string.loading);
        stickerViewHolder.textView.setTextColor(context.getResources().getColor(R.color.colorLoading));
        // NOTE: placeholder won't dismiss even after image loaded successfully, bug?
        RequestOptions requestOptions = new RequestOptions()/*.placeholder(R.drawable.placeholder)*/
                .error(R.drawable.error);
        RequestBuilder<Drawable> requestBuilder = Glide.with(context).load(s);
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
                Log.d("Test", "Test");
                if (service != null){
                    service.postSticker(id + (int) stickerViewHolder.itemView.getTag());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return count;
    }

    private class StickerViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        StickerViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.status);
        }
    }
}
