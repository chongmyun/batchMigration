package com.trans.migration.slima.book.chunk;

import com.trans.migration.batch.util.AllUtils;
import com.trans.migration.slima.book.domain.BoSpecies;
import com.trans.migration.slima.book.domain.SlimBook;
import com.trans.migration.slima.book.domain.SlimSpecies;
import com.trans.migration.slima.user.domain.SlimUser;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookItemErrorWriter implements ItemWriter<BoSpecies> {

    @Autowired
    private AllUtils allUtils;

    private static final String INSERT_SLIM_BOOK_ERROR = " INSERT INTO(SLIM_SPECIES_KEY,REG_NO,SPECIES_ERROR_MSG,BOOK_ERROR_MSG) SLIM_SPECIES_ERROR VALUES (:slimSpeciesKey,:regNo,:speciesErrorMsg,:bookErrorMsg)";
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public BookItemErrorWriter(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void write(Chunk<? extends BoSpecies> chunk) throws Exception {
        ExecutionContext stepExecutionContext = StepSynchronizationManager.getContext().getStepExecution().getExecutionContext();
        List<SlimSpecies> errorList = (List<SlimSpecies>) stepExecutionContext.get("errorList");

        List<SqlParameterSource> errorParams = new ArrayList<>();

        if(errorList != null && errorList.size() > 0) {
            for (SlimSpecies slimSpecies : errorList) {
                List<SlimBook> errorBooks = slimSpecies.getErrorBooks();
                if(errorBooks.size() > 0){
                    for (SlimBook errorBook : errorBooks) {
                        Map<String,Object> errorParam = new HashMap<>();
                        errorParam.put("slimSpeciesKey",slimSpecies.getSlimSpeciesKey());
                        errorParam.put("regNo",errorBook.getRegNo());
                        errorParam.put("speciesErrorMsg",slimSpecies.getErrorMsg());
                        errorParam.put("bookErrorMsg",errorBook.getErrorMsg());
                        SqlParameterSource sqlParameterSource = new MapSqlParameterSource(errorParam);
                        errorParams.add(sqlParameterSource);
                    }
                }else{
                    Map<String,Object> errorParam = new HashMap<>();
                    errorParam.put("slimSpeciesKey",slimSpecies.getSlimSpeciesKey());
                    errorParam.put("regNo",null);
                    errorParam.put("speciesErrorMsg",slimSpecies.getErrorMsg());
                    errorParam.put("bookErrorMsg",null);
                    SqlParameterSource sqlParameterSource = new MapSqlParameterSource(errorParam);
                    errorParams.add(sqlParameterSource);
                }
            }

            if(errorParams.size() > 0){
                namedParameterJdbcTemplate.batchUpdate(INSERT_SLIM_BOOK_ERROR,errorParams.toArray(new SqlParameterSource[0]));
            }
        }

    }
}
