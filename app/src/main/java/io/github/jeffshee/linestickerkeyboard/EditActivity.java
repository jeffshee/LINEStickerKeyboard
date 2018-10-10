package io.github.jeffshee.linestickerkeyboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import io.github.jeffshee.linestickerkeyboard.Adapter.AdapterCallback;
import io.github.jeffshee.linestickerkeyboard.Adapter.ListAdapter;
import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;
import io.github.jeffshee.linestickerkeyboard.Util.SharedPrefHelper;

public class EditActivity extends AppCompatActivity {

    private ArrayList<StickerPack> stickerPacks;
    private SharedPrefHelper helper;
    private Activity activity;
    private ListAdapter listAdapter;
    private RecyclerView recyclerView;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        // Get intent
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
            dialog = new SpotsDialog.Builder().setContext(this).setMessage("Fetching sticker").setCancelable(false).build();
            dialog.show();
            new Fetcher().execute(intent.getStringExtra(Intent.EXTRA_TEXT));
        }

        activity = this;
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditorDialog(activity, 0, 0);
            }
        });

        helper = new SharedPrefHelper(this);
        stickerPacks = helper.getStickerPacksFromPref();
        listAdapter = new ListAdapter(this, stickerPacks);
        ItemTouchHelper.Callback callback = new AdapterCallback(listAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(listAdapter);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save change?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        helper.saveNewStickerPacks(stickerPacks);
                        Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).show();
    }

    private void showEditorDialog(Context context, int firstId, int count) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_editor, null, false);
        builder.setView(view);
        final EditText etId = view.findViewById(R.id.etId);
        final EditText etCount = view.findViewById(R.id.etCount);
        if (firstId != 0) etId.setText(String.valueOf(firstId));
        if (count != 0) etCount.setText(String.valueOf(count));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int id, count;
                try {
                    id = Integer.parseInt(etId.getText().toString());
                    count = Integer.parseInt(etCount.getText().toString());
                } catch (NumberFormatException e) {
                    return;
                }
                StickerPack stickerPack = new StickerPack(id, count);
                stickerPacks.add(0, stickerPack);
                listAdapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Leak warning and possible solution:
    // https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur
    private class Fetcher extends AsyncTask<String, Integer, int[]> {

        @Override
        protected int[] doInBackground(String... strings) {
            Pattern pattern = Pattern.compile("https://line.me/S/sticker/(\\d++)");
            Matcher matcher = pattern.matcher(strings[0]);
            if (matcher.find()) {
                try {
                    final String url = matcher.group(0);
                    Document document = Jsoup.connect(url).get();
                    Elements elements = document.getElementsByClass("mdCMN09Image");
                    if (elements.size() > 0) {
                        int firstId = 0, count;
                        String s = elements.get(0).attr("style");
                        count = elements.size();
                        pattern = Pattern.compile("/(\\d+?)/");
                        matcher = pattern.matcher(s);
                        if (matcher.find()) firstId = Integer.valueOf(matcher.group(1));
                        int[] result = new int[2];
                        result[0] = firstId;
                        result[1] = count;
                        return result;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(int[] result) {
            if (result != null)
                showEditorDialog(activity, result[0], result[1]);
            else
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }
}
