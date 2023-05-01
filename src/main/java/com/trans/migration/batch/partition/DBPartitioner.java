package com.trans.migration.batch.partition;

import com.trans.migration.batch.domain.MaxMinKey;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

public class DBPartitioner implements Partitioner {

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        //1. 키 최댓값,최솟값
        ExecutionContext jobExecutionContext = JobSynchronizationManager.getContext().getJobExecution().getExecutionContext();
        MaxMinKey maxMinKey = (MaxMinKey)jobExecutionContext.get("maxMinKey");

        //2. gridSize 만큼 파티션 나누기
        long minKey = maxMinKey.getMinKey();
        long maxKey = maxMinKey.getMaxKey();
        long difference = maxKey - minKey + 1;
        long range = difference / gridSize;

        Map<String, ExecutionContext> partitionExecutionContext = new HashMap<>(gridSize);

        for(int i = 0 ; i < gridSize ; i++){
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.putString("partition","partition"+i);
            executionContext.putInt("gridSize",gridSize);
            executionContext.putLong("startKey",minKey + range*i);
            if( i == gridSize -1) executionContext.putLong("endKey",maxKey);
            else executionContext.putLong("endKey",minKey + range*i + (range - 1));
            partitionExecutionContext.put("partition"+i,executionContext);
        }
        return  partitionExecutionContext;
    }
}
