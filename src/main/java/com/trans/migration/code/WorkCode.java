package com.trans.migration.code;

import lombok.Getter;

@Getter
public enum WorkCode {
    ONE("1",	"동양서"),
    TWO("2",	"서양서"),
    THREE("3",	"비도서자료"),
    FOUR("4",	"신간"),
    FIVE("5",	"희망자료");

    private String code;
    private String codeDescription;

    WorkCode(String code, String codeDescription) {
        this.code = code;
        this.codeDescription = codeDescription;
    }
}
