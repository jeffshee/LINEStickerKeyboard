package io.github.jeffshee.linestickerkeyboard.Model;

import java.util.ArrayList;

public class HistoryPack {
    private static final int MAX_SIZE = 50;
    private ArrayList<Sticker> stickers;

    public HistoryPack(ArrayList<Sticker> history) {
        this.stickers = history;
    }

    public void add(Sticker sticker) {
        for (int i = 0; i < stickers.size(); i++) {
            if (sticker.equal(stickers.get(i))) stickers.remove(i);
        }
        stickers.add(0, sticker);
        if (stickers.size() > MAX_SIZE) {
            stickers.remove(MAX_SIZE);
        }
    }

    public int getId(int index) {
        return stickers.get(index).getId();
    }

    public Sticker.Type getType(int index) {
        return stickers.get(index).getType();
    }

    public Sticker getSticker(int index){
        return stickers.get(index);
    }

    public void clear() {
        stickers.clear();
    }

    public int size() {
        return stickers.size();
    }
}
