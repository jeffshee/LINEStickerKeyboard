package io.github.jeffshee.linestickerkeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class FetchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
            FetchService.startActionFetch(this, intent.getStringExtra(Intent.EXTRA_TEXT));
        }
        Toast.makeText(this, getString(R.string.fetch_activity_toast), Toast.LENGTH_SHORT).show();
        finish();
    }


}


