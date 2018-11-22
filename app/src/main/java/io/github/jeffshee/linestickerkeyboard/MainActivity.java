package io.github.jeffshee.linestickerkeyboard;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.jeffshee.linestickerkeyboard.Model.HistoryPack;
import io.github.jeffshee.linestickerkeyboard.Model.Sticker;
import io.github.jeffshee.linestickerkeyboard.Util.NewSharedPrefHelper;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TRACKER = "https://github.com/jeffshee/LINEStickerKeyboard/issues";
    private static final String HP = "https://github.com/jeffshee/LINEStickerKeyboard";

    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disclaimer();
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.main_activity_list)));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                activity.startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
                break;
            case 1:
                InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (im != null) im.showInputMethodPicker();
                break;
            case 2:
                activity.startActivity(new Intent(activity, EditActivity.class));
                break;
            case 3:
                clearHistory();
                break;
            case 4:
                feedback();
                break;
            case 5:
                about();
                break;
            default:
                // TODO:
                Toast.makeText(activity, "Stub", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void clearHistory() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.clear_history))
                .setPositiveButton(getString(R.string.positive_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NewSharedPrefHelper.saveNewHistoryPack(activity, new HistoryPack(new ArrayList<Sticker>()));
                    }
                })
                .setNegativeButton(getString(R.string.negative_cancel), null);
        builder.show();
    }

    private void feedback() {
        Uri uri = Uri.parse(TRACKER);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void about() {
        Uri uri = Uri.parse(HP);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void disclaimer() {
        if(!NewSharedPrefHelper.getDisclaimerStatus(this)){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false).setTitle(getString(R.string.disclaimer))
                    .setMessage(getString(R.string.disclaimer_text))
                    .setPositiveButton(getString(R.string.positive_agree), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            NewSharedPrefHelper.saveDisclaimerStatus(activity, true);
                        }
                    })
                    .setNegativeButton(getString(R.string.negative_disagree), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            builder.show();
        }
    }
}
