package com.sparta.plate.entity;

public enum FlagStatusEnum {

    CREATE,    //생성
    DELETE,   //삭제
    UPDATE;    //갱신

    public static FlagStatusEnum fromString(String status) {
        for (FlagStatusEnum flagStatus : FlagStatusEnum.values()) {
            if (flagStatus.name().equalsIgnoreCase(status)) {
                return flagStatus;
            }
        }
        throw new IllegalArgumentException("Invalid FlagStatus: " + status);
    }
}
