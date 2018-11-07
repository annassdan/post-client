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
    private int p;
    private String saveUrl;

    public SpeciesSyncronizer() {
        this.p = 0;
    }

    @Async
    public void executingTaskSpeciesToEBrpl(LinkedHashMap mappingSetting, int... sleep) {
        executor.execute(() -> {
            try {
                String host = String.valueOf(mappingSetting.get("host"));
                String tempPort = String.valueOf(mappingSetting.get("port"));
                String port = (tempPort == null || tempPort.isEmpty()) ? HTTP_DEFAULT_PORT : tempPort;
                LinkedHashMap api = (LinkedHashMap)  mappingSetting.get("api");
                saveUrl = host + ":" + port + String.valueOf(api.get("save"));
                TypeReference<TNCSpecies> typeReference = new TypeReference<TNCSpecies>() {};

                List<LinkedHashMap> setting = ((List<LinkedHashMap>) mappingSetting.get("mapOfColumns"));
                int delay = (int) mappingSetting.get("delayInMilisecond");
                int numberOfDataPerRequest = (int) mappingSetting.get("numberOfDataPerRequest");

                while (true) {
                    i = 0;
                    logger.info("SPECIES");
                    TimeUnit.MILLISECONDS.sleep(delay);

                    List<TNCSpecies> data = tncSpeciesService.getAllByPostStatus(PostStatus.DRAFT.name(), p, numberOfDataPerRequest);
                    if (data.size() == 0) {
                        if (tncSpeciesService.countAllByPostStatus(PostStatus.DRAFT.name()) > 0)
                            p = 0;
                    } else {
                        processingTask(data, setting);
                        p = (data.size() < numberOfDataPerRequest) ? 0 : (p + 1);
                    }
                }
            } catch (Exception ignored) {
                p = 0;
            }
        });
    }



    private synchronized void processingTask(List<TNCSpecies> tncSpecies, List<LinkedHashMap> setting) {

        tncSpecies.forEach(species -> {
            i++;
            if (species != null){
                BRPLSpecies brplSpecies = translator.translateToDestinationClass(TNCSpecies.class, BRPLSpecies.class, species, setting);
                if (brplSpecies != null) {
                    Object response = translator.httpRequestPostForObject(saveUrl, brplSpecies, Object.class);
                    if (response != null) {
                        LinkedHashMap res = (LinkedHashMap) response;
                        String status = String.valueOf(res.get("httpStatus"));
                        if (status.equals("OK")) {
                            species.setPostStatus(PostStatus.POSTED.name());
                            tncSpeciesService.save(species);
                        }
                    }

                    String c = "Untuk Page " + String.valueOf(p + 1) + " ## data Ke-" + String.valueOf(i);
                    logger.info(c);
                }
            }

        });
    }

}
