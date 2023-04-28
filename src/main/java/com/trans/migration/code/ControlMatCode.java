package com.trans.migration.code;

import lombok.Getter;

/**
 * 제어자료구분
 */
@Getter
public enum ControlMatCode {

    KMO("KMO",	"단행본"),
    KSE("KSE",	"국내연속간행물"),
    WSE("WSE",	"국외연속간행물");

    private String code;
    private String codeDescription;

    ControlMatCode(String code, String codeDescription) {
        this.code = code;
        this.codeDescription = codeDescription;
    }
}
