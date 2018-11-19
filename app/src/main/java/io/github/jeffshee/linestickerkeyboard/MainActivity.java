package io.github.jeffshee.linestickerkeyboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
import io.github.jeffshee.linestickerkeyboard.Util.SharedPrefHelper;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Activity activity = this;
    SharedPrefHelper sharedPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefHelper = new SharedPrefHelper(this);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.main_activity_list)));
        listView.setOnItemClickListener(this);
    }

    private static final String KEY_IS_MIME = "linestickerkeyboard.pref.mime";

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
            case 3:
                activity.startActivity(new Intent(activity, EditActivity.class));
                break;
            case 4:
                sharedPrefHelper.saveNewHistoryPack(new HistoryPack(new ArrayList<Sticker>()));
                break;
            default:
                // TODO:
                Toast.makeText(activity, "Stub", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
