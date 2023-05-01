package com.trans.migration.slima.loan.job;

import com.trans.migration.batch.file.AbstractFlatFileItemReader;
import com.trans.migration.batch.partition.FilePartitioner;
import com.trans.migration.batch.util.AllUtils;
import com.trans.migration.slima.loan.domain.SlimLoan;
import com.trans.migration.slima.loan.domain.SlimReservation;
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
public class LoanCsvJobConfig extends DefaultBatchConfiguration {

    private final AllUtils allUtils;
    private int chunkSize = 10000;

    @Bean
    public Job loanCsvJob(JobRepository jobRepository, Step loanCsvPartition,Step reservationCsvPartition){
        return new JobBuilder("loanCsvJob",jobRepository)
                .start(loanCsvPartition)
                .next(reservationCsvPartition)
                .build();
    }

    @Bean
    public Step loanCsvPartition(JobRepository jobRepository,Step loanCsvStep,
                                 Partitioner loanParallelPartitioner,PartitionHandler loanPartitionHandler){
        return new StepBuilder("loanCsvPartition",jobRepository)
                .partitioner(loanCsvStep.getName(),loanParallelPartitioner)
                .partitionHandler(loanPartitionHandler)
                .build();
    }

    @Bean
    public Partitioner loanParallelPartitioner(){
        return new FilePartitioner();
    }

    @Bean
    public PartitionHandler loanPartitionHandler(@Value("#{jobParameters[GridSize]}") String gridSize, Step loanCsvStep,
                                                 TaskExecutor taskExecutor){
        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setGridSize(Integer.parseInt(gridSize));
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor);
        taskExecutorPartitionHandler.setStep(loanCsvStep);

        try{
            taskExecutorPartitionHandler.afterPropertiesSet();
        }catch(Exception e){
            e.printStackTrace();
        }
        return taskExecutorPartitionHandler;
    }

    @Bean
    public Step loanCsvStep(JobRepository jobRepository, DataSourceTransactionManager tm,
                            FlatFileItemReader<SlimLoan> loanCsvReader, ItemProcessor<SlimLoan,SlimLoan> loanCsvProcessor,
                            JdbcBatchItemWriter<SlimLoan> loanCsvWriter){
        return new StepBuilder("loanCsvStep",jobRepository)
                .<SlimLoan,SlimLoan>chunk(chunkSize,tm)
                .reader(loanCsvReader)
                .processor(loanCsvProcessor)
                .writer(loanCsvWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<SlimLoan> loanCsvReader(@Value("#{jobParameters[loanPath]}")String loanPath,@Value("#{stepExecutionContext[index]}") String index){

        String names = allUtils.getObjectFieldNames(SlimLoan.class,false);
        String ext = loanPath.substring(loanPath.lastIndexOf("."));
        String filePath = loanPath.substring(0,loanPath.lastIndexOf("."));
        String parallelFile = filePath+index+ext;
        return new AbstractFlatFileItemReader<SlimLoan>(index,parallelFile, SlimLoan.class, names) {};
    }

    @Bean
    @StepScope
    public ItemProcessor<SlimLoan,SlimLoan> loanCsvProcessor(@Value("#{stepExecutionContext[partition]}") String partition){
        return item -> {
            item.setPartitionKey(partition);
            return item;
        };
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<SlimLoan> loanCsvWriter(){
        String names = allUtils.getObjectFieldNames(SlimLoan.class,true);

        names = ":"+names.replaceAll(",",":,");
        String sql = "insert into slim_loan values ("+names+")";
        return new JdbcBatchItemWriterBuilder<SlimLoan>()
                .dataSource(getDataSource())
                .sql(sql)
                .beanMapped()
                .build();
    }

    @Bean
    public Step reservationCsvPartition(JobRepository jobRepository,Step reservationCsvStep,
                                 Partitioner reservationParallelPartitioner,PartitionHandler reservationPartitionHandler){
        return new StepBuilder("loanCsvPartition",jobRepository)
                .partitioner(reservationCsvStep.getName(),reservationParallelPartitioner)
                .partitionHandler(reservationPartitionHandler)
                .build();
    }

    @Bean
    public Partitioner reservationParallelPartitioner(){
        return new FilePartitioner();
    }

    @Bean
    public PartitionHandler reservationPartitionHandler(@Value("#{jobParameters[GridSize[}") String gridSize, Step reservationCsvStep,
                                                 TaskExecutor taskExecutor){
        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setGridSize(Integer.parseInt(gridSize));
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor);
        taskExecutorPartitionHandler.setStep(reservationCsvStep);

        try{
            taskExecutorPartitionHandler.afterPropertiesSet();
        }catch(Exception e){
            e.printStackTrace();
        }
        return taskExecutorPartitionHandler;
    }

    @Bean
    public Step reservationCsvStep(JobRepository jobRepository, DataSourceTransactionManager tm,
                            FlatFileItemReader<SlimReservation> reservationCsvReader, ItemProcessor<SlimReservation,SlimReservation> reservationCsvProcessor,
                            JdbcBatchItemWriter<SlimReservation> reservationCsvWriter){
        return new StepBuilder("reservationCsvStep",jobRepository)
                .<SlimReservation,SlimReservation>chunk(chunkSize,tm)
                .reader(reservationCsvReader)
                .processor(reservationCsvProcessor)
                .writer(reservationCsvWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<SlimReservation> reservationCsvReader(@Value("#{jobParameters[reservationPath]}")String reservationPath, @Value("#{stepExecutionContext[index]}") String index){

        String names = allUtils.getObjectFieldNames(SlimReservation.class,false);
        String ext = reservationPath.substring(reservationPath.lastIndexOf("."));
        String filePath = reservationPath.substring(0,reservationPath.lastIndexOf("."));
        String parallelFile = filePath+index+ext;
        return new AbstractFlatFileItemReader<SlimReservation>(index,parallelFile, SlimReservation.class, names) {};
    }

    @Bean
    @StepScope
    public ItemProcessor<SlimReservation,SlimReservation> reservationCsvProcessor(@Value("#{stepExecutionContext[partition]}") String partition){
        return item -> {
            item.setPartitionKey(partition);
            return item;
        };
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<SlimReservation> reservationCsvWriter(){
        String names = allUtils.getObjectFieldNames(SlimReservation.class,true);

        names = ":"+names.replaceAll(",",":,");
        String sql = "insert into slim_reservation values ("+names+")";
        return new JdbcBatchItemWriterBuilder<SlimReservation>()
                .dataSource(getDataSource())
                .sql(sql)
                .beanMapped()
                .build();
    }
}
