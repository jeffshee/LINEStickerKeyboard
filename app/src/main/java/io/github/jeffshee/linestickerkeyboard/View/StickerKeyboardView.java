
package io.github.jeffshee.linestickerkeyboard.View;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import io.github.jeffshee.linestickerkeyboard.Adapter.StickerViewPagerAdapter;
import io.github.jeffshee.linestickerkeyboard.IMService;
import io.github.jeffshee.linestickerkeyboard.Model.HistoryPack;
import io.github.jeffshee.linestickerkeyboard.Model.Sticker;
import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;
import io.github.jeffshee.linestickerkeyboard.R;
import io.github.jeffshee.linestickerkeyboard.Util.FileHelper;
import io.github.jeffshee.linestickerkeyboard.Util.SharedPrefHelper;

/* Summary
    IMService <-> StickerKeyboardView -- [ HistoryPackView <- HistoryAdapter <- HistoryPack
             StickerViewPagerAdapter -^  [ StickerPackView <- StickerAdapter <- StickerPack
                                         [      ``                  ``              ``
 */
public class StickerKeyboardView extends LinearLayout implements View.OnClickListener {
    ArrayList<View> views = new ArrayList<>();
    HistoryPackView historyPackView;
    private HistoryPack historyPack;
    private Context imService;
    private View panel;

    public StickerKeyboardView(Context context) {
        super(context);
        this.imService = context;
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // https://stackoverflow.com/questions/36924481/you-need-to-use-a-theme-appcompat-theme-or-descendant-with-the-design-library
        ContextThemeWrapper ctx = new ContextThemeWrapper(imService, R.style.MyTheme);
        LayoutInflater.from(ctx).inflate(R.layout.keyboard_layout, this, true);

        // History Pack
        historyPack = SharedPrefHelper.getHistoryFromPref(imService);
        historyPackView = new HistoryPackView(imService, historyPack);
        views.add(historyPackView);

        // Sticker Pack
        ArrayList<StickerPack> stickerPacksFiltered = new ArrayList<>();
        for (StickerPack stickerPack : SharedPrefHelper.getStickerPacksFromPref(imService)) {
            if (stickerPack.getVisible()) {
                stickerPacksFiltered.add(stickerPack);
                views.add(new StickerPackView(imService, stickerPack));
            }
        }

        // TabLayout
        ViewPager viewPager = findViewById(R.id.container);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        StickerViewPagerAdapter adapter = new StickerViewPagerAdapter(views);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        LayoutInflater inflater = (LayoutInflater) imService.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View view;
            if (inflater != null) {
                view = inflater.inflate(R.layout.item_tab_icon, null);
                ImageView icon = view.findViewById(R.id.textView);
                if (i == 0)
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.baseline_history_white_36));
                else {
                    File file = FileHelper.getPngFile(imService, stickerPacksFiltered.get(i - 1).getFirstId());
                    Glide.with(this).load(file).into(icon);
                }
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) tab.setCustomView(view);
            }
        }

        // Default Tab
        if (historyPack.size() != 0) {
            viewPager.setCurrentItem(0);
        } else if (stickerPacksFiltered.size() > 0) {
            viewPager.setCurrentItem(1);
        }

        // Setting Button
        ImageButton button = findViewById(R.id.imageButton);
        button.setOnClickListener(this);
        button.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (imService instanceof IMService) ((IMService) imService).showIMPicker();
                return true;
            }
        });

        // Setting Panel and buttons
        panel = findViewById(R.id.panel);
        panel.setVisibility(INVISIBLE);
        Button btnMenu = findViewById(R.id.btnMenu);
        Button btnSwitch = findViewById(R.id.btnSwitch);
        panel.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        btnSwitch.setOnClickListener(this);
    }

    public void refreshHistoryAdapter(Sticker sticker) {
        historyPack.add(sticker);
        historyPackView.adapter.update(historyPack);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.panel:
                panel.setVisibility(INVISIBLE);
                break;

            case R.id.imageButton:
                panel.setVisibility(VISIBLE);
                // Fancy background fading ;)
                ObjectAnimator colorFade = ObjectAnimator.ofObject(panel, "backgroundColor",
                        new ArgbEvaluator(),
                        Color.argb(100, 0, 0, 0),
                        Color.argb(200, 0, 0, 0));
                colorFade.setDuration(300);
                colorFade.start();
                break;

            case R.id.btnMenu:
                if (imService instanceof IMService) ((IMService) imService).launchMainMenu();
                panel.setVisibility(INVISIBLE);
                break;

            case R.id.btnSwitch:
                if (imService instanceof IMService) ((IMService) imService).showIMPicker();
                panel.setVisibility(INVISIBLE);
                break;
        }
    }
}

