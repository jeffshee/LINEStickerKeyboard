
package io.github.jeffshee.linestickerkeyboard.View;

import android.content.Context;
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

import java.util.ArrayList;

import io.github.jeffshee.linestickerkeyboard.Adapter.StickerViewPagerAdapter;
import io.github.jeffshee.linestickerkeyboard.IMService;
import io.github.jeffshee.linestickerkeyboard.Model.HistoryPack;
import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;
import io.github.jeffshee.linestickerkeyboard.R;
import io.github.jeffshee.linestickerkeyboard.Util.SharedPrefHelper;

import static android.content.Context.INPUT_METHOD_SERVICE;

/* Summary
    IMService <-> StickerKeyboardView -- [ HistoryPackView <- HistoryAdapter <- HistoryPack
             StickerViewPagerAdapter -^  [ StickerPackView <- StickerAdapter <- StickerPack
                                         [      ``                  ``              ``
 */
public class StickerKeyboardView extends LinearLayout {
    private static final String URL_F = "https://sdl-stickershop.line.naver.jp/stickershop/v1/sticker/";
    private static final String URL_B = "/android/sticker.png;compress=true";
    ArrayList<View> views = new ArrayList<>();
    HistoryPackView historyPackView;
    private HistoryPack historyPack;
    private ArrayList<StickerPack> stickerPacks;
    private SharedPrefHelper helper;
    private StickerViewPagerAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public StickerKeyboardView(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        helper = new SharedPrefHelper(context);

        // https://stackoverflow.com/questions/36924481/you-need-to-use-a-theme-appcompat-theme-or-descendant-with-the-design-library
        ContextThemeWrapper ctx = new ContextThemeWrapper(context, R.style.MyTheme);
        LayoutInflater.from(ctx).inflate(R.layout.keyboard_layout, this, true);

        // History Pack
        historyPack = helper.getHistoryFromPref();
        historyPackView = new HistoryPackView(context, historyPack);
        views.add(historyPackView);

        // Sticker Pack
        stickerPacks = helper.getStickerPacksFromPref();
        for (StickerPack stickerPack : stickerPacks) {
            views.add(new StickerPackView(context, stickerPack));
        }

        // TabLayout
        viewPager = findViewById(R.id.container);
        tabLayout = findViewById(R.id.tabLayout);

        adapter = new StickerViewPagerAdapter(views);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View view;
            if (inflater != null) {
                view = inflater.inflate(R.layout.item_tab_icon, null);
                ImageView icon = view.findViewById(R.id.textView);
                if (i == 0)
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.baseline_history_white_36));
                else
                    Glide.with(this).load(URL_F + String.valueOf(stickerPacks.get(i - 1).getFirstId()) + URL_B).into(icon);
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) tab.setCustomView(view);
            }
        }

        // Default Tab
        if (historyPack.size() != 0) {
            viewPager.setCurrentItem(0);
        } else if (stickerPacks.size() > 0) {
            viewPager.setCurrentItem(1);
        }

        // Setting Button
        ImageButton button = findViewById(R.id.imageButton);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof IMService) {
                    ((IMService) context).showSettingDialog();
                }
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

    public void addNewItemToHistory(int id) {
        historyPack.add(id);
        helper.saveNewHistoryPack(historyPack);
        historyPackView.adapter.update(historyPack);
    }

    public void refreshViewPager(Context context){
        views = new ArrayList<>();

        // History Pack
        historyPack = helper.getHistoryFromPref();
        historyPackView = new HistoryPackView(context, historyPack);
        views.add(historyPackView);

        // Sticker Pack
        stickerPacks = helper.getStickerPacksFromPref();
        for (StickerPack stickerPack : stickerPacks) {
            views.add(new StickerPackView(context, stickerPack));
        }

        adapter.update(views);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View view;
            if (inflater != null) {
                view = inflater.inflate(R.layout.item_tab_icon, null);
                ImageView icon = view.findViewById(R.id.textView);
                if (i == 0)
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.baseline_history_white_36));
                else
                    Glide.with(this).load(URL_F + String.valueOf(stickerPacks.get(i - 1).getFirstId()) + URL_B).into(icon);
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) tab.setCustomView(view);
            }
        }

        // Default Tab
        if (historyPack.size() != 0) {
            viewPager.setCurrentItem(0);
        } else if (stickerPacks.size() > 0) {
            viewPager.setCurrentItem(1);
        }
    }
}

