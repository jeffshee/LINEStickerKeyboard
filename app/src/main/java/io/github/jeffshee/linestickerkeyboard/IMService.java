
package io.github.jeffshee.linestickerkeyboard;

import android.content.ClipDescription;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v13.view.inputmethod.EditorInfoCompat;
import android.support.v13.view.inputmethod.InputConnectionCompat;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import io.github.jeffshee.linestickerkeyboard.View.StickerKeyboardView;

public class IMService extends InputMethodService {

    private static final String TAG = "ImageKeyboard";
    private static final String AUTHORITY = "io.github.jeffshee.linestickerkeyboard";
    private static final String MIME_TYPE_PNG = "image/png";
    private static final String URL_F = "https://sdl-stickershop.line.naver.jp/stickershop/v1/sticker/";
    private static final String URL_B = "/android/sticker.png;compress=true";

    /* ImageKeyboard Google Samples
     * https://github.com/googlesamples/android-CommitContentSampleIME/
     */

    public void postSticker(final int id) {
        if (isCommitContentSupported(MIME_TYPE_PNG)) {
            File imagesDir = new File(getFilesDir(), "images");
            if (imagesDir.mkdirs())
                Log.d("Service", "Dir created successfully");
            final File outputFile = new File(imagesDir, String.valueOf(id) + ".png");
            final byte[] buffer = new byte[4096];
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream inputStream = null;
                    try {
                        try {
                            OutputStream outputStream = null;
                            try {
                                URL url = new URL(URL_F + id + URL_B);
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
                                Log.d("Service", String.valueOf(id) + " downloaded");
                                doCommitContent(String.valueOf(id), MIME_TYPE_PNG, outputFile);
                            } finally {
                                if (outputStream != null) {
                                    outputStream.flush();
                                    outputStream.close();
                                }
                            }
                        } finally {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "Not supported", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCommitContentSupported(String mimeType) {
        final EditorInfo editorInfo = getCurrentInputEditorInfo();
        if (editorInfo == null) {
            return false;
        }

        final InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return false;
        }

        final String[] supportedMimeTypes = EditorInfoCompat.getContentMimeTypes(editorInfo);
        for (String supportedMimeType : supportedMimeTypes) {
            if (ClipDescription.compareMimeTypes(mimeType, supportedMimeType)) {
                return true;
            }
        }
        return false;
    }

    private void doCommitContent(@NonNull String description, @NonNull String mimeType,
                                 @NonNull File file) {
        final EditorInfo editorInfo = getCurrentInputEditorInfo();
        final Uri contentUri = FileProvider.getUriForFile(this, AUTHORITY, file);
        final int flag;
        if (Build.VERSION.SDK_INT >= 25) {
            flag = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
        } else {
            flag = 0;
            try {
                grantUriPermission(
                        editorInfo.packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception e) {
                Log.e(TAG, "grantUriPermission failed packageName=" + editorInfo.packageName
                        + " contentUri=" + contentUri, e);
            }
        }

        final InputContentInfoCompat inputContentInfoCompat = new InputContentInfoCompat(
                contentUri,
                new ClipDescription(description, new String[]{mimeType}),
                null);
        InputConnectionCompat.commitContent(
                getCurrentInputConnection(), getCurrentInputEditorInfo(), inputContentInfoCompat,
                flag, null);
    }

    @Override
    public View onCreateInputView() {
        return new StickerKeyboardView(this);
    }
}

