package com.trans.migration.slima.book.chunk;

import com.trans.migration.slima.book.domain.BoBook;
import com.trans.migration.slima.book.domain.BoSpecies;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class BookItemWriter implements ItemWriter<BoSpecies> {

    private static final String INSERT_SPECIES = "INSERT INTO BO_SPECIES_TBL(REC_KEY,CONTROL_MAT_CODE,FORM_CODE,MAT_CODE,MEDIA_CODE,SUB_REG_CODE,USE_LIMIT_CODE,USE_OBJ_CODE,MANAGE_CODE,SHELF_LOC_CODE" +
            ",CLASS_NO_TYPE,CLASS_NO,KDC_CLASS_NO,KDC_CLASS_NO_VER,DDC_CLASS_NO,DDC_CLASS_NO_VER,ECT_CLASS_NO,WORK_CODE,INPUT_DATE" +
            ",PRIORITY_YN,CATALOG_STATUS,WORKER,FIRST_WORK,ACQ_YEAR,CONTROL_NO)" +
            "VALUES(" +
            ":recKey,:controlMatCode,:formCode,:matCode,:mediaCode,:subRegCode,:useLimitCode,:useObjCode,:manageCode,:shelfLocCode" +
            ",:classNoType,:classNo,:kdcClassNo,:kdcClassNoVer,:ddcClassNo,:ddcClassNoVer,:ectClassNo,:workCode,:inputDate" +
            ",:priorityYn,:catalogStatus,:worker,:firstWork,:acqYear,:controlNo"+
            ")";

    private static final String INSERT_IDX_BO = "INSERT INTO IDX_BO_TBL(REC_KEY,FORM_CODE,MAT_CODE,MEDIA_CODE,SUB_REG_CODE,USE_LIMIT_CODE,USE_OBJ_CODE,WORK_CODE,MANAGE_CODE" +
            ",IDX_TITLE,IDX_DUP_TITLE,IDX_KEY_WORD,IDX_AUTHOR,IDX_PUBLISHER,IDX_ALL_ITEM,IDX_VOL,IDX_ICS,IDX_STANDARDNO,IDX_ITITLE" +
            ",IDX_IPUBLISHER,IDX_IPUB_YEAR,TITLE_INFO,AUTHOR_INFO,PUB_INFO,PUB_YEAR_INFO,EDIT_INFO,ICS_INFO,STANDARDNO_INFO" +
            ",TITLE,AUTHOR,PUBLISHER,PUB_YEAR_INFO,ST_CODE,ST_ISSN,ST_STRN,ST_RNSTRN,ST_CBN,ST_CAN,CLASS_NO,BOOK_CODE" +
            ",VOL_CODE,VOL_CODE_DISP,KDCP_CLASS,KDC_CLASS,DDC_CLASS,CEC_CLASS,CWC_CLASS,COC_CLASS,UDC_CLASS,LC_CLASS,ETC_CLASS" +
            ",UNI_CODE,DEG_CODE,EDIT,TEXTLANG,SUMLANG,PUBLISH_COUNTRY_CODE,EDIT_NO,SERIES_TITLE,SERIES_VOL,CURRENCY_SIGN)" +
            "VALUES(" +
            ":recKey,:formCode,:matCode,:mediaCode,:subRegCode,:useLimitCode,:useObjCode,:workCode,:manageCode,:idxTitle" +
            ",:idxDupTitle,:idxKeyWord,:idxAuthor,:idxPublisher,:idxAllItem,:idxVol,:idxIcs,:idxStandardno,:idxItitle" +
            ",:idxIpublisher,:idxIpubYear,:titleInfo,:authorInfo,:pubInfo,:pubYearInfo,:editInfo,:icsInfo,:standardnoInfo" +
            ",:title,:author,:publisher,:pubYearInfo,:stCode,:stIssn,:stStrn,:stRnstrn,:stCbn,:stCan,:classNo,:bookCode" +
            ",:volCode,:volCodeDisp,:kdcpClass,:kdcClass,:ddcClass,:cecClass,:cwcClass,:cocClass,:udcClass,:lcClass,:etcClass" +
            ",:uniCode,:degCode,:edit,:textlang,:sumlang,:publishCountryCode,:editNo,:seriesTitle,:seriesVol,:currencySign" +
            ")";

    private static final String INSERT_BO_BOOK = "INSERT INTO BO_BOOK_TBL(" +
            "REC_KEY,ACQ_KEY,SPECIES_KEY,ACCESSION_REC_KEY,REG_DATE,INPUT_DATE,SHELF_DATE,REPRESENT_BOOK_YN,BOOK_APPENDIX_FLAG" +
            ",WORKING_STATUS,REG_NO,REG_CODE,ACQ_CODE,ACQ_YEAR,MANAGE_CODE,MEDIA_CODE,USE_LIMIT_CODE,USE_OBJECT_CODE,VOL_SORT_NO,AUTHOR" +
            ",VOL,VOL_INDEX,VOL_TITLE,VOL_TITLE_INDEX,PUBLISH_YEAR,PUBLISH_DATE,PAGE,PHYSICAL_PROPERTY,ACCOMP_MAT,PRICE" +
            ",PRICE_CHARACTER,PRICE_OTHER_INFO,EA_ISBN,EA_ADD_CODE,SEPARATE_SHELF_CODE,CLASS_NO_TYPE,CLASS_NO,BOOK_CODE" +
            ",VOL_CODE,COPY_CODE,CAT_TRANSFER_DATE,CAT_COMPLETE_DATE,LOC_TRANSFER_DATE,SHELF_LOC_CODE,FIRST_WORK,WORKNO" +
            ")" +
            "VALUES(" +
            "ESL_SEQ.NEXTVAL,:acqKey,:speciesKey,:accessionRecKey,:regDate,:inputDate,:shelfDate,:representBookYn,:bookAppendixFlag" +
            ",:workingStatus,:regNo,:regCode,:acqCode,:acqYear,:manageCode,:mediaCode,:useLimitCode,:useObjectCode,:volSortNo,:author" +
            ",:vol,:volIndex,:volTitle,:volTitleIndex,:publishYear,:publishDate,:page,:physicalProperty,:accompMat,:price" +
            ",:priceCharacter,:priceOtherInfo,:eaIsbn,:eaAddCode,:separateShelfCode,:classNoType,:classNo,:bookCode" +
            ",:volCode,:copyCode,:catTransferDate,:catCompleteDate,:locTransferDate,:shelfLocCode,:firstWork,:workno" +
            ")";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public BookItemWriter(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void write(Chunk<? extends BoSpecies> chunk) throws Exception {
        List<? extends BoSpecies> speciesList = chunk.getItems();

        SqlParameterSource[] speciesParams = SqlParameterSourceUtils.createBatch(speciesList.toArray());

        List<SqlParameterSource> idxParams = new ArrayList<>();
        List<SqlParameterSource> bookParams = new ArrayList<>();

        for (BoSpecies boSpecies : speciesList) {
            SqlParameterSource idxParam = new BeanPropertySqlParameterSource(boSpecies.getIdxbo());
            idxParams.add(idxParam);
            List<BoBook> boBooks = boSpecies.getBoBookList();
            for (BoBook boBook : boBooks) {
                SqlParameterSource bookParam = new BeanPropertySqlParameterSource(boBook);
                bookParams.add(bookParam);
            }
        }

        namedParameterJdbcTemplate.batchUpdate(INSERT_SPECIES,speciesParams);

        namedParameterJdbcTemplate.batchUpdate(INSERT_IDX_BO,idxParams.toArray(new SqlParameterSource[0]));

        if(bookParams.size() > 0){
            namedParameterJdbcTemplate.batchUpdate(INSERT_BO_BOOK,bookParams.toArray(new SqlParameterSource[0]));
        }
    }
}
