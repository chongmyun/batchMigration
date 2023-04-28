package com.trans.migration.slima.user.job;

import com.trans.migration.batch.file.AbstractFlatFileItemReader;
import com.trans.migration.batch.partition.ParallelPartitioner;
import com.trans.migration.batch.util.AllUtils;
import com.trans.migration.slima.user.domain.SlimUser;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;


@Configuration
@RequiredArgsConstructor
public class UserCsvJobConfig extends DefaultBatchConfiguration {

    private final AllUtils allUtils;

    private int chunkSize = 10000;

    @Bean
    public Job userCsvJob(JobRepository jobRepository, Step userCsvPartition){
        return new JobBuilder("userCsvJob",jobRepository)
                .start(userCsvPartition)
                .build();
    }

    @Bean
    public Step userCsvPartition(JobRepository jobRepository,Step userCsvStep,
                                 Partitioner userParallelPartitioner,PartitionHandler userPartitionHandler){
        return new StepBuilder("userCsvPartition",jobRepository)
                .partitioner(userCsvStep.getName(),userParallelPartitioner)
                .partitionHandler(userPartitionHandler)
                .build();
    }

    @Bean
    public Partitioner userParallelPartitioner(){
        return new ParallelPartitioner();
    }

    @Bean
    @StepScope
    public PartitionHandler userPartitionHandler(@Value("#{jobParameters[GridSize[}") String gridSize, Step userCsvStep,
                                                 TaskExecutor taskExecutor){
        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setGridSize(Integer.parseInt(gridSize));
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor);
        taskExecutorPartitionHandler.setStep(userCsvStep);

        try{
            taskExecutorPartitionHandler.afterPropertiesSet();
        }catch(Exception e){
            e.printStackTrace();
        }
        return taskExecutorPartitionHandler;
    }

    @Bean
    public Step userCsvStep(JobRepository jobRepository, DataSourceTransactionManager tm,
                            FlatFileItemReader<SlimUser> userCsvReader,ItemProcessor<SlimUser,SlimUser> userCsvProcessor,
                            JdbcBatchItemWriter<SlimUser> userCsvWriter){
        return new StepBuilder("userCsvStep",jobRepository)
                .<SlimUser,SlimUser>chunk(chunkSize,tm)
                .reader(userCsvReader)
                .processor(userCsvProcessor)
                .writer(userCsvWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<SlimUser> userCsvReader(@Value("#{jobParameters[userPath]}")String userPath,@Value("#{stepExecutionContext[index]}") String index){

        String names = allUtils.getObjectFieldNames(SlimUser.class,false);
        System.out.println(names);
        String ext = userPath.substring(userPath.lastIndexOf("."));
        String filePath = userPath.substring(0,userPath.lastIndexOf("."));
        String parallelFile = filePath+index+ext;
        return new AbstractFlatFileItemReader<SlimUser>(index,parallelFile, SlimUser.class, names) {};
    }

    @Bean
    @StepScope
    public ItemProcessor<SlimUser,SlimUser> userCsvProcessor(@Value("#{stepExecutionContext[partition]}") String partition){
        return item -> {
            item.setPartitionKey(partition);
            return item;
        };
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<SlimUser> userCsvWriter(){
        String names = allUtils.getObjectFieldNames(SlimUser.class,true);

        names = ":"+names.replaceAll(",",":,");
        String sql = "insert into slim_user values ("+names+")";
        return new JdbcBatchItemWriterBuilder<SlimUser>()
                .dataSource(getDataSource())
                .sql(sql)
                .beanMapped()
                .build();
    }



}
