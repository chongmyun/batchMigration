package com.trans.migration.slima.book.runjob;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class JobStartComponent {

    private final JobLauncher jobLauncher;

    private final Job bookCsvFileJob;

    private final Environment env;

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @Bean
    @ConditionalOnProperty(value="jobProperty" ,havingValue = "bookCsvFileJob")
    public void bookCsvFileJobRunner() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        String gridSize = env.getProperty("gridSize") != null ? env.getProperty("gridSize") : "1";
        String speciesPath = env.getProperty("speciesPath") != null ? env.getProperty("speciesPath") : "";
        if(speciesPath.equals("")) System.exit(0);
        JobParameters jobParameters = jobParametersBuilder.addString("GridSize", gridSize).addString("speciesPath",speciesPath).toJobParameters();
        jobLauncher.run(bookCsvFileJob,jobParameters);
    }

//    @Bean
//    @ConditionalOnProperty(value="jobProperty" ,havingValue = "transferDBJob")
//    public void transferDBRunner() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
//        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
//        JobParameters jobParameters = jobParametersBuilder.addString("id", "chong").toJobParameters();
//        jobLauncher.run(transferDBJob,jobParameters);
//    }

}
