package io.github.jeffshee.linestickerkeyboard.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngReaderApng;
import ar.com.hjg.pngj.chunks.PngChunk;
import ar.com.hjg.pngj.chunks.PngChunkFCTL;
import io.github.jeffshee.apng2gif.ApngView.ApngExtractFrames;
import io.github.jeffshee.apng2gif.Glide.AnimatedGifEncoder;

/* Most of the sources are taken from ApngView (https://github.com/sahasbhop/apng-view)
 * and Glide (https://github.com/bumptech/glide)
 * All rights go to their respective owners
 *
 * The objective of this lib is to provide simple apng2gif functionality to Android platform
 * by integrating the sources taken from mentioned parties.
 *
 * Usage (example):
 *
 * File input, output;
 * // Initialize your input(apng) and output(gif)
 * // ...
 *
 * Apng2GifCustom apng2Gif = new Apng2GifCustom();
 * apng2Gif.start(input, output);
 *
 */

public class Apng2GifCustom {

    private File input;
    private File output;
    private ArrayList<PngChunkFCTL> fctlArrayList;
    private Bitmap[] bitmapArray;
    private int baseWidth, baseHeight;

    private static final float DELAY_FACTOR = 1000f;

    public void start(File apng, File gif) {
        input = apng;
        output = gif;
        fctlArrayList = new ArrayList<>();
        ApngExtractFrames.process(input);
        readApngInformation(input);
        prepareBitmaps();
        encodeGif();
        clean();
    }

    private void readApngInformation(File input) {
        PngReader reader = new PngReaderApng(input);
        reader.end();

        List<PngChunk> pngChunks = reader.getChunksList().getChunks();
        PngChunk chunk;

        for (int i = 0; i < pngChunks.size(); i++) {
            chunk = pngChunks.get(i);
            if (chunk instanceof PngChunkFCTL) {
                fctlArrayList.add((PngChunkFCTL) chunk);
            }
        }
        bitmapArray = new Bitmap[fctlArrayList.size()];
    }

    private void prepareBitmaps() {
        // Base Bitmap
        bitmapArray[0] = getFrameBitmap(0);
        baseWidth = bitmapArray[0].getWidth();
        baseHeight = bitmapArray[0].getHeight();

        // Animate Bitmap
        for (int i = 1; i < fctlArrayList.size(); i++) {
            Bitmap frameBitmap = getFrameBitmap(i);
            bitmapArray[i] = createAnimateBitmap(i, frameBitmap);
        }
    }

    private Bitmap createAnimateBitmap(int frameIndex, Bitmap frameBitmap) {
        PngChunkFCTL previousChunk = fctlArrayList.get(frameIndex - 1);
        Bitmap bitmap = handleDisposeOperation(frameIndex, previousChunk);

        Bitmap redrawnBitmap;

        PngChunkFCTL chunk = fctlArrayList.get(frameIndex);

        byte blendOp = chunk.getBlendOp();
        int offsetX = chunk.getxOff();
        int offsetY = chunk.getyOff();

        redrawnBitmap = handleBlendingOperation(offsetX, offsetY, blendOp, frameBitmap, bitmap);

        return redrawnBitmap;
    }

