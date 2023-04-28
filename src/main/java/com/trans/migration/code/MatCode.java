package com.trans.migration.code;

import lombok.Getter;

/**
 * 자료구분
 */
@Getter
public enum MatCode {
    CA("CA","만화"),
    CP("CP","회의자료"),
    DM("DM","석사학위논문"),
    DP("DP","박사학위논문"),
    GM("GM","일반자료"),
    HA("HA","교과서"),
    HR("HR","학습서"),
    JO("JO","학술지"),
    MA("MA","잡지"),
    NP("NP","신문"),
    RB("RB","고서"),
    RP("RP","보고서"),
    SM("SM","교지"),
    ZZ("ZZ","기타(도자기..)");

    private String code;
    private String codeDescription;

    MatCode(String code, String codeDescription) {
        this.code = code;
        this.codeDescription = codeDescription;
    }
}
