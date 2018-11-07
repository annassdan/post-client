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

    @Autowired
    private PostClientTranslator translator;

    private int i = 0;
    private int p;
    private String saveUrl = "";

    public DeepslopeSyncronizer() {
        this.p = 0;
    }

    @Async
    public void executingTaskDeepslopeToEBrpl(LinkedHashMap mappingSetting, int... sleep) {


        executor.execute(() -> {
            try {

                String host = String.valueOf(mappingSetting.get("host"));
                String tempPort = String.valueOf(mappingSetting.get("port"));
                String port = (tempPort == null || tempPort.isEmpty()) ? HTTP_DEFAULT_PORT : tempPort;
                LinkedHashMap api = (LinkedHashMap) mappingSetting.get("api");
                saveUrl = host + ":" + port + String.valueOf(api.get("save"));
                TypeReference<TNCDeepslope> typeReference = new TypeReference<TNCDeepslope>() {};

                List<LinkedHashMap> setting = (List<LinkedHashMap>) mappingSetting.get("mapOfColumns");
                int delay = (int) mappingSetting.get("delayInMilisecond");
                int numberOfDataPerRequest = (int) mappingSetting.get("numberOfDataPerRequest");

                while (true) {
                    logger.info("DEEPSLOPE");
                    TimeUnit.MILLISECONDS.sleep(delay);

                    List<TNCDeepslope> data = tncDeepslopeService.getAllByPostStatus(PostStatus.DRAFT.name(), p, numberOfDataPerRequest);
                    if (data.size() == 0) {
                        if (tncDeepslopeService.countAllByPostStatus(PostStatus.DRAFT.name()) > 0)
                            p = 0;
                    } else {
                        processingTask(data, setting, numberOfDataPerRequest);
                        p = (data.size() < numberOfDataPerRequest) ? 0 : (p + 1);
                    }
                }
            } catch (Exception ignored) {
            }
        });
    }

    private List<BRPLSizing> processingSizingData(Long landingId) {

        return null;
    }


    private synchronized void processingTask(List<TNCDeepslope> tncDeepslopes, List<LinkedHashMap> setting, int numberOfDataPerRequest) {

        LinkedHashMap<String, LinkedHashMap<String, Class>> assocRelationsType = new LinkedHashMap<>();
        LinkedHashMap<String, Class> types = new LinkedHashMap<>();
        types.put("sourceClass", TNCSizing.class);
        types.put("destinationClass", BRPLSizing.class);
        assocRelationsType.put("TNCSizing", types);

        tncDeepslopes.forEach(deepslope -> {
            i++;
            if (deepslope != null){
                LinkedHashMap<String, List<?>> assocRelations = new LinkedHashMap<>();
                long sizingSize = tncSizingService.countAllByLandingIdAndPostStatus(deepslope.getOid(), PostStatus.DRAFT.name());
                List<TNCSizing> sizingList = tncSizingService.getAllByLandingIdAndPostStatus(deepslope.getOid(), PostStatus.DRAFT.name(), 0, (int) sizingSize);
                assocRelations.put("TNCSizing", sizingList);

                BRPLDeepslope brplDeepslope = translator.translateToDestinationClass(TNCDeepslope.class, BRPLDeepslope.class, deepslope, setting, assocRelations, assocRelationsType);
                if (brplDeepslope != null) {
                    Object response = translator.httpRequestPostForObject(saveUrl, brplDeepslope, Object.class);
                    if (response != null) {
                        LinkedHashMap res = (LinkedHashMap) response;
                        String status = String.valueOf(res.get("httpStatus"));
                        if (status.equals("OK")) {
                            deepslope.setPostStatus(PostStatus.POSTED.name());
                            tncDeepslopeService.save(deepslope);
                            tncSizingService.saves(sizingList);
//                            sizingList.forEach(sizing -> {
//                                sizing.setPostStatus(PostStatus.POSTED.name());
//                                tncSizingService.sa
//                            });
                        }
                    }

                    String c = "Untuk Page " + String.valueOf(p + 1) + " ## data Ke-" + String.valueOf(i);
                    logger.info(c);
                }
            }
        });

    }


}
