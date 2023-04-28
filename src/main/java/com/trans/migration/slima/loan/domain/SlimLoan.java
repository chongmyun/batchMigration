package com.trans.migration.slima.loan.domain;

import lombok.Data;

@Data
public class SlimLoan {
    private String libCode;
    private String loanDate;
    private String useId;
    private String slimSpeciesKey;
    private String regNo;
    private String loanTypeCode;
    private String returnPlanDate;
    private String returnLibCode;
    private String returnDate;
    private String renewalYN;

    private String prevLoanDate;
    private String shelfLocCode;
    private String nonLoanYN;
    private String retroActiveYN;
    private String retroActiveDate;
    private String nonReturnYN;
    private String returnTypeCode;

    private String partitionKey;

}
