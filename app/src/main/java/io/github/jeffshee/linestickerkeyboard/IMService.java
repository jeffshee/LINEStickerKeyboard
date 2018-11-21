
package io.github.jeffshee.linestickerkeyboard;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v13.view.inputmethod.EditorInfoCompat;
import android.support.v13.view.inputmethod.InputConnectionCompat;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;

import io.github.jeffshee.linestickerkeyboard.Model.Sticker;
import io.github.jeffshee.linestickerkeyboard.Util.FileHelper;
import io.github.jeffshee.linestickerkeyboard.View.StickerKeyboardView;

public class IMService extends InputMethodService {

    private static final String TAG = "ImageKeyboard";
    private static final String AUTHORITY = "io.github.jeffshee.linestickerkeyboard";
    private static final String MIME_TYPE_PNG = "image/png";
    private static final String MIME_TYPE_GIF = "image/gif";
    private StickerKeyboardView stickerKeyboardView;
    Receiver receiver;
    Context context = this;
    /* ImageKeyboard Google Samples
     * https://github.com/googlesamples/android-CommitContentSampleIME/
     */
    public void postSticker(Sticker sticker, boolean saveHistory) {
        boolean isPng = isCommitContentSupported(MIME_TYPE_PNG);
        boolean isGif = isCommitContentSupported(MIME_TYPE_GIF);
        if (sticker.getType() == Sticker.Type.STATIC) {
            if (isPng) {
                File file = FileHelper.getFile(this, sticker);
                doCommitContent(file, MIME_TYPE_PNG);
                if (saveHistory) stickerKeyboardView.addNewItemToHistory(sticker);
            } else {
                Toast.makeText(this, getString(R.string.not_supported), Toast.LENGTH_SHORT).show();
                File file = FileHelper.getFile(this, sticker);
                createShareIntent(file, MIME_TYPE_PNG);
                if (saveHistory) stickerKeyboardView.addNewItemToHistory(sticker);
            }
        } else {
            if (isGif) {
                File file = FileHelper.getFile(this, sticker);
                doCommitContent(file, MIME_TYPE_GIF);
                if (saveHistory) stickerKeyboardView.addNewItemToHistory(sticker);
            } else if (isPng) {
                File file = FileHelper.getPngFile(this, sticker.getId());
                doCommitContent(file, MIME_TYPE_PNG);
                if (saveHistory) stickerKeyboardView.addNewItemToHistory(sticker);
            } else {
                Toast.makeText(this, getString(R.string.not_supported), Toast.LENGTH_SHORT).show();
                File file = FileHelper.getFile(this, sticker);
                createShareIntent(file, MIME_TYPE_GIF);
                if (saveHistory) stickerKeyboardView.addNewItemToHistory(sticker);
            }
        }
    }

    private void createShareIntent(File file, String mimeType) {
        final EditorInfo editorInfo = getCurrentInputEditorInfo();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setPackage(editorInfo.packageName);
        shareIntent.setType(mimeType);
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, "io.github.jeffshee.linestickerkeyboard", file));
        startActivity(Intent.createChooser(shareIntent,getString(R.string.share)));
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
        for (String supportedMimeType : supportedMimeTypes) Log.d("Supported", supportedMimeType);
        for (String supportedMimeType : supportedMimeTypes) {
            if (ClipDescription.compareMimeTypes(mimeType, supportedMimeType)) {
                return true;
            }
        }
        return false;
    }

    private void doCommitContent(@NonNull File file, String mimeType) {
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
                new ClipDescription("lineSticker", new String[]{mimeType}), null);
        InputConnectionCompat.commitContent(
                getCurrentInputConnection(), getCurrentInputEditorInfo(), inputContentInfoCompat,
                flag, null);
    }

    @Override
    public View onCreateInputView() {
        stickerKeyboardView = new StickerKeyboardView(this);
        return stickerKeyboardView;
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {

        super.onStartInput(attribute, restarting);
    }

    /*
                https://stackoverflow.com/questions/3494476/android-ime-how-to-show-a-pop-up-dialog
                I JUST WANT TO SHOW A DIALOG ON MY KEYBOARD WHY IT IS SO F*KING DIFFICULT LOL??!? T^T
                https://stackoverflow.com/questions/51906586/display-dialog-from-input-method-service-in-android-9-android-pie
                NOTE: Might causing bug on Android 9
                TODO: Confirmation Required
             */
    public void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setItems(R.array.settings_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(IMService.this, MainActivity.class);
                                IMService.this.startActivity(intent);
                                break;
                            case 1:
                                InputMethodManager im = (InputMethodManager) IMService.this.getSystemService(INPUT_METHOD_SERVICE);
                                if (im != null) {
                                    im.showInputMethodPicker();
                                }
                                break;
                        }
                    }
                }).setIcon(R.mipmap.ic_launcher);
        AlertDialog dialog = builder.create();

        // Workaround for IMService + AlertDialog
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp;
        if (window != null) {
            lp = window.getAttributes();
            lp.token = stickerKeyboardView.getWindowToken();
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.show();
        }
        //
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Broadcast Receiver
        IntentFilter filter = new IntentFilter(FetchService.BROADCAST_ACTION);
        receiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(stickerKeyboardView!=null)
                stickerKeyboardView.refreshViewPager(context);
        }
    }
}

