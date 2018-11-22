package io.github.jeffshee.linestickerkeyboard;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.jeffshee.apng2gif.Apng2Gif;
import io.github.jeffshee.linestickerkeyboard.Model.Sticker;
import io.github.jeffshee.linestickerkeyboard.Model.StickerPack;
import io.github.jeffshee.linestickerkeyboard.Util.NewSharedPrefHelper;

public class FetchService extends IntentService {
    private static final String ACTION_FETCH = "io.github.jeffshee.linestickerkeyboard.action.FETCH";
    private static final String EXTRA_PARAM1 = "io.github.jeffshee.linestickerkeyboard.extra.PARAM1";

    private static final String CHANNEL_ID = "DOWNLOAD";
    private static final int NOTIFICATION_ID = 0;

    private static final String URL_COMMON = "https://stickershop.line-scdn.net/stickershop/v1/sticker/";
    private static final String STATIC_URL_FORMAT = URL_COMMON + "%d/IOS/sticker@2x.png;compress=true";
    private static final String ANIMATED_URL_FORMAT = URL_COMMON + "%d/IOS/sticker_animation@2x.png;compress=true";
    private static final String POPUP_URL_FORMAT = URL_COMMON + "%d/IOS/sticker_popup.png;compress=true";

    public static final String BROADCAST_ACTION = "io.github.jeffshee.linestickerkeyboard.REFRESH";

    int firstId = 0, count = 0, storeId = 0;
    String title = "";
    Sticker.Type type;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;
    File pngDir;
    File gifDir;
    Apng2Gif apng2Gif;

    public FetchService() {
        super("FetchService");
    }

    public static void startActionFetch(Context context, String lineShare) {
        Intent intent = new Intent(context, FetchService.class);
        intent.setAction(ACTION_FETCH);
        intent.putExtra(EXTRA_PARAM1, lineShare);
        context.startService(intent);
    }

