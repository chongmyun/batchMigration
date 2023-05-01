package com.trans.migration.slima.user.listener;

import com.trans.migration.slima.user.domain.SlimUser;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ExecutionContext;

public class SlimUserProcessorListener implements ItemProcessListener<SlimUser,SlimUser> {

    private long startKey = 1 ;
    @Override
    public void beforeProcess(SlimUser item) {
        //이용자 페이징 조회를 위해 임시키 생성
        ExecutionContext stepExecutionContext = StepSynchronizationManager.getContext().getStepExecution().getExecutionContext();
        item.setTempUserKey(startKey);
        startKey++;
    }

}
