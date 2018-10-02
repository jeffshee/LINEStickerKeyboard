package io.github.jeffshee.linestickerkeyboard.Model;

import java.util.ArrayList;

public class HistoryPack {
    private static final int MAX_SIZE = 50;
    private ArrayList<Integer> history;

    public HistoryPack(ArrayList<Integer> history) {
        this.history = history;
    }

    public void add(int id) {
        for (int i = 0; i < history.size(); i++) {
            if (id == history.get(i)) history.remove(i);
        }
        history.add(0, id);
        if (history.size() > MAX_SIZE) {
            history.remove(MAX_SIZE);
        }
    }

    public int getId(int index){
        return history.get(index);
    }

    public void clear() {
        history.clear();
    }

    public int size(){
        return history.size();
    }
}
