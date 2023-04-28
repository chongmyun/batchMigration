package com.trans.migration.slima.book.job;

import com.trans.migration.batch.partition.ParallelPartitioner;
import com.trans.migration.slima.book.chunk.BookItemProcessor;
import com.trans.migration.slima.book.chunk.BookItemWriter;
import com.trans.migration.slima.book.domain.BoSpecies;
import com.trans.migration.slima.book.domain.SlimSpecies;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.Types;

@Configuration
@RequiredArgsConstructor
public class BookDBJobConfig extends DefaultBatchConfiguration {

    @Value("${chunkSize:5000}")
    private int chunkSize;

    /**
     * 1. 잡생성
    * */
    @Bean
    public Job bookDBJob(JobRepository jobRepository, Step bookDBPartition){
        return new JobBuilder("bookDBJob",jobRepository)
                .start(bookDBPartition)
                .build();
    }

    /**
     * 2. 파티션 스탭생성
     */
    @Bean
    public Step bookDBPartition(JobRepository jobRepository,Step bookDBStep,Partitioner bookDBPartitioner,
                                PartitionHandler bookDBPartitionHandler){
        return new StepBuilder("bookDBPartition",jobRepository)
                .partitioner(bookDBStep.getName(),bookDBPartitioner)
                .partitionHandler(bookDBPartitionHandler)
                .build();
    }

    @Bean
    public Partitioner bookDBPartitioner(){return new ParallelPartitioner(); }

    @Bean
    @JobScope
    public PartitionHandler bookDBPartitionHandler(@Value("#{jobParameters[GridSize]}") String gridSize,
                                                   TaskExecutor taskExecutor, Step bookDBStep){
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setGridSize(Integer.parseInt(gridSize));
        handler.setTaskExecutor(taskExecutor);
        handler.setStep(bookDBStep);

        try {
            handler.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return handler;
    }
    /**
     * 3. 작업 스탭생성
     * */
    @Bean
    public Step bookDBStep(JobRepository jobRepository, DataSourceTransactionManager tm,
                           JdbcCursorItemReader<SlimSpecies> bookDBItemReader,ItemProcessor<SlimSpecies,BoSpecies> bookDBItemProcessor,
                           ItemWriter<BoSpecies> bookDBItemWriter){
        return new StepBuilder("bookDBStep",jobRepository)
                .<SlimSpecies, BoSpecies>chunk(chunkSize,tm)
                .reader(bookDBItemReader)
                .processor(bookDBItemProcessor)
                .writer(bookDBItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<SlimSpecies> bookDBItemReader(@Value("#{stepExecutionContext[partition]}") String partition,DataSource dataSource){

        RowMapper<SlimSpecies> slimSpeciesRowMapper = BeanPropertyRowMapper.newInstance(SlimSpecies.class);

        return new JdbcCursorItemReaderBuilder<SlimSpecies>()
                .name("bookDBItemReader")
                .dataSource(dataSource)
                .fetchSize(chunkSize)
                .rowMapper(slimSpeciesRowMapper)
                .sql("SELECT * FROM SLIM_SPECIES WHERE PARTITION_KEY = ? ORDER BY SLIM_SPECIES_KEY")
                .queryArguments(partition, Types.VARCHAR)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<SlimSpecies,BoSpecies> bookDBItemProcessor(DataSource dataSource){
        return new BookItemProcessor(dataSource);
    }

    @Bean
    @StepScope
    public ItemWriter<BoSpecies> bookDBItemWriter(DataSource dataSource){
        return new BookItemWriter(dataSource);
    }



}
