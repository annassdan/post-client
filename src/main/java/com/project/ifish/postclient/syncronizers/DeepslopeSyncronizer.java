package com.project.ifish.postclient.syncronizers;

import com.project.ifish.postclient.models.attnc.TNCDeepslope;
import com.project.ifish.postclient.services.TNCDeepslopeService;
import com.project.ifish.postclient.services.TNCSizingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings("unused")
public class DeepslopeSyncronizer {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    TNCDeepslopeService tncDeepslopeService;

    @Autowired
    TNCSizingService tncSizingService;

    @Autowired
    private TaskExecutor executor;

    public List<TNCDeepslope> getAllData(int page, int size) {
        return tncDeepslopeService.getAll(page, size);
    }

    @Async
    public void executingTaskDeepslopeToEBrpl(String m, int... sleep) {
        executor.execute(() -> {
            try {
                while (true) {
                    if (sleep.length > 0)
                        TimeUnit.SECONDS.sleep(sleep[0]);
                    else
                        TimeUnit.SECONDS.sleep(1);
                    logger.info(m);
                }
            } catch (Exception ignored) {
            }
        });
    }


}
