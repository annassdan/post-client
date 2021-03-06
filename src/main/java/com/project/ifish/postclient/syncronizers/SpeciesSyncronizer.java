package com.project.ifish.postclient.syncronizers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.project.ifish.postclient.PostClient;
import com.project.ifish.postclient.PostclientApplication;
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
import org.springframework.web.client.HttpClientErrorException;

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

    public static long isFirstSyncronIsDone = -1;

    private String token = "";

    @Autowired
    private TaskExecutor maxTimer;

    Integer reachedTime = 0;
    int maxTime = 0;

    boolean intteruptedByTimeOut = false;
    int page = -1;

    @Async
    public void executingTaskSpeciesToEBrpl(LinkedHashMap mappingSetting, int... sleep) {
        token = PostclientApplication.validToken;

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
            maxTime = (int) mappingSetting.get("maxTimePerScheduledProcessMinute");
            int numberOfDataPerRequest = (int) mappingSetting.get("numberOfDataPerRequest");

            boolean process;
            int processDelay = (int) mappingSetting.get("scheduleDelayInMinute");
            long processAt = 0;

            while (true) {
                i = 0;
                if (PostclientApplication.enableLogger)
                    logger.info("SPECIES## scheduled process...");
                process = true;

                reachedTime = 0;
                maxTimer.execute(() -> {
                    while (reachedTime <= maxTime) {
                        try {
                            reachedTime++;
                            TimeUnit.MINUTES.sleep(1);
                        } catch (InterruptedException e) {
                            reachedTime = maxTime + 1;
                            e.printStackTrace();
                        }
                    }
                });

//                int page  = -1;
                page = (intteruptedByTimeOut) ? (page - 1) : -1;
                while (process) {
                    try {
                        page++;

                        TimeUnit.MILLISECONDS.sleep(delay);
                        long amountOfData = tncSpeciesService.countAllByPostStatus(PostStatus.DRAFT.name());
                        isFirstSyncronIsDone = amountOfData;
                        if (amountOfData > 0) {
                            List<TNCSpecies> data = tncSpeciesService.getAllByPostStatus(page, numberOfDataPerRequest);
                            if (data == null || data.size() == 0) {
                                intteruptedByTimeOut = false;
                                if (PostclientApplication.enableLogger)
                                    logger.info("Exit to next schedule");
                                break;
                            }

                            processingTask(data, setting);
                            process = amountOfData > numberOfDataPerRequest;
                        } else {
                            process = false;
                        }

                        if (reachedTime > maxTime) {
                            intteruptedByTimeOut = true;
                            if (PostclientApplication.enableLogger)
                                logger.info("Will be process in next time");
                            break;
//                            process = false;
                        }


                    } catch (Exception ignored) {
                        intteruptedByTimeOut = false;
                        break;
                    }
                    intteruptedByTimeOut = false;
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
                if (species != null && species.getPostStatus().toLowerCase().equals(PostStatus.POSTED.name().toLowerCase()))
                    continue;

                if (reachedTime > maxTime) // will be process in next time
                    break;

                i++;
                if (species != null) {
                    BRPLSpecies brplSpecies = translator.translateToDestinationClass(TNCSpecies.class, BRPLSpecies.class, species, setting);
                    if (brplSpecies != null) {
                        try {
                            Object response = null;
                            try {
                                response = translator.httpRequestPostForObject(saveUrl + "?access_token=" + token,
                                        brplSpecies, Object.class);
                            } catch (HttpClientErrorException e) {
                                if (e.getRawStatusCode() == 401) { // unauthorized
                                    if (PostclientApplication.isTokenNotExpired) {
                                        PostclientApplication.isTokenNotExpired = false;
                                        if (PostclientApplication.enableLogger)
                                            logger.info("Unauthorized From Species.....");
                                        TimeUnit.SECONDS.sleep(2);
                                        token = PostclientApplication.requestToken(PostclientApplication.appConfig);
                                        if (PostclientApplication.enableLogger)
                                            logger.info("Got New Token from Species....");
                                        TimeUnit.SECONDS.sleep(2);
                                    }

                                    try {
                                        response = translator.httpRequestPostForObject(saveUrl + "?access_token=" + token,
                                                brplSpecies, Object.class);
                                    } catch (Exception ex) {
                                        reachedTime = maxTime + 1;
                                        break;
                                    }
                                }
                            }
                            if (response != null) {
                                LinkedHashMap res = (LinkedHashMap) response;
                                String status = String.valueOf(res.get("httpStatus"));
                                if (status.equals("OK")) {
                                    species.setPostStatus(PostStatus.POSTED.name());
                                    tncSpeciesService.save(species);

                                    String c = "Species# -- ## percobaan ke-" + String.valueOf(i);
                                    if (PostclientApplication.enableLogger)
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
