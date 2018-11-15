package io.github.jeffshee.linestickerkeyboard.Model;

public class Sticker {
    public enum Type {
        STATIC,
        ANIMATED,
        POPUP
    }

    private Type type;
    private int id;

    public Sticker(Type type, int id) {
        this.type = type;
        this.id = id;
    }

    boolean equal(Sticker sticker) {
        return this.id == sticker.getId();
    }

    public Type getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }
}
