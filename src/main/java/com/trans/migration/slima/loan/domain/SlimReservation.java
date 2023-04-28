package com.trans.migration.slima.loan.domain;

import lombok.Data;

@Data
public class SlimReservation {
    private String libCode;
    private String reservationDate;
    private String userId;
    private String regNo;
    private String slimSpeciesKey;
    private String cancelDate;

    private String reservationSelectDate;
    private String reservationExpireDate;

    private String partitionKey;

}
