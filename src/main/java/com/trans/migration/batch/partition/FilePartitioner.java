package com.trans.migration.batch.partition;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

public class FilePartitioner implements Partitioner {
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitionExecutionContext = new HashMap<>(gridSize);

        for(int i = 1 ; i<=gridSize ; i++){
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.putString("partition","partition"+i);
            executionContext.putInt("index",i);
            executionContext.putInt("gridSize",gridSize);
            partitionExecutionContext.put("partition"+i,executionContext);
        }
        return  partitionExecutionContext;
    }
}
