package com.trans.migration.slima.book.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class BoSpecies {

    private Long recKey;
    private String acqYear;
    private String workCode;
    private String controlNo;
    private String controlMatCode;
    private String matCode;
    private String formCode;
    private String subRegCode;
    private String mediaCode;
    private String useObjCode;
    private String useLimitCode;
    private String manageCode;
    private String marc;
    private String locFlag;
    private String subjectCode;
    private String classNoType;
    private String kdcClassNo;
    private String kdcClassNoVer;
    private String ddcClassNo;
    private String ddcClassNoVer;
    private String ectClassNo;
    private String shelfLocCode;
    private Long tocCommonKey;
    private String centerUploadStatus;
    private String remark;
    private String worker;
    private Date inputDate;
    private String dupFlag;
    private String applyYn;
    private String priorityYn;
    private String catalogStatus;
    private String userDefineCode1;
    private String userDefineCode2;
    private String userDefineCode3;
    private String firstWork;
    private String lastWork;
    private String alpasIndexRemake;

    private String errorMsg;

    private IdxBo idxbo;

    private List<BoBook> boBookList = new ArrayList<>();

    public BoSpecies() {}

}
