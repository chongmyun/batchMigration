package com.trans.migration.code;

import lombok.Getter;

@Getter
public enum AcqCode {

    ONE("1","구입"),TWO("2",	"기증");

    private String code;
    private String codeDescription;

    AcqCode(String code, String codeDescription) {
        this.code = code;
        this.codeDescription = codeDescription;
    }

    static AcqCode findByCode(String code){
        for (AcqCode acqCode : AcqCode.values()){
            if(code == acqCode.code){
                return acqCode;
            }
        }
        return null;
    }
}
