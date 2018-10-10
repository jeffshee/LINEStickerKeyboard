package io.github.jeffshee.linestickerkeyboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnEnableIM = findViewById(R.id.btnEnableIM);
        Button btnEditSticker = findViewById(R.id.btnEditSticker);
        btnEnableIM.setOnClickListener(this);
        btnEditSticker.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEnableIM:
                activity.startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
                break;
            case R.id.btnEditSticker:
                activity.startActivity(new Intent(activity, EditActivity.class));
                break;
        }
    }
}
