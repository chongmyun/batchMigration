package com.trans.migration.slima.user.chunk;


import com.trans.migration.slima.user.domain.SlimUser;
import com.trans.migration.slima.user.repository.SlimUserRepository;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class UserItemReader extends AbstractPagingItemReader<SlimUser> {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SlimUserRepository slimUserRepository;
    private int chunkSize = 0;

    private long startKey = 0;

    private long endKey = 0;

    public UserItemReader(DataSource dataSource, SlimUserRepository userRepository,long startKey, long endKey,int chunkSize) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.slimUserRepository = userRepository;
        this.startKey = startKey;
        this.endKey = endKey;
        this.chunkSize = chunkSize;
        this.setPageSize(chunkSize);
    }


    @Override
    protected void doReadPage() {
        if (results == null) results = new CopyOnWriteArrayList<>();
        else results.clear();

        BeanPropertyRowMapper<SlimUser> beanPropertyRowMapper = new BeanPropertyRowMapper<>();

        long start = startKey;
        long end = endKey;
        List<SlimUser> slimUsers = slimUserRepository.getSlimUsersByPaging(start,end, chunkSize);

        this.startKey = slimUsers.get(slimUsers.size() - 1 ).getTempUserKey();

        results.addAll(slimUsers);

    }
}
