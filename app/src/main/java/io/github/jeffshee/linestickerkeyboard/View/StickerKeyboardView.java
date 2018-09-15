
package io.github.jeffshee.linestickerkeyboard.View;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.github.jeffshee.linestickerkeyboard.Adapter.StickerViewPagerAdapter;
import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;
import io.github.jeffshee.linestickerkeyboard.R;

public class StickerKeyboardView extends LinearLayout {
    private static final String URL_F = "https://sdl-stickershop.line.naver.jp/stickershop/v1/sticker/";
    private static final String URL_B = "/android/sticker.png;compress=true";

    ArrayList<StickerPack> stickerPacks = new ArrayList<>();
    ArrayList<View> views = new ArrayList<>();


    public StickerKeyboardView(Context context) {
        super(context);
        init(context);
    }

    public StickerKeyboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StickerKeyboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // https://stackoverflow.com/questions/36924481/you-need-to-use-a-theme-appcompat-theme-or-descendant-with-the-design-library
        ContextThemeWrapper ctx = new ContextThemeWrapper(context, R.style.MyTheme);
        LayoutInflater.from(ctx).inflate(R.layout.keyboard_layout, this, true);

        // TODO: Dummy stickers here
        stickerPacks.add(new StickerPack(6588224, 30));
        stickerPacks.add(new StickerPack(3008851, 30));
        stickerPacks.add(new StickerPack(6681024, 30));
        stickerPacks.add(new StickerPack(8031396, 30));
        stickerPacks.add(new StickerPack(13831750, 30));
        stickerPacks.add(new StickerPack(56014, 30));
        stickerPacks.add(new StickerPack(4235640, 30));
        stickerPacks.add(new StickerPack(18346658, 30));
        stickerPacks.add(new StickerPack(50231454, 24));
        stickerPacks.add(new StickerPack(30889672, 24));
        stickerPacks.add(new StickerPack(7115472, 24));

        for (StickerPack stickerPack : stickerPacks) {
            views.add(new StickerPackView(context, stickerPack.getId(), stickerPack.getCount()));
        }

        ViewPager viewPager = findViewById(R.id.container);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        StickerViewPagerAdapter adapter = new StickerViewPagerAdapter(views);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View view = null;
            if (inflater != null) {
                view = inflater.inflate(R.layout.item_tab_icon, null);
                ImageView icon = view.findViewById(R.id.imageView);
                Glide.with(this).load(URL_F + String.valueOf(stickerPacks.get(i).getId()) + URL_B).into(icon);
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) tab.setCustomView(view);
            }
        }
    }
}

