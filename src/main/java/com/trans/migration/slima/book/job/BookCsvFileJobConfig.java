package com.trans.migration.slima.book.job;

import com.trans.migration.batch.file.AbstractFlatFileItemReader;
import com.trans.migration.batch.partition.ParallelPartitioner;
import com.trans.migration.batch.util.AllUtils;
import com.trans.migration.slima.book.domain.SlimAppendix;
import com.trans.migration.slima.book.domain.SlimBook;
import com.trans.migration.slima.book.domain.SlimSpecies;
import com.trans.migration.slima.user.domain.SlimUser;
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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BookCsvFileJobConfig extends DefaultBatchConfiguration {

    private final Environment env;

    private final AllUtils allUtils;
    /**
     * 1.잡 생성
     * */
    @Bean
    public Job bookCsvFileJob(JobRepository jobRepository, Step speciesCsvFilePartition,Step bookCsvFilePartition,Step appendixCsvFileStep){
        return new JobBuilder("bookCsvFileJob",jobRepository)
                .start(speciesCsvFilePartition)
                .next(bookCsvFilePartition)
                .next(appendixCsvFileStep)
                .build();
    }

    /**
     * 2.파티션 생성
     *   - 파티션핸들러(PartitionHandler)
     *     원하는 파티션 갯수 설정 ,STEP 지정 및 TaskExecutor 지정
     *   - Partitioner
     *     Partitioner 를 구현해 파티션 크기만큼 각 파티션에 executionContext 부여
     * */
    @Bean
    public Partitioner parallelPartitioner(){
        return new ParallelPartitioner();
    }

    /**
     * 2-1 종 파일 파티션 생성
     * */
    @Bean
    public Step speciesCsvFilePartition(JobRepository jobRepository, Step speciesCsvFileStep
            , Partitioner parallelPartitioner, PartitionHandler speciesCsvFilePartitionHandler){
        return new StepBuilder("bookCsvFilePartition",jobRepository)
                .partitioner(speciesCsvFileStep.getName(),parallelPartitioner)
                .partitionHandler(speciesCsvFilePartitionHandler)
                .build();
    }

    @Bean
    @JobScope
    public PartitionHandler speciesCsvFilePartitionHandler(@Value("#{jobParameters[GridSize]}") String gridSize,
                                                        TaskExecutor taskExecutor, Step speciesCsvFileStep){

        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setGridSize(Integer.parseInt(gridSize));
        handler.setTaskExecutor(taskExecutor);
        handler.setStep(speciesCsvFileStep);

        try {
            handler.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return handler;
    }

    /**
    * 2-2 책 파일 파티션 생성
    * */
    @Bean
    public Step bookCsvFilePartition(JobRepository jobRepository, Step bookCsvFileStep
            , Partitioner parallelPartitioner, PartitionHandler bookCsvFilePartitionHandler){
        return new StepBuilder("bookCsvFilePartition",jobRepository)
                .partitioner(bookCsvFileStep.getName(),parallelPartitioner)
                .partitionHandler(bookCsvFilePartitionHandler)
                .build();
    }

    @Bean
    @JobScope
    public PartitionHandler bookCsvFilePartitionHandler(@Value("#{jobParameters[GridSize]}") String gridSize,
                                             TaskExecutor taskExecutor, Step bookCsvFileStep){

        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setGridSize(Integer.parseInt(gridSize));
        handler.setTaskExecutor(taskExecutor);
        handler.setStep(bookCsvFileStep);

        try {
            handler.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return handler;
    }

    /**
     * 3. 작업스탭 생성
     * */

    /**
     * 3-1. 서지 작업스탭 생성
     * */
    @Bean
    public Step speciesCsvFileStep(JobRepository jobRepository, PlatformTransactionManager tm,
                                FlatFileItemReader<SlimSpecies> speciesCsvFileItemReader,ItemProcessor<SlimSpecies,SlimSpecies> speciesCsvFileItemProcessor
                                , JdbcBatchItemWriter<SlimSpecies> speciesCsvFileItemWriter){
        return new StepBuilder("bookCsvFileStep",jobRepository)
                .<SlimSpecies,SlimSpecies>chunk(10000,tm)
                .reader(speciesCsvFileItemReader)
                .processor(speciesCsvFileItemProcessor)
                .writer(speciesCsvFileItemWriter)
                .build();
    }

    /**
     * 3-2. 책 작업스탭 생성
     * */
    @Bean
    public Step bookCsvFileStep(JobRepository jobRepository, PlatformTransactionManager tm,
                                 FlatFileItemReader<SlimBook> bookCsvFileItemReader, JdbcBatchItemWriter<SlimBook> bookCsvFileItemWriter){
        return new StepBuilder("bookCsvFileStep",jobRepository)
                .<SlimBook,SlimBook>chunk(10000,tm)
                .reader(bookCsvFileItemReader)
                .writer(bookCsvFileItemWriter)
                .build();
    }

    /**
     * 3-3. 부록자료 작업스탭 생성
     * */
    @Bean
    public Step appendixCsvFileStep(JobRepository jobRepository,PlatformTransactionManager tm,
                                    FlatFileItemReader<SlimAppendix> appendixCsvFileItemReader,ItemProcessor<SlimAppendix,SlimBook> appendixCsvFileItemProcessor,
                                    JdbcBatchItemWriter<SlimBook> appendixCsvFileItemWriter){
        return new StepBuilder("appendixCsvFileStep",jobRepository)
                .<SlimAppendix,SlimBook>chunk(10000,tm)
                .reader(appendixCsvFileItemReader)
                .processor(appendixCsvFileItemProcessor)
                .writer(appendixCsvFileItemWriter)
                .build();
    }

    /**
     * 4. reader,process,writer 생성
     * */

    /**
     * 4-1. 종 파일 reader,processor,writer 생성
    * */
    @Bean
    @StepScope
    public FlatFileItemReader<SlimSpecies> speciesCsvFileItemReader(@Value("#{stepExecutionContext[index]}") String index,
                                                              @Value("#{jobParameters['speciesPath']}") String speciesPath){
        //fileReader
        String names = allUtils.getObjectFieldNames(SlimSpecies.class,false);
        String file = speciesPath.substring(0, speciesPath.lastIndexOf("."));
        String ext = speciesPath.substring(speciesPath.lastIndexOf("."));
        String parallelFile = file+index+ext;
        return new AbstractFlatFileItemReader<SlimSpecies>(index,parallelFile, SlimSpecies.class, names) {};
    }

    @Bean
    @StepScope
    public ItemProcessor<SlimSpecies,SlimSpecies> speciesCsvFileItemProcessor(@Value("#{stepExecutionContext[partition]}") final String partition){

        return species -> {
            species.setPartitionKey(partition);
            return species;
        };
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<SlimSpecies> speciesCsvFileItemWriter(){
        String names = allUtils.getObjectFieldNames(SlimSpecies.class,true);
        names = ":"+names.replaceAll(",",":,");

        String sql = "insert into slim_species values("+names+")";
        return new JdbcBatchItemWriterBuilder<SlimSpecies>()
                .dataSource(getDataSource())
                .sql(sql)
                .beanMapped()
                .build();
    }

    /**
     * 4-2.책 파일 reader,writer 생성
     * */
    @Bean
    @StepScope
    public FlatFileItemReader<SlimBook> bookCsvFileItemReader(@Value("#{stepExecutionContext[index]}") String index,
                                                        @Value("#{jobParameters['bookPath']}") String bookPath){
        //fileReader
        String names = allUtils.getObjectFieldNames(SlimBook.class,false);
        String file = bookPath.substring(0, bookPath.lastIndexOf("."));
        String ext = bookPath.substring(bookPath.lastIndexOf("."));
        String parallelFile = file+index+ext;
        return new AbstractFlatFileItemReader<SlimBook>(index,parallelFile, SlimBook.class, names) {};
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<SlimBook> bookCsvFileItemWriter(){

        String names = allUtils.getObjectFieldNames(SlimBook.class,false);

        names = ":"+names.replaceAll(",",":,");

        String sql = "insert into slim_book values ("+names+")";
        return new JdbcBatchItemWriterBuilder<SlimBook>()
                .dataSource(getDataSource())
                .sql(sql)
                .beanMapped()
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<SlimAppendix> appendixCsvFileItemReader(@Value("#{stepExecutionContext[index]}") String index,
                                                                    @Value("#{jobParameters['appendixPath']}") String appendixPath){

        String names = allUtils.getObjectFieldNames(SlimAppendix.class,false);
        String file = appendixPath.substring(0, appendixPath.lastIndexOf("."));
        String ext = appendixPath.substring(appendixPath.lastIndexOf("."));
        String parallelFile = file+index+ext;
        return new AbstractFlatFileItemReader<SlimAppendix>(index,parallelFile, SlimAppendix.class, names) {};
    }

    @Bean
    @StepScope
    public ItemProcessor<SlimAppendix,SlimBook> appendixCsvFileItemProcessor(){

        return appendix -> {
            SlimBook slimBook = new SlimBook();
            slimBook.setSlimSpeciesKey(appendix.getSlimSpeciesKey());
            slimBook.setRegNo(appendix.getRegNo());
            slimBook.setBookAppendixFlag("A");
            slimBook.setLibCode(appendix.getLibCode());
            return slimBook;
        };
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<SlimBook> appendixCsvFileItemWriter(){
        String sql = "insert into slim_book(species_key,reg_no,lib_code,book_appendix_flag) values(:speciesKey,:appendixRegNo,:libCode,:appendixFlag)";
        return new JdbcBatchItemWriterBuilder<SlimBook>()
                .dataSource(getDataSource())
                .sql(sql)
                .beanMapped()
                .build();
    }

}
