package com.project.ifish.postclient.syncronizers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.project.ifish.postclient.PostClient;
import com.project.ifish.postclient.models.atbrpl.BRPLDeepslope;
import com.project.ifish.postclient.models.atbrpl.BRPLSizing;
import com.project.ifish.postclient.models.attnc.TNCDeepslope;
import com.project.ifish.postclient.models.attnc.TNCSizing;
import com.project.ifish.postclient.services.TNCDeepslopeService;
import com.project.ifish.postclient.services.TNCSizingService;
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
public class DeepslopeSyncronizer implements PostClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private TNCDeepslopeService tncDeepslopeService;

    @Autowired
    private TNCSizingService tncSizingService;

    @Autowired
    private TaskExecutor executor;

    private PostClientTranslator translator;

    private int i = 0;
    private String saveUrl = "";

    @Async
    public void executingTaskDeepslopeToEBrpl(LinkedHashMap mappingSetting, int... sleep) {

        executor.execute(() -> {
            String host = String.valueOf(mappingSetting.get("host"));
            String tempPort = String.valueOf(mappingSetting.get("port"));
            String port = (tempPort == null || tempPort.isEmpty()) ? HTTP_DEFAULT_PORT : tempPort;
            LinkedHashMap api = (LinkedHashMap) mappingSetting.get("api");
            saveUrl = host + ":" + port + String.valueOf(api.get("save"));
            TypeReference<TNCDeepslope> typeReference = new TypeReference<TNCDeepslope>() {
            };

            List<LinkedHashMap> setting = (List<LinkedHashMap>) mappingSetting.get("mapOfColumns");
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
                        logger.info("DEEPSLOPE## untuk proses ke-" + String.valueOf(processAt));
                        TimeUnit.MILLISECONDS.sleep(delay);

                        long amountOfData = tncDeepslopeService.countAllByPostStatus(PostStatus.DRAFT.name());
                        if (amountOfData > 0) {
                            List<TNCDeepslope> data = tncDeepslopeService.getAllByPostStatus(PostStatus.DRAFT.name(), 0, numberOfDataPerRequest);
                            processingTask(data, setting);
                            process = amountOfData > numberOfDataPerRequest;
                        } else {
                            process = false;
                        }

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


    private synchronized void processingTask(List<TNCDeepslope> tncDeepslopes, List<LinkedHashMap> setting) {

        LinkedHashMap<String, LinkedHashMap<String, Class>> assocRelationsType = new LinkedHashMap<>();
        LinkedHashMap<String, Class> types = new LinkedHashMap<>();
        types.put("sourceClass", TNCSizing.class);
        types.put("destinationClass", BRPLSizing.class);
        assocRelationsType.put("TNCSizing", types);

        for (TNCDeepslope deepslope : tncDeepslopes) {
            try {
                i++;
                if (deepslope != null) {
                    LinkedHashMap<String, List<?>> assocRelations = new LinkedHashMap<>();
                    long sizingSize = tncSizingService.countAllByLandingIdAndPostStatus(deepslope.getOid(), PostStatus.DRAFT.name());
                    List<TNCSizing> sizingList;
                    if (sizingSize > 0) {
                        sizingList = tncSizingService.getAllByLandingIdAndPostStatus(deepslope.getOid(), PostStatus.DRAFT.name(), 0, (int) sizingSize);
                        assocRelations.put("TNCSizing", sizingList);

                        BRPLDeepslope brplDeepslope = translator.translateToDestinationClass(TNCDeepslope.class, BRPLDeepslope.class, deepslope, setting, assocRelations, assocRelationsType);
                        if (brplDeepslope != null) {
                            try {
                                Object response = translator.httpRequestPostForObject(saveUrl, brplDeepslope, Object.class);
                                if (response != null) {
                                    LinkedHashMap res = (LinkedHashMap) response;
                                    String status = String.valueOf(res.get("httpStatus"));
                                    if (status.equals("OK")) {
                                        deepslope.setPostStatus(PostStatus.POSTED.name());
                                        tncDeepslopeService.save(deepslope);
                                        for (TNCSizing sizing : sizingList) {
                                            sizing.setPostStatus(PostStatus.POSTED.name());
                                            tncSizingService.save(sizing);
                                        }
                                        String c = "Deepslope# -- ## data Ke-" + String.valueOf(i);
                                        logger.info(c);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }
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
