package com.jorge.juegovocabulario;

public class CardItem {
    private final int imageResId;
    private final int pairId;
    private boolean revealed;
    private boolean matched;

    public CardItem(int imageResId, int pairId) {
        this.imageResId = imageResId;
        this.pairId = pairId;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getPairId() {
        return pairId;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }
}
