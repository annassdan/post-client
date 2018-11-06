package com.project.ifish.postclient;

@SuppressWarnings("unused")
public interface PostClient {

    String DATE_PATTERN = "dd/MM/yyyy";
    String TIME_PATTERN = "HH:mm";
    String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss.SSS";
    String DATE_TIME_PATTERN_MINIMAL = "dd/MM/yyyy HH:mm:ss";
    String HTTP_DEFAULT_PORT = "21";

    /**
     * 5 Menit
     */
    int DELAY_TASK_TO_TNC = 5;
    int DELAY_TASK_TO_BRPL = 10;

    String TNC_BOAT_CLASS_NAME = "TNCBoat";
    String TNC_SPECIES_CLASS_NAME = "TNCSpecies";
    String TNC_DEEPSLOPE_CLASS_NAME = "TNCDeepslope";

    interface MapSettings {
        interface DataType {
            String DATE = "Date";
            String TIME = "Time";
            String TIMESTAMP = "Timestamp";
            String STRING = "String";
            String BOOLEAN = "Boolean";
            String INTEGER = "Integer";
            String DOUBLE = "Double";
            String LONG = "Long";
            String MANY_TO_ONE = "ManyToOne";
            String ONE_TO_ONE = "OneToOne";
            String ONE_TO_MANY = "OneToMany";
        }

        String AT_BRPL = "atBrpl";
        String AT_TNC = "atTnc";
        String AT_BRPL_TYPE = "atBrplType";
        String AT_TNC_TYPE = "atTncType";
    }


}
