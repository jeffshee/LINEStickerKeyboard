package io.github.jeffshee.linestickerkeyboard.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HistoryPack {
    private static final int MAX_SIZE = 100;
    private ArrayList<Sticker> stickers;

    public HistoryPack(ArrayList<Sticker> history) {
        this.stickers = history;
    }

    public void add(Sticker sticker) {
        stickers.remove(sticker);
        stickers.add(0, sticker);
        if (stickers.size() > MAX_SIZE) {
            stickers.remove(MAX_SIZE);
        }
    }

    public void remove(int index) {
        stickers.remove(index);
    }

    public void removeAll(ArrayList<Integer> ids) {
        ArrayList<Sticker> removeList = new ArrayList<>();
        for (Sticker sticker : stickers) {
            if (ids.contains(sticker.getId())) {
                removeList.add(sticker);
            }
        }
        stickers.removeAll(removeList);
    }

    public int getId(int index) {
        return stickers.get(index).getId();
    }

    public Sticker.Type getType(int index) {
        return stickers.get(index).getType();
    }

    public Sticker getSticker(int index) {
        return stickers.get(index);
    }

    public void clear() {
        stickers.clear();
    }

    public int size() {
        return stickers.size();
    }
}
