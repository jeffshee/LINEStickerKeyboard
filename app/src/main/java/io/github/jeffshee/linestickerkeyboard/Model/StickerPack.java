package io.github.jeffshee.linestickerkeyboard.Model;

public class StickerPack {
    private int id, count;

    public StickerPack(int id, int count) {
        this.id = id;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }
}
