package com.trans.migration.code;

import lombok.Getter;

/**
 * 분류표구분
 * */
@Getter
public enum ClassNoType {

    ONE("1","KDC"),
    TWO("2","DDC"),
    SEVEN("7","기타분류표");

    private String code;
    private String codeDescription;

    ClassNoType(String code, String codeDescription) {
        this.code = code;
        this.codeDescription = codeDescription;
    }

    static ClassNoType findByCode(String code){
        for (ClassNoType classNoType : ClassNoType.values()){
            if(code == classNoType.code){
                return classNoType;
            }
        }
        return null;
    }
}
