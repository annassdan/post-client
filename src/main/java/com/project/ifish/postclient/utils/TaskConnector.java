package com.project.ifish.postclient.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings("unused")
public class TaskConnector {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private TaskExecutor executor;

    @Async
    public void executingTaskToEBrpl(String m, int... sleep) {
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
