package com.trans.migration.slima.user.job;

import com.trans.migration.batch.domain.MaxMinKey;
import com.trans.migration.batch.partition.DBPartitioner;
import com.trans.migration.batch.partition.FilePartitioner;
import com.trans.migration.exception.UserException;
import com.trans.migration.slima.book.chunk.BookItemWriter;
import com.trans.migration.slima.book.domain.BoSpecies;
import com.trans.migration.slima.user.chunk.UserItemErrorWriter;
import com.trans.migration.slima.user.chunk.UserItemProcessor;
import com.trans.migration.slima.user.chunk.UserItemReader;
import com.trans.migration.slima.user.chunk.UserItemWriter;
import com.trans.migration.slima.user.domain.CoLoanUser;
import com.trans.migration.slima.user.domain.SlimUser;
import com.trans.migration.slima.user.listener.CoLoanUserProcessorListener;
import com.trans.migration.slima.user.repository.SlimUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class UserDBJobConfig extends DefaultBatchConfiguration {

    private final SlimUserRepository slimUserRepository;

    private int chunkSize = 10000;

    /**
     * 1. 이용자 마이그레이션 작업
     * */
    @Bean
    public Job userDBJob(JobRepository jobRepository,Step userDBMinMaxKey, Step userDBStep){
        return new JobBuilder("userDBJob",jobRepository)
                .start(userDBMinMaxKey)
                .next(userDBStep)
                .build();
    }

    /**
     * 2-1. 이용자 임시키 min,max 값 구하기
     * */
    @Bean
    public Step userDBMinMaxKey(JobRepository jobRepository, DataSourceTransactionManager tm,
                           ItemReader<MaxMinKey> userDBKeyReader,ItemWriter<MaxMinKey> userDBKeyWriter){
        return new StepBuilder("userDBMinMaxKey",jobRepository)
                .<MaxMinKey,MaxMinKey>chunk(1,tm)
                .reader(userDBKeyReader)
                .writer(userDBKeyWriter)
                .build();
    }

    @Bean
    public JdbcPagingItemReader<MaxMinKey> userDBKeyReader(DataSource dataSource) throws Exception {

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("maxKey",Order.ASCENDING);

        SqlPagingQueryProviderFactoryBean queryProvider  = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("max(temp_key) as maxKey,min(temp_key) as minKey");
        queryProvider.setFromClause("from slim_user");
        queryProvider.setSortKeys(sortKeys);

        return new JdbcPagingItemReaderBuilder<MaxMinKey>()
                .name("userDBKeyReader")
                .dataSource(dataSource)
                .fetchSize(1)
                .pageSize(1)
                .rowMapper(new BeanPropertyRowMapper<>(MaxMinKey.class))
                .queryProvider(queryProvider.getObject())
                .build();
    }

    @Bean
    public ItemWriter<MaxMinKey> userDBKeyWriter(){
        return chunk -> {
            List<? extends MaxMinKey> items = chunk.getItems();
            ExecutionContext jobExecutionContext = JobSynchronizationManager.getContext().getJobExecution().getExecutionContext();
            MaxMinKey maxMinKey = items.get(0);
            jobExecutionContext.put("maxMinKey",maxMinKey);
        };
    }

    /**
     * 2-2.이용자 임시키 min,max 를 이용해 gridSize 를 이용해
     * 파티셔닝 및 이용자 마이그레이션 스텝
     * */
    @Bean
    public Step UserDBPartition(JobRepository jobRepository, Step userDBMigrationStep,
                                Partitioner userDBPartitioner, PartitionHandler userDBPartitionHandler){
        return new StepBuilder("UserDBPartition",jobRepository)
                .partitioner(userDBMigrationStep.getName(),userDBPartitioner)
                .partitionHandler(userDBPartitionHandler)
                .build();
    }

    @Bean
    public Partitioner userDBPartitioner(){
        return new DBPartitioner();
    }

    @Bean
    @StepScope
    public PartitionHandler userDBPartitionHandler(@Value("#{jobParameters[GridSize[}") String gridSize, Step userDBStep,
                                                 TaskExecutor taskExecutor){
        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setGridSize(Integer.parseInt(gridSize));
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor);
        taskExecutorPartitionHandler.setStep(userDBStep);

        try{
            taskExecutorPartitionHandler.afterPropertiesSet();
        }catch(Exception e){
            e.printStackTrace();
        }
        return taskExecutorPartitionHandler;
    }

    @Bean
    public Step userDBMigrationStep(JobRepository jobRepository, DataSourceTransactionManager tm,
                           ItemReader<SlimUser> userDBMigrationReader, ItemProcessor<SlimUser,CoLoanUser> userDBMigrationProcessor,
                                    CompositeItemWriter<CoLoanUser> userDBCompositeItemWriter,ItemProcessListener<SlimUser,CoLoanUser> coLoanUserProcessorListener){
        return new StepBuilder("userDBMigrationStep",jobRepository)
                .<SlimUser,CoLoanUser>chunk(chunkSize,tm)
                .reader(userDBMigrationReader)
                .processor(userDBMigrationProcessor)
                .writer(userDBCompositeItemWriter)
                .listener(coLoanUserProcessorListener)
                .faultTolerant()
                .skip(UserException.class)
                .skipLimit(100000)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<SlimUser> userDBMigrationReader(@Value("#{stepExecutionContext[startKey]}") long startKey, @Value("#{stepExecutionContext[endKey]}") long endKey,DataSource dataSource){
        return new UserItemReader(dataSource,slimUserRepository,startKey,endKey,chunkSize);
    }

    @Bean
    @StepScope
    public ItemProcessor<SlimUser,CoLoanUser> userDBMigrationProcessor(){
        return new UserItemProcessor();
    }

    @Bean
    @StepScope
    public CompositeItemWriter<CoLoanUser> userDBCompositeItemWriter(DataSource dataSource){
        CompositeItemWriter compositeItemWriter = new CompositeItemWriter();
        compositeItemWriter.setDelegates(Arrays.asList(userDBMigrationWriter(),userDBMigrationErrorWriter()));

        return compositeItemWriter;
    }

    @Bean
    @StepScope
    public ItemWriter<CoLoanUser> userDBMigrationWriter(){
        return new UserItemWriter(getDataSource());
    }

    @Bean
    @StepScope
    public ItemWriter<CoLoanUser> userDBMigrationErrorWriter(){
        return new UserItemErrorWriter(getDataSource());
    }

    @Bean
    public ItemProcessListener<SlimUser,CoLoanUser> coLoanUserProcessorListener(){
        return new CoLoanUserProcessorListener();
    }


}
