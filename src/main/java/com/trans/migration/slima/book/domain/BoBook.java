package com.trans.migration.slima.book.domain;

import lombok.Data;

import java.util.Date;

@Data
public class BoBook {
    private Long recKey;
    private Long acqKey;
    private Long speciesKey;
    private Long dupSpeciesKey;
    private Long accessionRecKey;
    private String representBookYn;
    private String bookAppendixFlag;
    private String workingStatus;
    private String prevWorkingStatus;
    private String regNo;
    private String acqCode;
    private String acqYear;
    private int seqNo;
    private int serialNo;
    private String manageCode;
    private String mediaCode;
    private String useLimitCode;
    private String useObjectCode;
    private String regCode;
    private int volSortNo;
    private String author;
    private String vol;
    private String volIndex;
    private String volTitle;
    private String volTitleIndex;
    private String publishYear;
    private Date publishDate;
    private String page;
    private String physicalProperty;
    private String bookSize;
    private String accompMat;
    private int price;
    private String priceCharacter;
    private String priceOtherInfo;
    private String eaIsbn;
    private String eaAddCode;
    private String eaInvalidIsbn;
    private String extraIsbn;
    private String extraAddCode;
    private String extraInvalidIsbn;
    private String separateShelfCode;
    private String classNoType;
    private String classNo;
    private String bookCode;
    private String volCode;
    private String copyCode;
    private String labelType;
    private String deliveryYn;
    private Date inputDate;
    private Date checkinDate;
    private Date regTransferDate;
    private Date regDate;
    private Date catTransferDate;
    private Date catCompleteDate;
    private Date locTransferDate;
    private Date shelfDate;
    private String shelfLocCode;
    private Date shelfChangeDate;
    private String shelfChangeBeforeLoc;
    private String shelfWorker;
    private String shelfChangeReasonCode;
    private String shelfChangeEtcReason;
    private String bookCheckYn;
    private String applicantName;
    private Date lastManageCodeChangeDate;
    private Date lastShelfStatusChangeDate;
    private Date lastUselimitcodeChangeDate;
    private Date mailSendDate;
    private String firstWork;
    private String lastWork;
    private int purchasePrice;
    private String workno;
    private String epc;
    private String epcCheck;
    private int lastShelfStatusWorker;
    private String divisionManageCode;
    private String purchaseManageCode;
    private String totalAcqManageCode;
    private String totalAcqYn;
    private Long groupBookKey;
    private String shelfGroupCode;
    private String noCheckIsbnIllRequest;
    private Long marcAcqKey;

    public BoBook() {}

}
