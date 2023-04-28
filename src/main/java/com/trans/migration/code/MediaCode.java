package com.trans.migration.code;

import lombok.Getter;

/**
 * 매체구분
 * */
@Getter
public enum MediaCode {
    AB("AB",	"오디오북"),
    EB("EB",	"E-BOOK"),
    KA("KA",	"점자자료"),
    LE("LE",	"킷트"),
    LF("LF",	"축물"),
    LP("LP",	"사진(실물)"),
    LQ("LQ",	"그림.서화(실물)"),
    LY("LY",	"팜플랫.리프렛.낱장자료"),
    LZ("LZ",	"절첩"),
    MA("MA",	"낱장지도"),
    MB("MB",	"벽지도"),
    MC("MC",	"구체"),
    MS("MS",	"필사자료(책자형)"),
    NA("NA",	"악보"),
    OA("OA",	"플로피 디스크"),
    OB("OB",	"CD-ROM"),
    OD("OD",	"테이프 카트리지"),
    OH("OH",	"마그네틱 테이프 릴"),
    PA("PA",	"CD-I"),
    PB("PB",	"CD-G"),
    PC("PC",	"비디오-CD"),
    PD("PD",	"DVD"),
    PR("PR",	"인쇄자료(책자형)"),
    SB("SB",	"카세트 테이프"),
    SD("SD",	"컴팩트 디스크"),
    SG("SG",	"음반(LP)"),
    VD("VD",	"영화필름"),
    VT("VT",	"비디오 테이프"),
    VU("VU",	"LD"),
    XA("XA",	"마이크로 피시"),
    XH("XH",	"마이크로 필름"),
    XU("XU",	"슬라이드"),
    XZ("XZ",	"기타 마이크로자료"),
    ZZ("ZZ",	"기타 비도서자료");

    private String code;
    private String codeDescription;


    MediaCode(String code, String codeDescription) {
        this.code = code;
        this.codeDescription = codeDescription;
    }
}
