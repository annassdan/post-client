package com.project.ifish.postclient.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ifish.postclient.PostClient;
import com.project.ifish.postclient.PostclientApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@SuppressWarnings("unused")
@Configuration
public class PostClientTranslator implements PostClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public <T> Object httpRequestPostForObject(String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(url, request, responseType);
    }

    public <T> LinkedHashMap castToLinkedHashMap(Class<T> className, T data) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return ((LinkedHashMap) (mapper.readValue(mapper.writeValueAsBytes(data), Object.class)));
        } catch (IOException e) {
            return null;
        }
    }

    public <T> T castToObject(LinkedHashMap translatedObject, Class<T> className) {
        T clazz = null;
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String s = mapper.writeValueAsString(translatedObject);
            return mapper.readValue(s, className);
        } catch (IOException e) {
            return null;
        }
    }

    private String convertDateToPattern(Date date, String patternTarget) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(patternTarget);
            return formatter.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    private Object searchDefaultValue(String dataType) {
        switch (dataType) {
            case MapSettings.DataType.MANY_TO_ONE:
                LinkedHashMap map = new LinkedHashMap();
                map.put("uuid", "");
                return map;
            case MapSettings.DataType.ONE_TO_ONE:
            case MapSettings.DataType.ONE_TO_MANY:
            case MapSettings.DataType.DATE:
            case MapSettings.DataType.TIME:
            case MapSettings.DataType.TIMESTAMP:
                return null;
            case MapSettings.DataType.INTEGER:
            case MapSettings.DataType.DOUBLE:
            case MapSettings.DataType.LONG:
                return 0;
            default:
                return "";
        }
    }

    private Object Numbering(Object v, String type) {
        try {
            if (type.equals(MapSettings.DataType.INTEGER))
                return Integer.parseInt(String.valueOf(v));
            else if (type.equals(MapSettings.DataType.DOUBLE))
                return Double.parseDouble(String.valueOf(v));
            else if (type.equals(MapSettings.DataType.LONG))
                return Long.parseLong(String.valueOf(v));
        } catch (Exception e) {
            return 0;
        }

        return 0;
    }


    private boolean BooleanNumber(Object v, String type) {
        try {
            if (type.equals(MapSettings.DataType.INTEGER))
                return Integer.parseInt(String.valueOf(v)) == 1;
            else if (type.equals(MapSettings.DataType.DOUBLE))
                return Double.parseDouble(String.valueOf(v)) == 1;
            else if (type.equals(MapSettings.DataType.LONG))
                return Long.parseLong(String.valueOf(v)) == 1;
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public LinkedHashMap translate(List<LinkedHashMap> setting, LinkedHashMap object) {
        return translate(setting, object, null, null);
    }


    public LinkedHashMap translate(List<LinkedHashMap> setting,
                                   LinkedHashMap object,
                                   LinkedHashMap<String, List<?>> assocs,
                                   LinkedHashMap<String, LinkedHashMap<String, Class>> assocsType) {

        @SuppressWarnings("unchecked")
        LinkedHashMap translatedObject = new LinkedHashMap();

        setting.forEach(map -> {
            String column = String.valueOf(map.get(MapSettings.AT_BRPL));
//            if (column.equals("dataBoat")) {
//                logger.info("aa");
//            }

            String brplType = String.valueOf(map.get(MapSettings.AT_BRPL_TYPE));
            boolean relation = (brplType.equals(MapSettings.DataType.ONE_TO_MANY) || brplType.equals(MapSettings.DataType.ONE_TO_ONE) ||
                    brplType.equals(MapSettings.DataType.MANY_TO_ONE));
            boolean isNullInTnc = map.get(MapSettings.AT_TNC) == null;

            Object value = (isNullInTnc) ? null : object.get(map.get(MapSettings.AT_TNC));
            boolean isNull = value == null;

            Object data = null;
            if (String.valueOf(map.get(MapSettings.AT_TNC_TYPE))
                    .equals(String.valueOf(map.get(MapSettings.AT_BRPL_TYPE)))) {
                if (map.get(MapSettings.AT_TNC_TYPE).equals(MapSettings.DataType.DATE)) {
                    data = (!isNull) ? value : "";
                } else if (map.get(MapSettings.AT_TNC_TYPE).equals(MapSettings.DataType.TIMESTAMP)) {
                    try {
                        data = (!isNull) ? convertDateToPattern(
                                new SimpleDateFormat(DATE_TIME_PATTERN_MINIMAL).parse(String.valueOf(value))
                                , DATE_TIME_PATTERN_MINIMAL) : "";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    data = (isNull) ? searchDefaultValue(String.valueOf(map.get(MapSettings.AT_BRPL_TYPE))) : value;
                }
            } else { // jika tipe datanya berbeda


                if (isNullInTnc && !relation) {
                    data = searchDefaultValue(String.valueOf(map.get(MapSettings.AT_BRPL_TYPE)));
                } else {
                    if (isNull && !relation) {
                        data = searchDefaultValue(String.valueOf(map.get(MapSettings.AT_BRPL_TYPE)));
                    } else {
                        String tncType = String.valueOf(map.get(MapSettings.AT_TNC_TYPE));

                        switch (brplType) {
                            case MapSettings.DataType.MANY_TO_ONE:
                                LinkedHashMap c = new LinkedHashMap();
                                c.put("uuid", String.valueOf(value));

                                data = c;
                                break;
                            case MapSettings.DataType.ONE_TO_MANY:
//                                if (map.containsValue("classRef")) {
                                String cNameRef = String.valueOf(map.get("classRef"));
                                LinkedHashMap<String, Class> types = (LinkedHashMap<String, Class>) assocsType.get(cNameRef);
                                Class sourceClass = types.get("sourceClass");
                                Class destinationClass = types.get("destinationClass");
                                List assocValues = assocs.get(cNameRef); // ambil data asosiasinya

                                LinkedHashMap settingAssoc = PostclientApplication.getSyncronizingSetting(PostclientApplication.appConfig, cNameRef);
                                List<LinkedHashMap> columnSetting = (List<LinkedHashMap>) settingAssoc.get("mapOfColumns");

                                List translatedAssoc = new ArrayList();
                                assocValues.forEach(o -> {

                                    translatedAssoc.add(
                                            translateToDestinationClass(sourceClass,
                                                    destinationClass, o,
                                                    columnSetting)
                                    );
                                });

                                data = translatedAssoc;
//                                }
                                break;
                            case MapSettings.DataType.DATE:
                            case MapSettings.DataType.TIME:
                            case MapSettings.DataType.TIMESTAMP:
                                data = null;
                                break;
                            case MapSettings.DataType.STRING:
                                data = String.valueOf(value);
                                break;
                            case MapSettings.DataType.BOOLEAN:
                                data = BooleanNumber(value, tncType);
                                break;
                            default:
                                data = Numbering(value, brplType);
                                break;
                        }
                    }
                }


            }


            translatedObject.put(column, data);
        });

        return translatedObject;
    }

    public <S, D> D translateToDestinationClass(
            Class<S> sourceClassName,
            Class<D> destinationClassName,
            S sourceData,
            List<LinkedHashMap> setting
    ) {
        return translateToDestinationClass(sourceClassName, destinationClassName, sourceData, setting, null, null);
    }


    public <S, D> D translateToDestinationClass(
            Class<S> sourceClassName,
            Class<D> destinationClassName,
            S sourceData,
            List<LinkedHashMap> setting,
            LinkedHashMap<String, List<?>> assocs,
            LinkedHashMap<String, LinkedHashMap<String, Class>> assocsType
    ) {
        try {
            if (sourceData == null) return null;
            LinkedHashMap map = castToLinkedHashMap(sourceClassName, sourceData);
            if (map == null) return null;
            LinkedHashMap o = translate(setting, map, assocs, assocsType);
            if (o == null) return null;
            return castToObject(o, destinationClassName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
