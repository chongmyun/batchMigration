package com.trans.migration.slima.book.domain;

import lombok.Data;

@Data
public class SlimBook {

    private long slimSpeciesKey;
    private String regNo;
    private String libCode;
    private String bookAppendixFlag;
    private String bookStatus;
    private String acqCode;
    private String regCode;
    private String resourceType;  //자료형태(구분)
    private String separateShelfCode;
    private String classNoType;
    private String classNo;
    private String bookCode;
    private String volNo;
    private String copyNo;
    private String shelfDate;
    private String shelfCode;
    private String workNo;
    private String acqYear;
    private String regDate;
    private String author;
    private String vol;
    private String volIndex;
    private String volTitle;
    private String volTitleIndex;
    private String publishYear;
    private String publishDate;
    private String page;
    private String physicalProperty;
    private String bookSize;
    private String accompMat;
    private String price;
    private String priceCharacter;
    private String priceOtherInfo;
    private String eaIsbn;

    public SlimBook() {}


}
