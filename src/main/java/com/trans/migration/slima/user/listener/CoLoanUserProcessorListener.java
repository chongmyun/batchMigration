package com.trans.migration.slima.user.listener;

import com.trans.migration.slima.user.domain.CoLoanUser;
import com.trans.migration.slima.user.domain.SlimUser;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

public class CoLoanUserProcessorListener implements ItemProcessListener<SlimUser, CoLoanUser> {

    @Override
    public void onProcessError(SlimUser item, Exception e) {
        StepContext context = StepSynchronizationManager.getContext();
        ExecutionContext stepExecutionContext = context.getStepExecution().getExecutionContext();

        List<SlimUser> errorList = (List<SlimUser>) stepExecutionContext.get("errorList");

        if(errorList == null ){
            errorList = new ArrayList<>();
            stepExecutionContext.put("errorList",errorList);
        }

        errorList.add(item);
    }
}
