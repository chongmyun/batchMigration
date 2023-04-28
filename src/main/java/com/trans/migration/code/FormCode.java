package com.trans.migration.code;

import lombok.Getter;

/**
 * 형식구분
 * */
@Getter
public enum FormCode {
    CR("CR",	"계속자료"),
    MX("MX",	"복합자료"),
    MU("MU",	"음악/녹음자료"),
    BK("BK",	"도서"),
    ER("ER",	"전자자료"),
    MP("MP",	"지도자료"),
    VM("VM",	"시청각자료");

    private String code;
    private String codeDescription;

    FormCode(String code, String codeDescription) {
        this.code = code;
        this.codeDescription = codeDescription;
    }
}
