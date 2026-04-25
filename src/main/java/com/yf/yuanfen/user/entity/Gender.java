package com.yf.yuanfen.user.entity;

public enum Gender {
    UNKNOWN(0), MALE(1), FEMALE(2), OTHER(3);

    private final int value;

    Gender(int value) { this.value = value; }

    public int getValue() { return value; }

    public static boolean isValid(Integer value) {
        if (value == null) return true;
        for (Gender g : values()) {
            if (g.value == value) return true;
        }
        return false;
    }
}
