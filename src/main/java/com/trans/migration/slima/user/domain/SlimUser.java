package com.trans.migration.slima.user.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class SlimUser {

    public SlimUser(){}

    private long tempUserKey;
    private String userId;
    private String userNo;
    private String userWebId;
    private String password;
    private String rfid;
    private String rfidPassword;
    private String username;
    private String birthday;
    private String sex;
    private String applyDate;
    private String libCode;
    private String userPosition;
    private String userClass;
    private String smsYN;
    private String emailYN;
    private String grade;
    private String number;
    private String phoneNumber;
    private String email;
    private String zipCode;
    private String addressOne;
    private String addressTwo;
    private String loanStopDate;
    private String userPicturePath;
    private String popupRemark;
    private String remark;
    private String securityLevel;
    private String acceptDate;
    private String privacyExpireDate;
    private String acceptWorker;
    private String privacyDate;
    private String stopDate;
    private String partitionKey;

    @Nullable
    private String errorMsg;


}
