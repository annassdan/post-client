package com.project.ifish.postclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ifish.postclient.syncronizers.BoatSyncronizer;
import com.project.ifish.postclient.syncronizers.DeepslopeSyncronizer;
import com.project.ifish.postclient.syncronizers.SpeciesSyncronizer;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
// test_db_brpl_ifish
@SuppressWarnings("unused")
public class PostclientApplication implements CommandLineRunner, PostClient {

    public static boolean enableLogger = false;

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public static List<LinkedHashMap> appConfig;

    public static boolean isTokenNotExpired = false;

    public static String validToken = "";

    @Autowired
    private SpeciesSyncronizer speciesSyncronizer;

    @Autowired
    private DeepslopeSyncronizer deepslopeSyncronizer;

    @Autowired
    private BoatSyncronizer boatSyncronizer;

    @Autowired
    private TaskExecutor starter;

    @Autowired
    private TaskExecutor mainProcess;

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

    List<LinkedHashMap> settings = null;

    @Async
    private void initProcess() {

        starter.execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(30);
                logger.info("Started...");

                boolean init = true;
                while (init) {
                    try {
                        settings = initMapColumns();
                        requestToken(settings); // request first token
                        appConfig = settings;

                        init = false;
                    } catch (Exception e) {
                        logger.info("Re-initializing and connecting to ebrpl server...");
                    }
                    TimeUnit.SECONDS.sleep(2);

                }

                mainProcess.execute(() -> {
                    try {
                        /// waiting for master data sincronized
                        logger.info("** Waiting for syncronized of master data...");
                        TimeUnit.SECONDS.sleep(10);
                        boolean wait = true;
                        while (wait) {
                            try {
                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            wait = !(SpeciesSyncronizer.isFirstSyncronIsDone == 0 && BoatSyncronizer.isFirstSyncronIsDone == 0);
                        }

                        logger.info("#Deepslope #First Starting for Deepslope Syncronizer in 3 seconds ......");
                        TimeUnit.SECONDS.sleep(3);
                        LinkedHashMap deepslopeSetting = getSyncronizingSetting(settings, TNC_DEEPSLOPE_CLASS_NAME);
                        if (deepslopeSetting == null) throw new AssertionError();
                        deepslopeSyncronizer.executingTaskDeepslopeToEBrpl(deepslopeSetting);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                });


                logger.info("#Species #First Starting for Species Syncronizer in 2 seconds ......");
                TimeUnit.SECONDS.sleep(2);
                LinkedHashMap speciesSetting = getSyncronizingSetting(settings, TNC_SPECIES_CLASS_NAME);
                if (speciesSetting == null) throw new AssertionError();
                speciesSyncronizer.executingTaskSpeciesToEBrpl(speciesSetting);

                logger.info("#Boat #First Starting for Boat Syncronizer in 2 seconds ......");
                TimeUnit.SECONDS.sleep(2);
                LinkedHashMap boatSetting = getSyncronizingSetting(settings, TNC_BOAT_CLASS_NAME);
                if (boatSetting == null) throw new AssertionError();
                boatSyncronizer.executingTaskBoatToEBrpl(boatSetting);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        });

    }


    @Override
    public void run(String... args) throws Exception {
        logger.info("*** Initializing all syncronizer in 30 seconds.......");
        initProcess();
    }

    public static synchronized String requestToken(List<LinkedHashMap> setting) {
        LinkedHashMap map = getSyncronizingSetting(setting, BRPL_REQUEST_TOKEN_SETTING);
        LinkedHashMap reqTokenSetting = (LinkedHashMap) map.get("settings");

        PostclientApplication.enableLogger = (boolean) reqTokenSetting.get("enableLogger");
        String host = (String) reqTokenSetting.get("host");
        String port = (String) reqTokenSetting.get("port");
        String user = (String) reqTokenSetting.get("ifishUsername");
        String pass = (String) reqTokenSetting.get("ifishPassword");
        String reqTokenPath = (String) reqTokenSetting.get("authReqTokenPath");
        String trustedClient = (String) reqTokenSetting.get("trustedClient");
        String trustedClientId = (String) reqTokenSetting.get("secretId");

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = getHeaders(trustedClient, trustedClientId);
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        String url = new StringBuilder()
                .append(host).append(":").append(port).append(reqTokenPath)
                .append("?username=").append(user)
                .append("&password=").append(pass)
                .append("&grant_type=password").toString();
        ResponseEntity<LinkedHashMap> o = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                LinkedHashMap.class);

        validToken = (String) o.getBody().get("access_token");
        isTokenNotExpired = true;

        return (String) o.getBody().get("access_token");
    }


    private static HttpHeaders getHeaders(String username, String password) {
        String plainCredentials = username + ":" + password;
        String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
