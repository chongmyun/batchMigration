package com.trans.migration.slima.request.domain;

import lombok.Data;

@Data
public class SlimRequest {

    private String libCode;
    private String userId;
    private String requestDate;
    private String title;
    private String author;
    private String publisher;
    private String pubYear;
    private String eaIsbn;
    private String price;
    private String requestRemark;
    private String shelfDate;
    private String requestStatus;
    private String cancelReason;
    private String isReservation;


}
