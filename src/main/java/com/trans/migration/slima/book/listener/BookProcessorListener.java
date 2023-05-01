package com.trans.migration.slima.book.listener;

import com.trans.migration.slima.book.domain.BoSpecies;
import com.trans.migration.slima.book.domain.SlimSpecies;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

public class BookProcessorListener implements ItemProcessListener<SlimSpecies, BoSpecies> {
    @Override
    public void onProcessError(SlimSpecies item, Exception e) {
        StepContext context = StepSynchronizationManager.getContext();
        ExecutionContext stepExecutionContext = context.getStepExecution().getExecutionContext();
        List<SlimSpecies> errorList = (List<SlimSpecies>) stepExecutionContext.get("errorList");

        if(errorList == null ){
            errorList = new ArrayList<>();
            stepExecutionContext.put("errorList",errorList);
        }

        errorList.add(item);
    }
}
