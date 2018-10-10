package io.github.jeffshee.linestickerkeyboard.Adapter;

public interface ItemTouchHelperAdapter{
    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
