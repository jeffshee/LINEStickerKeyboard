package io.github.jeffshee.linestickerkeyboard.Model;

public class StickerPack {
    private Sticker firstSticker;
    private int count;
    private int storeId;  //stub
    private String title; //stub

    public StickerPack(Sticker firstSticker, int count) {
        this.firstSticker = firstSticker;
        this.count = count;
    }

    public StickerPack(Sticker firstSticker, int count, String title) {
        this.firstSticker = firstSticker;
        this.count = count;
        this.title = title;
    }

    public StickerPack(Sticker firstSticker, int count, int storeId, String title) {
        this.firstSticker = firstSticker;
        this.count = count;
        this.storeId = storeId;
        this.title = title;
    }

    public int getFirstId() {
        return firstSticker.getId();
    }

    public int getId(int index) {
        return firstSticker.getId() + index;
    }

    public Sticker.Type getType() {
        return firstSticker.getType();
    }

    public Sticker getSticker(int index) {
        return new Sticker(firstSticker.getType(), firstSticker.getId() + index);
    }

    public int getCount() {
        return count;
    }

    public int getStoreId() {
        return storeId;
    }

    public String getTitle() {
        return title;
    }
}
