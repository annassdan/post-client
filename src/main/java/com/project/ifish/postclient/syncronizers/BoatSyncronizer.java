package com.project.ifish.postclient.syncronizers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.project.ifish.postclient.PostClient;
import com.project.ifish.postclient.PostclientApplication;
import com.project.ifish.postclient.models.atbrpl.BRPLBoat;
import com.project.ifish.postclient.models.attnc.TNCBoat;
import com.project.ifish.postclient.services.TNCBoatService;
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
public class BoatSyncronizer implements PostClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private TNCBoatService boatService;

    @Autowired
    private TaskExecutor executor;

    @Autowired
    private PostClientTranslator translator;

    private int i = 0;
    private String saveUrl = "";

    public static long isFirstSyncronIsDone = 0;

    private String token = "";

    @Autowired
    private TaskExecutor maxTimer;

    Integer reachedTime = 0;
    int maxTime = 0;


    @Async
    public void executingTaskBoatToEBrpl(LinkedHashMap mappingSetting, int... sleep) {

        token = PostclientApplication.validToken;


        executor.execute(() -> {

            String host = String.valueOf(mappingSetting.get("host"));
            String tempPort = String.valueOf(mappingSetting.get("port"));
            String port = (tempPort == null || tempPort.isEmpty()) ? HTTP_DEFAULT_PORT : tempPort;
            LinkedHashMap api = (LinkedHashMap) mappingSetting.get("api");
            saveUrl = host + ":" + port + String.valueOf(api.get("save"));
            TypeReference<TNCBoat> typeReference = new TypeReference<TNCBoat>() {
            };

            List<LinkedHashMap> setting = (List<LinkedHashMap>) mappingSetting.get("mapOfColumns");
            int delay = (int) mappingSetting.get("delayInMilisecond");
            maxTime = (int) mappingSetting.get("maxTimePerScheduledProcessMinute");
            int numberOfDataPerRequest = (int) mappingSetting.get("numberOfDataPerRequest");

            boolean process;
            int processDelay = (int) mappingSetting.get("scheduleDelayInMinute");

            List<TNCBoat> data;
            while (true) {
                i = 0;
                logger.info("BOAT## scheduled process...");
                process = true;

                reachedTime = 0;
                maxTimer.execute(() -> {
                    while (reachedTime <= 20) {
                        try {
                            reachedTime++;
                            TimeUnit.MINUTES.sleep(1);
                        } catch (InterruptedException e) {
                            reachedTime = maxTime + 1;
                            e.printStackTrace();
                        }
                    }
                });

                while (process) {
                    try {

                        TimeUnit.MILLISECONDS.sleep(delay);

                        long amountOfData = boatService.countAllByPostStatus(PostStatus.DRAFT.name());
                        isFirstSyncronIsDone = amountOfData;
                        if (amountOfData > 0) {
                            data = boatService.getAllByPostStatus(PostStatus.DRAFT.name(), 0, numberOfDataPerRequest);
                            processingTask(data, setting);

                            process = amountOfData > numberOfDataPerRequest;
                        } else {
                            process = false;
                        }

                        if (reachedTime > maxTime){
                            logger.info("Maximum Timeout has reached... will be process in next time");
                            process = true;
                        }


                    } catch (Exception ignored) {
                        break;
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


    private synchronized void processingTask(List<TNCBoat> tncboats, List<LinkedHashMap> setting) {

        for (TNCBoat boat : tncboats)
            try {
                if (reachedTime > maxTime) // will be process in next time
                    break;

                i++;
                if (boat != null) {
                    BRPLBoat brplBoat = translator.translateToDestinationClass(TNCBoat.class, BRPLBoat.class, boat, setting);

                    if (brplBoat != null) {
                        try {
                            Object response = null;
                            try {
                                response = translator.httpRequestPostForObject(saveUrl + "?access_token=" + token,
                                        brplBoat, Object.class);
                            } catch (HttpClientErrorException e) {
                                if (e.getRawStatusCode() == 401) { // unauthorized
                                    if (PostclientApplication.isTokenNotExpired) {
                                        PostclientApplication.isTokenNotExpired = false;
                                        logger.info("Unauthorized from Boat...");
                                        TimeUnit.SECONDS.sleep(2);
                                        token = PostclientApplication.requestToken(PostclientApplication.appConfig);
                                        logger.info("Got New Token from Boat....");
                                        TimeUnit.SECONDS.sleep(2);
                                    }

                                    try {
                                        response = translator.httpRequestPostForObject(saveUrl + "?access_token=" + token,
                                            brplBoat, Object.class);
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
                                    boat.setPostStatus(PostStatus.POSTED.name());
                                    boatService.save(boat);

                                    String c = "Boat# -- ## percobaan ke-" + String.valueOf(i);
                                    logger.info(c);
                                }
                                res.clear();
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
