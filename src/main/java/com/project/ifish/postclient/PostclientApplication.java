package com.project.ifish.postclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ifish.postclient.syncronizers.BoatSyncronizer;
import com.project.ifish.postclient.syncronizers.DeepslopeSyncronizer;
import com.project.ifish.postclient.syncronizers.SpeciesSyncronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
public class PostclientApplication implements CommandLineRunner, PostClient {


    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public static List<LinkedHashMap> appConfig;

    @Autowired
    private SpeciesSyncronizer speciesSyncronizer;

    @Autowired
    private DeepslopeSyncronizer deepslopeSyncronizer;

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


    public static LinkedHashMap getSyncronizingSetting(List<LinkedHashMap> settings, @SuppressWarnings("unchecked") String cn) {
        for (LinkedHashMap setting : settings) {
            if (setting.get("className").equals(cn))
                return setting;
        }
        return null;
    }


    @Override
    public void run(String... args) throws Exception {
        List<LinkedHashMap> settings = initMapColumns();
        appConfig = settings;

        LinkedHashMap speciesSetting = getSyncronizingSetting(settings, TNC_SPECIES_CLASS_NAME);
        if (speciesSetting == null) throw new AssertionError();
        speciesSyncronizer.executingTaskSpeciesToEBrpl(speciesSetting);


        LinkedHashMap boatSetting = getSyncronizingSetting(settings, TNC_BOAT_CLASS_NAME);
        if (boatSetting == null) throw new AssertionError();
        boatSyncronizer.executingTaskBoatToEBrpl(boatSetting);


        LinkedHashMap deepslopeSetting = getSyncronizingSetting(settings, TNC_DEEPSLOPE_CLASS_NAME);
        if (deepslopeSetting == null) throw new AssertionError();
        deepslopeSyncronizer.executingTaskDeepslopeToEBrpl(deepslopeSetting);

    }
}
