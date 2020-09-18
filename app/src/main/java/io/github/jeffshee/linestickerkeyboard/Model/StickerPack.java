package io.github.jeffshee.linestickerkeyboard.Model;

import java.util.ArrayList;

public class StickerPack {
    private String title;
    private int storeId;
    private Sticker.Type type;
    private ArrayList<Integer> ids;
    private boolean visible;

    public StickerPack(String title, int storeId, Sticker.Type type, ArrayList<Integer> ids) {
        this.title = title;
        this.storeId = storeId;
        this.type = type;
        this.ids = ids;
        this.visible = true;
    }

    public int getId(int index) {
        return ids.get(index);
    }

    public ArrayList<Integer> getIds() {
        return ids;
    }

    public Sticker.Type getType() {
        return type;
    }

    public Sticker getSticker(int index) {
        return new Sticker(type, ids.get(index));
    }

    public int getCount() {
        return ids.size();
    }

    public int getStoreId() {
        return storeId;
    }

    public String getTitle() {
        return title;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean b) {
        visible = b;
    }
}
