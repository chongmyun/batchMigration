package com.trans.migration.slima.book.domain;

import jakarta.annotation.Nullable;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SlimSpecies {

    private Long slimSpeciesKey;

    private String marc;

    private String partitionKey;

    @Nullable
    private String errorMsg;

    @Nullable
    private List<SlimBook> errorBooks = new ArrayList<>();
}