    public static void startActionFetchManual(Context context, int storeId) {
        Intent intent = new Intent(context, FetchService.class);
        intent.setAction(ACTION_FETCH);
        intent.putExtra(EXTRA_PARAM1,
                String.format(Locale.getDefault(), "https://line.me/S/sticker/%d", storeId));
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        notificationManager = NotificationManagerCompat.from(this);
        pngDir = new File(getFilesDir(), "png");
        gifDir = new File(getFilesDir(), "gif");
        apng2Gif = new Apng2Gif();

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handleActionFetch(param1);
            }
        }
    }

    private void handleActionFetch(String param1) {
        String resultMsg;
        builder.setContentTitle("Fetching").setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true).setProgress(0, 0, true);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        if (fetch(param1)) {
            builder.setContentTitle("Downloading").setSmallIcon(R.mipmap.ic_launcher_round)
                    .setOngoing(true).setProgress(0, 0, true);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
            if (download()) {
                if (type == Sticker.Type.STATIC) {
                    NewSharedPrefHelper.addNewStickerPack(this,
                            new StickerPack(new Sticker(type, firstId), count, storeId, title));
                    resultMsg = "Operation completed";
                    send();
                } else {
                    builder.setContentTitle("Converting").setSmallIcon(R.mipmap.ic_launcher_round)
                            .setOngoing(true).setProgress(0, 0, true);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                    if (convert()) {
                        NewSharedPrefHelper.addNewStickerPack(this,
                                new StickerPack(new Sticker(type, firstId), count, storeId, title));
                        resultMsg = "Operation completed";
                        send();
                    } else resultMsg = "Convert failed";
                }
            } else resultMsg = "Download failed";
        } else resultMsg = "Fetch failed";
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(resultMsg).setSmallIcon(R.mipmap.ic_launcher_round).setOngoing(false);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private boolean fetch(String param1) {
        /*
         * To identify the type of sticker,
         * check whether the class is present or not in mdCMN08Img
         * static-normal : null              => 0
         * static-voice : MdIcoSound_b       => 0
         * anim-normal : MdIcoPlay_b         => 1
         * anim-voice : MdIcoAni_b           => 1
         * popup-normal : MdIcoFlash_b       => 2
         * popup-voice : MdIcoFlashAni_b     => 2
         */

        Pattern pattern = Pattern.compile("https://line.me/S/sticker/(\\d++)");
        Matcher matcher = pattern.matcher(param1);
        if (matcher.find()) {
            try {
                final String url = matcher.group(0);
                Document document = Jsoup.connect(url).get();
                // Check type
                Element element = document.getElementsByClass("mdCMN08Img").first();
                if (element.children().hasClass("MdIcoPlay_b") || element.children().hasClass("MdIcoAni_b")) {
                    Log.d("Fetcher", "Animated type detected");
                    type = Sticker.Type.ANIMATED;
                } else if (element.children().hasClass("MdIcoFlash_b") || element.children().hasClass("MdIcoFlashAni_b")) {
                    Log.d("Fetcher", "Popup type detected");
                    type = Sticker.Type.POPUP;
                } else {
                    type = Sticker.Type.STATIC;
                    Log.d("Fetcher", "Static type detected");
                }
                // Get firstId and count
                Elements elements = document.getElementsByClass("mdCMN09Image");
                if (elements.size() > 0) {
                    String s = elements.first().attr("style");
                    count = elements.size();
                    pattern = Pattern.compile("/(\\d+?)/");
                    matcher = pattern.matcher(s);
                    if (matcher.find()) {
                        firstId = Integer.valueOf(matcher.group(1));
                        Log.d("Fetcher", "firstId: " + firstId + " count: " + count);
                        // Get Title ;)
                        element = document.getElementsByClass("mdCMN08Ttl").first();
                        title = element.text();
                        // Get StoreId ;)
                        storeId = Integer.parseInt(url.substring(26));
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean download() {
        if (pngDir.mkdirs()) Log.d("Download", "pngDir created");

        for (int id = firstId; id < firstId + count; id++) {
            final File outputFile = new File(pngDir, String.valueOf(id) + ".png");
            final byte[] buffer = new byte[1024];
            InputStream inputStream = null;
            OutputStream outputStream = null;
            URL url;
            try {
                try {
                    switch (type) {
                        case STATIC:
                            url = new URL(String.format(Locale.getDefault(), STATIC_URL_FORMAT, id));
                            break;
                        case ANIMATED:
                            url = new URL(String.format(Locale.getDefault(), ANIMATED_URL_FORMAT, id));
                            break;
                        case POPUP:
                            url = new URL(String.format(Locale.getDefault(), POPUP_URL_FORMAT, id));
                            break;
                        default:
                            continue;
                    }
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.connect();
                    outputStream = new FileOutputStream(outputFile);
                    inputStream = urlConnection.getInputStream();
                    while (true) {
                        final int numRead = inputStream.read(buffer);
                        if (numRead <= 0) {
                            break;
                        }
                        outputStream.write(buffer, 0, numRead);
                    }
                    builder.setContentTitle(String.format(Locale.getDefault(), "Downloading (%d/%d)", id - firstId + 1, count))
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setOngoing(true).setProgress(count, id - firstId + 1, false);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                    Log.d("Downloader", String.valueOf(id) + " downloaded");
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.flush();
                        outputStream.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean convert() {
        if (gifDir.mkdirs()) Log.d("Download", "gifDir created");
        File png, gif;
        for (int id = firstId; id < firstId + count; id++) {
            png = new File(pngDir, String.valueOf(id) + ".png");
            gif = new File(gifDir, String.valueOf(id) + ".gif");
            apng2Gif.start(png, gif);
            builder.setContentTitle(String.format(Locale.getDefault(), "Converting (%d/%d)", id - firstId + 1, count))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setOngoing(true).setProgress(count, id - firstId + 1, false);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
        return true;
    }

    private void send() {
        Intent intent = new Intent();
        intent.setAction(BROADCAST_ACTION);
        intent.putExtra("message", "add");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
