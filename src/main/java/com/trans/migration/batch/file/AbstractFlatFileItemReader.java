package com.trans.migration.batch.file;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;

public abstract class AbstractFlatFileItemReader<T> extends FlatFileItemReader<T> {

    public AbstractFlatFileItemReader(String index, String filePath, Class<T> target, String names) {

        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        setResource(fileSystemResource);
        if(index.equals("1")){
            setLinesToSkip(1);
        }

        setEncoding("UTF-8");
        DefaultLineMapper<T> defaultLineMapper = new DefaultLineMapper();
        BeanWrapperFieldSetMapper<T> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(target);
        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(",");
        delimitedLineTokenizer.setNames(names.split(","));
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        setLineMapper(defaultLineMapper);
    }

}
