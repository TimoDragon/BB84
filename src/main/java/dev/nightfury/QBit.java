package dev.nightfury;

import lombok.Getter;

public class QBit {
    @Getter
    private int bit;
    @Getter
    private char base;

    public QBit(int bit, char base) {
        this.bit = bit;
        this.base = base;
    }
}