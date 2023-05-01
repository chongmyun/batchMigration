package com.trans.migration.slima.request.job;

import com.trans.migration.batch.file.AbstractFlatFileItemReader;
import com.trans.migration.batch.util.AllUtils;
import com.trans.migration.slima.request.domain.SlimRequest;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


@Configuration
public class RequestCsvJobConfig extends DefaultBatchConfiguration {

    @Autowired
    private AllUtils allUtils;
    private int chunkSize = 10000;

    @Bean
    public Job requestCsvJob(JobRepository jobRepository, Step requestCsvStep){
        return new JobBuilder("requestCsvJob",jobRepository)
                .start(requestCsvStep)
                .build();
    }

    @Bean
    public Step requestCsvStep(JobRepository jobRepository, DataSourceTransactionManager tm,
                               ItemReader<SlimRequest> requestCsvReader, JdbcBatchItemWriter<SlimRequest> requestCsvWriter){
        return new StepBuilder("requestCsvStep",jobRepository)
                .<SlimRequest,SlimRequest>chunk(chunkSize,tm)
                .reader(requestCsvReader)
                .writer(requestCsvWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<SlimRequest> requestCsvReader(@Value("#{stepExecutionContext[requestPath]}") String requestPath){
        String names = allUtils.getObjectFieldNames(SlimRequest.class, false);
        return new AbstractFlatFileItemReader("1",requestPath, SlimRequest.class,names){};
    }

    @Bean
    public JdbcBatchItemWriter<SlimRequest> requestCsvWriter(DataSource dataSource){
        String names = allUtils.getObjectFieldNames(SlimRequest.class, true);
        names = ":" + names.replaceAll(",", ":");
        String sql = "insert into slim_request values("+names +")";
        return new JdbcBatchItemWriterBuilder<SlimRequest>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }
}
