package com.project.ifish.postclient.syncronizers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.project.ifish.postclient.PostClient;
import com.project.ifish.postclient.models.atbrpl.BRPLSpecies;
import com.project.ifish.postclient.models.attnc.TNCSpecies;
import com.project.ifish.postclient.services.TNCSpeciesService;
import com.project.ifish.postclient.utils.PostClientTranslator;
import com.project.ifish.postclient.utils.PostStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings("unused")

public class SpeciesSyncronizer implements PostClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private TNCSpeciesService tncSpeciesService;

    @Autowired
    private TaskExecutor executor;

    @Autowired
    private PostClientTranslator translator;

    private int i = 0;
    private String saveUrl;

    @Async
    public void executingTaskSpeciesToEBrpl(LinkedHashMap mappingSetting, int... sleep) {
        executor.execute(() -> {

            String host = String.valueOf(mappingSetting.get("host"));
            String tempPort = String.valueOf(mappingSetting.get("port"));
            String port = (tempPort == null || tempPort.isEmpty()) ? HTTP_DEFAULT_PORT : tempPort;
            LinkedHashMap api = (LinkedHashMap) mappingSetting.get("api");
            saveUrl = host + ":" + port + String.valueOf(api.get("save"));
            TypeReference<TNCSpecies> typeReference = new TypeReference<TNCSpecies>() {
            };

            List<LinkedHashMap> setting = ((List<LinkedHashMap>) mappingSetting.get("mapOfColumns"));
            int delay = (int) mappingSetting.get("delayInMilisecond");
            int numberOfDataPerRequest = (int) mappingSetting.get("numberOfDataPerRequest");

            boolean process;
            int processDelay = (int) mappingSetting.get("scheduleDelayInMinute");
            long processAt = 0;
            while (true) {
                processAt++;
                int stopProcess = 0;
                i = 0;
                process = true;
                while (process) {
                    try {
                        logger.info("SPECIES## untuk proses ke-" + String.valueOf(processAt));
                        TimeUnit.MILLISECONDS.sleep(delay);

                        long amountOfData = tncSpeciesService.countAllByPostStatus(PostStatus.DRAFT.name());
                        if (amountOfData > 0) {
                            List<TNCSpecies> data = tncSpeciesService.getAllByPostStatus(PostStatus.DRAFT.name(), 0, numberOfDataPerRequest);
                            processingTask(data, setting);
                            process = (amountOfData <= numberOfDataPerRequest) ? false : true;
                        } else {
                            process = false;
                        }

//                                List<TNCSpecies> data = tncSpeciesService.getAllByPostStatus(PostStatus.DRAFT.name(), p, numberOfDataPerRequest);
//                        if (data.size() == 0) {
//                            if (tncSpeciesService.countAllByPostStatus(PostStatus.DRAFT.name()) > 0)
//                                p = 0;
//                        } else {
//                            processingTask(data, setting);
//                            p = (data.size() < numberOfDataPerRequest) ? 0 : (p + 1);
//                        }
                    } catch (Exception ignored) {
                    }
                }

                try {
                    TimeUnit.MINUTES.sleep(processDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
    }


    private synchronized void processingTask(List<TNCSpecies> tncSpecies, List<LinkedHashMap> setting) {

        for (TNCSpecies species : tncSpecies) {
            try {
                i++;
                if (species != null) {
                    BRPLSpecies brplSpecies = translator.translateToDestinationClass(TNCSpecies.class, BRPLSpecies.class, species, setting);
                    if (brplSpecies != null) {
                        try {
                            Object response = translator.httpRequestPostForObject(saveUrl, brplSpecies, Object.class);
                            if (response != null) {
                                LinkedHashMap res = (LinkedHashMap) response;
                                String status = String.valueOf(res.get("httpStatus"));
                                if (status.equals("OK")) {
                                    species.setPostStatus(PostStatus.POSTED.name());
                                    tncSpeciesService.save(species);

                                    String c = "Species# -- ## data Ke-" + String.valueOf(i);
                                    logger.info(c);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

}
