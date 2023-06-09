package com.trans.migration.slima.user.domain;

import jakarta.annotation.Nullable;
import lombok.Data;

import java.util.Date;

@Data
public class CoLoanUser {

    private long recKey;
    private String userNo;
    private String userId;
    private String password;
    private String name;
    private Date birthday;
    private String birthdayType;
    private String civilNo;
    private String exchangeNo;
    private String departmentName;
    private String className;
    private String handphone;
    private String idxHandphone;
    private String eMail;
    private Date regDate;
    private Date removeDate;
    private String inferiorReason;
    private String memberClass;
    private String school;
    private String grade;
    private String classes;
    private String classNo;
    private String mailSendArea;
    private long loanCount;
    private long reservationCount;
    private long delayCount;
    private long delaydayCount;
    private Date loanStopDate;
    private Date lastLoanDate;
    private Date lastModifyDate;
    private String remark;
    private long reissueCnt;
    private Date loanStopSetDate;
    private Date newUserMailDate;
    private String centerUploadCheck;
    private String shelfLocCode;
    private byte[] userPicture;
    private String userPictureType;
    private String lostUserCard;
    private String smsUseYn;
    private String monetaInformPhoneNum;
    private Date monetaInformDate;
    private String cardPassword;
    private String userDefineCode1;
    private String userDefineCode2;
    private String userDefineCode3;
    private String firstWork;
    private String lastWork;
    private String office;
    private String workno;
    private String armyClass;
    private String mailingUseYn;
    private Date secedeDate;
    private String checking;
    private long fidKey;
    private String hAddr1;
    private String hPhone;
    private String hZipcode;
    private String idxUserName;
    private Date infoValidDate;
    private String manageCode;
    private String userClass;
    private String userClassCode;
    private String userPositionCode;
    private String wZipcode;
    private String wPhone;
    private String wAddr1;
    private long illExpireReturnCnt;
    private Date illAplStopSetDate;
    private Date illAplStopDate;
    private String selfLoanStationLimit;
    private String pbWorkno;
    private Date remarkEditDate;
    private String secondPhone;
    private Date klRegDate;
    private String gpinAscii;
    private String gpinHash;
    private String gpinSex;
    private String klMemberYn;
    private String cryptoCivilNo;
    private String ipinHash;
    private Date ipinDate;
    private String ipinResult;
    private String certifyWorker;
    private Date renewalDate;
    private String expelledReason;
    private Date privacyExpireDate;
    private Date privacyConfirmDate;
    private Date privacyDestroyDate;
    private String privacyConfirmYn;
    private Date klSecedeDate;
    private String groupLoanUseAbleYn;
    private Date groupLoanStopDate;
    private String illMemberYn;
    private Date illRegDate;
    private Date illSecedeDate;
    private long birthdayOrder;
    private long numberOfChildren;
    private String insertSmsYn;
    private long cumulativeLoanCount;
    private Date memberRegDate;
    private String passwordQ;
    private String passwordA;
    private String smsHandphone;

}