    private Bitmap handleDisposeOperation(int frameIndex, PngChunkFCTL previousChunk) {
        Bitmap bitmap = null;

        byte disposeOp = previousChunk.getDisposeOp();
        int offsetX = previousChunk.getxOff();
        int offsetY = previousChunk.getyOff();

        Canvas tempCanvas;
        Bitmap frameBitmap;
        Bitmap tempBitmap;

        switch (disposeOp) {
            case PngChunkFCTL.APNG_DISPOSE_OP_NONE:
                // Get bitmap from the previous frame
                bitmap = bitmapArray[frameIndex - 1];
                break;

            case PngChunkFCTL.APNG_DISPOSE_OP_BACKGROUND:
                // Get bitmap from the previous frame but the drawing region is needed to be cleared
                bitmap = bitmapArray[frameIndex - 1];
                if (bitmap == null) break;
                frameBitmap = getFrameBitmap(frameIndex - 1);

                tempBitmap = Bitmap.createBitmap(baseWidth, baseHeight, Bitmap.Config.ARGB_8888);
                tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(bitmap, 0, 0, null);

                tempCanvas.clipRect(offsetX, offsetY, offsetX + frameBitmap.getWidth(), offsetY + frameBitmap.getHeight());
                tempCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                tempCanvas.clipRect(0, 0, baseWidth, baseHeight);

                bitmap = tempBitmap;
                break;

            case PngChunkFCTL.APNG_DISPOSE_OP_PREVIOUS:
                if (frameIndex > 1) {
                    PngChunkFCTL tempPngChunk;

                    for (int i = frameIndex - 2; i >= 0; i--) {
                        tempPngChunk = fctlArrayList.get(i);
                        int tempDisposeOp = tempPngChunk.getDisposeOp();
                        int tempOffsetX = tempPngChunk.getxOff();
                        int tempOffsetY = tempPngChunk.getyOff();

                        frameBitmap = getFrameBitmap(i);

                        if (tempDisposeOp != PngChunkFCTL.APNG_DISPOSE_OP_PREVIOUS) {

                            if (tempDisposeOp == PngChunkFCTL.APNG_DISPOSE_OP_NONE) {
                                bitmap = bitmapArray[i];

                            } else if (tempDisposeOp == PngChunkFCTL.APNG_DISPOSE_OP_BACKGROUND) {
                                tempBitmap = Bitmap.createBitmap(baseWidth, baseHeight, Bitmap.Config.ARGB_8888);
                                tempCanvas = new Canvas(tempBitmap);
                                tempCanvas.drawBitmap(bitmapArray[i], 0, 0, null);

                                tempCanvas.clipRect(tempOffsetX, tempOffsetY, tempOffsetX + frameBitmap.getWidth(), tempOffsetY + frameBitmap.getHeight());
                                tempCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                                tempCanvas.clipRect(0, 0, baseWidth, baseHeight);

                                bitmap = tempBitmap;
                            }
                            break;
                        }
                    }
                }
                break;
        }
        return bitmap;
    }

    private Bitmap getFrameBitmap(int frameIndex) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(
                input.getParent() + File.separator + ApngExtractFrames.getFileName(input, frameIndex),
                options);
    }

    private Bitmap handleBlendingOperation(
            int offsetX, int offsetY, byte blendOp,
            Bitmap frameBitmap, Bitmap baseBitmap) {

        Bitmap redrawnBitmap = Bitmap.createBitmap(baseWidth, baseHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(redrawnBitmap);

        if (baseBitmap != null) {
            canvas.drawBitmap(baseBitmap, 0, 0, null);

            if (blendOp == PngChunkFCTL.APNG_BLEND_OP_SOURCE) {
                canvas.clipRect(offsetX, offsetY, offsetX + frameBitmap.getWidth(), offsetY + frameBitmap.getHeight());
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                canvas.clipRect(0, 0, baseWidth, baseHeight);
            }
        }

        canvas.drawBitmap(frameBitmap, offsetX, offsetY, null);

        return redrawnBitmap;
    }

    private void encodeGif() {
        AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        animatedGifEncoder.start(output.getPath());
        // Play indefinitely
        animatedGifEncoder.setRepeat(0);
        for (int i = 0; i < fctlArrayList.size(); i++) {
            PngChunkFCTL pngChunk = fctlArrayList.get(i);
            int delayNum = pngChunk.getDelayNum();
            int delayDen = pngChunk.getDelayDen();
            int delay = Math.round(delayNum * DELAY_FACTOR / delayDen);
            animatedGifEncoder.setDelay(delay);
            // Fill Background with white color, sorry transparency...
            animatedGifEncoder.addFrame(fillBackground(bitmapArray[i], Color.WHITE));
        }
        animatedGifEncoder.finish();
    }

    private Bitmap fillBackground(Bitmap bitmap, int color) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas tempCanvas = new Canvas(output);
        tempCanvas.drawColor(color);
        tempCanvas.drawBitmap(bitmap, 0, 0, null);
        return output;
    }

    private void clean() {
        File trash;
        for (int i = 0; i < fctlArrayList.size(); i++) {
            trash = new File(input.getParent(), ApngExtractFrames.getFileName(input, i));
            if (!trash.delete()) {
                Log.d("Apng2GifCustom", "Delete failed");
            }
        }
    }

}
