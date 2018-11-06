package com.project.ifish.postclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ifish.postclient.syncronizers.BoatSyncronizer;
import com.project.ifish.postclient.syncronizers.SpeciesSyncronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
public class PostclientApplication implements CommandLineRunner, PostClient {


    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private SpeciesSyncronizer speciesSyncronizer;

    @Autowired
    @SuppressWarnings("unused")
    private BoatSyncronizer boatSyncronizer;

    public PostclientApplication(SpeciesSyncronizer speciesSyncronizer) {
        this.speciesSyncronizer = speciesSyncronizer;
    }

    /**
     * inisialisasi time format selalu mengikut pada waktu daerah yang dimaksud
     */
    @PostConstruct
    @SuppressWarnings("unused")
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }


    public static void main(String[] args) {
        SpringApplication.run(PostclientApplication.class, args);
    }

    private List<LinkedHashMap> initMapColumns() throws IOException {
        File settingsFile = new File("settings/map.tnctobrpl.json");
        TypeReference<List<LinkedHashMap>> typeReference = new TypeReference<List<LinkedHashMap>>() {
        };

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(settingsFile, typeReference);
    }


    private static LinkedHashMap getSyncronizingSetting(List<LinkedHashMap> settings, @SuppressWarnings("unchecked") String cn) {
        for (LinkedHashMap setting : settings) {
            if (setting.get("className").equals(cn))
                return setting;
        }
        return null;
    }


    @Override
    public void run(String... args) throws Exception {

		RestTemplate restTemplate = new RestTemplate();
		String url = "http://localhost:4002/api/sampling/operasional/?page=0&size=100";

//		ResponseEntity responseEntity;




//		responseEntity.getB

		Object response = restTemplate.getForObject(url, Object.class);
//        speciesSyncronizer.executingTaskSpeciesToEBrpl(100);


//		String response = restTemplate.getForObject(url, String.class);

//		restTemplate.
//		TNCBoat tncBoat = TNCBoat.builder().captain("Annas").build();
//		LinkedHashMap linkedHashMap = (LinkedHashMap) ((Object) tncBoat);

//		LinkedHashMap map = (LinkedHashMap) response;

//		ResponseResolver resolver = (ResponseResolver) response;
//		ObjectMapper mapper = new ObjectMapper();

//		String map1 = mapper.writeValueAsString(response);
//		Object o = mapper.readValue(mapper.writeValueAsBytes(tncBoat), Object.class);
//		LinkedHashMap linkedHashMap = (LinkedHashMap) response;
//		JsonNode root = mapper.readTree(response);
//		logger.info((String) linkedHashMap.get("captain"));
//		logger.info(response);
//		taskConnector.executingTaskToEBrpl("Dan", 10);
//		taskConnector.executingTaskToEBrpl("Annas");


//        List<LinkedHashMap> linkedHashMaps = new ArrayList<>();
//        List<TNCBoat> tncBoats = boatSyncronizer.getBoatData(0, 10);

//        logger.info(map1);

        List<LinkedHashMap> settings = initMapColumns();
//        LinkedHashMap speciesSetting = getSyncronizingSetting(settings, TNC_SPECIES_CLASS_NAME);
//        if (speciesSetting == null) throw new AssertionError();
//        speciesSyncronizer.executingTaskSpeciesToEBrpl(speciesSetting);

        LinkedHashMap boatSetting = getSyncronizingSetting(settings, TNC_BOAT_CLASS_NAME);
        if (boatSetting == null) throw new AssertionError();
        boatSyncronizer.executingTaskBoatToEBrpl(boatSetting);


//        final ObjectMapper mapper1 = new ObjectMapper();
//
//        tncBoats.forEach((boat) -> {
//            LinkedHashMap boatHashMap = null;
//            try {
//                if (boat != null)
//                    boatHashMap = (LinkedHashMap) (mapper1.readValue(mapper1.writeValueAsBytes(boat), Object.class));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (boatHashMap != null)
//                linkedHashMaps.add(boatHashMap);
//
//
//            logger.info(String.valueOf(i++) + " - ");
//        });
//        logger.info("END");


//		logger.info(tncBoats.toString());


    }
}
