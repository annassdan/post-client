package com.project.ifish.postclient.syncronizers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings("unused")
public class SpeciesSyncronizer implements PostClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    TNCSpeciesService tncSpeciesService;

    @Autowired
    private TaskExecutor executor;

    @Autowired
    private PostClientTranslator translator;

    int i = 0;
    int p = 0;

    public List<TNCSpecies> getAllData(int page, int size) {
        return tncSpeciesService.getAll(page, size);
    }

    public List<TNCSpecies> getAllByPostStatus(String status, int page, int size) {
        return tncSpeciesService.getAllByPostStatus(status, page, size);
    }


    @Async
    public void executingTaskSpeciesToEBrpl(LinkedHashMap mappingSetting, int... sleep) {
        int delay = (int) mappingSetting.get("delayInSecond");
        int numberOfDataPerRequest = (int) mappingSetting.get("numberOfDataPerRequest");

        executor.execute(() -> {
            try {
                while (true) {
                    i = 0;
                    logger.info("SPECIES");
                    if (sleep.length > 0)
                        TimeUnit.SECONDS.sleep(sleep[0]);
                    else
                        TimeUnit.SECONDS.sleep(delay);

                    long size = processingTask(mappingSetting, numberOfDataPerRequest);
                    p = (size == 0) ? 0 : (p + 1);
//                    p++;
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        });
    }


    /**
     *
     * @param mappingSetting
     * @param numberOfDataPerRequest
     * @return
     */
    private synchronized long processingTask(LinkedHashMap mappingSetting, int numberOfDataPerRequest) {
        List<TNCSpecies> tncSpecies = getAllByPostStatus(PostStatus.DRAFT.name(), p, numberOfDataPerRequest);
        long speciesSize = tncSpecies.size();
        String host = String.valueOf(mappingSetting.get("host"));
        String tempPort = String.valueOf(mappingSetting.get("port"));
        String port = (tempPort == null || tempPort.isEmpty()) ? HTTP_DEFAULT_PORT : tempPort;
        LinkedHashMap api = (LinkedHashMap)  mappingSetting.get("api");

        TypeReference<TNCSpecies> typeReference = new TypeReference<TNCSpecies>() {};
        final ObjectMapper mapper = new ObjectMapper();

        List<LinkedHashMap> setting = (List<LinkedHashMap>) mappingSetting.get("mapOfColumns");

        tncSpecies.forEach(species -> {
            i++;
            LinkedHashMap map = null;
            try {
                if (species != null)
                    map = (LinkedHashMap) (mapper.readValue(mapper.writeValueAsBytes(species), Object.class));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (map != null) {
                    BRPLSpecies brplSpecies = null;
                    LinkedHashMap o = translator.translate(setting, map);
                    try {
                        String s = mapper.writeValueAsString(o);
                        brplSpecies = mapper.readValue(s, BRPLSpecies.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (brplSpecies != null) {
                            RestTemplate restTemplate = new RestTemplate();
                            String url = host + ":" + port + String.valueOf(api.get("save"));
                            Object response = restTemplate.postForObject(url, brplSpecies, Object.class);
                            LinkedHashMap res = (LinkedHashMap) response;
                            if (response != null) {
                                String status = String.valueOf(res.get("httpStatus"));
                                if (status.equals("OK")) {
                                    species.setPostStatus(PostStatus.POSTED.name());
                                    tncSpeciesService.save(species);
                                }
                            } else {

                            }

                            String c = "Proses Data Ke-" + String.valueOf(i);
                            logger.info(response.toString());

                        } else {

                        }
                    }
                }
            }


        });

        return speciesSize;
    }

}
