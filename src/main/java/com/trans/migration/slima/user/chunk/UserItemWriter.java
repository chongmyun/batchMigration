package com.trans.migration.slima.user.chunk;

import com.trans.migration.slima.user.domain.CoLoanUser;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import javax.sql.DataSource;
import java.util.List;

public class UserItemWriter implements ItemWriter<CoLoanUser> {

    private static final String INSERT_USER = " INSERT CO_LOAN_USER_TBL " +
            " (REC_KEY,USER_ID,USER_NO,PASSWORD,WORKNO,CARDPASSWORD,NAME,BIRTHDAY,GPIN_SEX,REG_DATE,MANAGE_CODE,USER_CLASS_CODE,USER_POSITION_CODE, " +
            " SMS_USE_YN,MAILING_USE_YN,GRADE,H_PHONE,HANDPHONE,E_MAIL,H_ZIPCODE,H_ADDR1,W_ARRD1,LOAN_STOP_DATE,USER_PICTURE,USER_PICTURE_TYPE,REMARK, " +
            " REMOVE_DATE,PRIVACY_EXPIRE_DATE,PRIVACY_DESTROY_DATE,PRIVACY_CONFIRM_DATE,PRIVACY_CONFIRM_YN) " +
            " VALUES(" +
            " ESL_SEQ.NEXTVAL,:userId,:userNo,:password,:workno,:cardpassword,:name,:birthday,:gpinSex,:regDate,:manageCode,:userClassCode,:userPositionCode, " +
            " :smsUseYn,:mailingUseYn,:grade,hPhone,:handphone,:eMail,:hZipcode,:hAddr1,:wArrd1,:loanStopDate,:userPicture,:userPictureType,:remark," +
            " :removeDate,:privacyExpireDate,:privacyDestoryDate,:privacyConfirmDate,:privacyConfirmYn " +
            ")";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserItemWriter(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void write(Chunk<? extends CoLoanUser> chunk) throws Exception {
        List<? extends CoLoanUser> userList = chunk.getItems();

        SqlParameterSource[] speciesParams = SqlParameterSourceUtils.createBatch(userList.toArray());

        namedParameterJdbcTemplate.batchUpdate(INSERT_USER,speciesParams);

    }
}
