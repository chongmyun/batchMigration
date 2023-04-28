package com.trans.migration.slima.book.domain;

import lombok.Data;

@Data
public class SlimAppendix {

    private long slimSpeciesKey;

    private String bookRegNo;
    private String regNo;

    private String libCode;

    private String bookAppendixFlag;
}
