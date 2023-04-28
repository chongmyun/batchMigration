package com.trans.migration.code;

import lombok.Getter;

/**
 * 이용대상구분
 * */
@Getter
public enum UseObjCode {

    AD("AD",	"성인"),
    ES("ES",	"초등학교"),
    HS("HS",	"고등학교"),
    JU("JU",	"아동"),
    MS("MS",	"중학교"),
    PU("PU",	"일반"),
    SP("SP",	"특수");

    private String code;
    private String codeDescription;

    UseObjCode(String code, String codeDescription) {
        this.code = code;
        this.codeDescription = codeDescription;
    }
}
