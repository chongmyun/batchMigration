package com.trans.migration.code;

import lombok.Getter;

/**
 * 이용제한구분
 * */
@Getter
public enum UseLimitCode {

    CA("CA",	"특수"),
    CD("CD",	"열람제한"),
    CL("CL",	"사서제한"),
    GM("GM",	"일반"),
    IZ("IZ",	"귀중자료");

    private String code;
    private String codeDescription;

    UseLimitCode(String code, String codeDescription) {
        this.code = code;
        this.codeDescription = codeDescription;
    }
}
