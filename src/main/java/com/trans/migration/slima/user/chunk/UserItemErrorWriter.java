package com.trans.migration.slima.user.chunk;

import com.trans.migration.batch.util.AllUtils;
import com.trans.migration.slima.user.domain.CoLoanUser;
import com.trans.migration.slima.user.domain.SlimUser;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import javax.sql.DataSource;
import java.util.List;

public class UserItemErrorWriter  implements ItemWriter<CoLoanUser> {

    @Autowired
    private AllUtils allUtils;
    private static final String INSERT_SLIM_USER_ERROR = " INSERT INTO(TEMP_USER_KEY,USER_ID,ERROR_MSG) SLIM_USER_ERROR VALUES (:tempUserKey,:userId,:errorMsg)";
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserItemErrorWriter(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void write(Chunk<? extends CoLoanUser> chunk) throws Exception {
        ExecutionContext stepExecutionContext = StepSynchronizationManager.getContext().getStepExecution().getExecutionContext();
        List<SlimUser> errorList = (List<SlimUser>) stepExecutionContext.get("errorList");

        if(errorList != null && errorList.size() > 0) {
            SqlParameterSource[] speciesParams = SqlParameterSourceUtils.createBatch(errorList.toArray());
            namedParameterJdbcTemplate.batchUpdate(INSERT_SLIM_USER_ERROR,speciesParams);
        }
    }
}
