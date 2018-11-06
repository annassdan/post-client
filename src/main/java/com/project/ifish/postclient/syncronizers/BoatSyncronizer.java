package com.project.ifish.postclient.syncronizers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ifish.postclient.PostClient;
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
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings("unused")
public class BoatSyncronizer implements PostClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    TNCBoatService boatService;

    @Autowired
    private TaskExecutor executor;

    @Autowired
    private PostClientTranslator translator;

    int i = 0;
    int p;

    public BoatSyncronizer() {
        this.p = 0;
    }

    public List<TNCBoat> getBoatData(int page, int size) {
        return boatService.getAll(page, size);
    }

    public List<TNCBoat> getAllByPostStatus(String status, int page, int size) {
        return boatService.getAllByPostStatus(status, page, size);
    }

    @Async
    public void executingTaskBoatToEBrpl(LinkedHashMap mappingSetting, int... sleep) {
        int delay = (int) mappingSetting.get("delayInSecond");
        int numberOfDataPerRequest = (int) mappingSetting.get("numberOfDataPerRequest");

        executor.execute(() -> {
            try {
                while (true) {
                    logger.info("BOAT");
//                    if (sleep.length > 0)
//                        TimeUnit.SECONDS.sleep(sleep[0]);
//                    else
                    TimeUnit.MILLISECONDS.sleep(delay);

                    List<TNCBoat> data = getAllByPostStatus(PostStatus.DRAFT.name(), p, numberOfDataPerRequest);
                    if (data.size() == 0) {
                        if (boatService.countAllByPostStatus(PostStatus.DRAFT.name()) > 0)
                            p = 0;
                        else
                            continue;
                    } else {
                        processingTask(data, mappingSetting, numberOfDataPerRequest);
                        p = (data.size() < numberOfDataPerRequest) ? 0 : (p + 1);
                    }
                }
            } catch (Exception ignored) {
                p = 0;
            }
        });
    }


    private synchronized void processingTask(List<TNCBoat> tncboats, LinkedHashMap mappingSetting, int numberOfDataPerRequest) {

//        long boatSize = tncboats.size();
        String host = String.valueOf(mappingSetting.get("host"));
        String tempPort = String.valueOf(mappingSetting.get("port"));
        String port = (tempPort == null || tempPort.isEmpty()) ? HTTP_DEFAULT_PORT : tempPort;
        LinkedHashMap api = (LinkedHashMap) mappingSetting.get("api");

        TypeReference<TNCBoat> typeReference = new TypeReference<TNCBoat>() {
        };
        final ObjectMapper mapper = new ObjectMapper();

        List<LinkedHashMap> setting = (List<LinkedHashMap>) mappingSetting.get("mapOfColumns");

        tncboats.forEach(boat -> {
            i++;
            LinkedHashMap map = null;
            try {
                if (boat != null)
                    map = (LinkedHashMap) (mapper.readValue(mapper.writeValueAsBytes(boat), Object.class));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (map != null) {
                    BRPLBoat brplBoat = null;
                    LinkedHashMap o = translator.translate(setting, map);
                    try {
                        String s = mapper.writeValueAsString(o);
                        brplBoat = mapper.readValue(s, BRPLBoat.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (brplBoat != null) {
                            RestTemplate restTemplate = new RestTemplate();
                            String url = host + ":" + port + String.valueOf(api.get("save"));
                            Object response = restTemplate.postForObject(url, brplBoat, Object.class);
                            LinkedHashMap res = (LinkedHashMap) response;
                            if (response != null) {
                                String status = String.valueOf(res.get("httpStatus"));
                                if (status.equals("OK")) {
                                    boat.setPostStatus(PostStatus.POSTED.name());
                                    boatService.save(boat);
                                }
                            } else {
                            }

                            String c = "Untuk Page " + String.valueOf(p + 1) + " ## data Ke-" + String.valueOf(i);
                            logger.info(c);

                        } else {

                        }
                    }
                }
            }


        });

//        return true;
    }

}
