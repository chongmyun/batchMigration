package com.trans.migration.code;

import lombok.Getter;

/**
 * 보조등록구분
 * */
@Getter
public enum SubRegCode {
    EL("EL","동양"),
    WL("WL",	"서양");

    private String code;
    private String codeDescription;

    SubRegCode(String code, String codeDescription) {
        this.code = code;
        this.codeDescription = codeDescription;
    }


}
