
package io.github.jeffshee.linestickerkeyboard.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.github.jeffshee.linestickerkeyboard.Adapter.StickerViewPagerAdapter;
import io.github.jeffshee.linestickerkeyboard.Model.HistoryPack;
import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;
import io.github.jeffshee.linestickerkeyboard.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class StickerKeyboardView extends LinearLayout {
    private static final String URL_F = "https://sdl-stickershop.line.naver.jp/stickershop/v1/sticker/";
    private static final String URL_B = "/android/sticker.png;compress=true";
    private static final String SHARED_PREF = "linestickerkeyboard.pref";
    private static final String KEY = "linestickerkeyboard.pref.history";
    ArrayList<StickerPack> stickerPacks = new ArrayList<>();
    ArrayList<View> views = new ArrayList<>();
    HistoryPackView historyPackView;
    private HistoryPack historyPack;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    public StickerKeyboardView(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        // https://stackoverflow.com/questions/36924481/you-need-to-use-a-theme-appcompat-theme-or-descendant-with-the-design-library
        ContextThemeWrapper ctx = new ContextThemeWrapper(context, R.style.MyTheme);
        LayoutInflater.from(ctx).inflate(R.layout.keyboard_layout, this, true);

        // History Pack
        historyPackView = new HistoryPackView(ctx, getHistoryFromPref());
        views.add(historyPackView);

        // Sticker Pack
        loadDummyStickers();
        for (StickerPack stickerPack : stickerPacks) {
            views.add(new StickerPackView(ctx, stickerPack));
        }

        // TabLayout
        ViewPager viewPager = findViewById(R.id.container);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ImageButton button = findViewById(R.id.imageButton);
        viewPager.setAdapter(new StickerViewPagerAdapter(views));

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View view;
            if (inflater != null) {
                view = inflater.inflate(R.layout.item_tab_icon, null);
                ImageView icon = view.findViewById(R.id.imageView);
                if (i == 0)
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.baseline_history_white_36));
                else
                    Glide.with(this).load(URL_F + String.valueOf(stickerPacks.get(i - 1).getFirstId()) + URL_B).into(icon);
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) tab.setCustomView(view);
            }
        }

        // Default Tab
        if (views.size() > 1) {
            viewPager.setCurrentItem(1);
        }

        // Setting Button
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Setting OnClick
            }
        });
        button.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                InputMethodManager im = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                if (im != null) {
                    im.showInputMethodPicker();
                }
                return true;
            }
        });
    }

    private HistoryPack getHistoryFromPref() {
        String json = sharedPreferences.getString(KEY, "");
        if (json.equals("")) {
            historyPack = new HistoryPack(new ArrayList<Integer>());
        } else {
            historyPack = gson.fromJson(sharedPreferences.getString(KEY, null),
                    HistoryPack.class);
        }
        return historyPack;
    }

    public void saveNewItemToHistory(int id) {
        historyPack.add(id);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, gson.toJson(historyPack));
        editor.apply();
        historyPackView.postUpdate(historyPack);
    }

    private void loadDummyStickers() {
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
    }
}

