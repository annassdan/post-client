package com.project.ifish.postclient.utils;

import com.project.ifish.postclient.PostClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@SuppressWarnings("unused")
@Configuration
public class PostClientTranslator implements PostClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

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

        LinkedHashMap translatedObject = new LinkedHashMap();

        setting.forEach(map -> {
            String column = String.valueOf(map.get(MapSettings.AT_BRPL));
//            if (column.equals("codrsStartDate")) {
////                logger.info("aa");
////            }

            boolean isNullInTnc = map.get(MapSettings.AT_TNC) == null;

            Object value = (isNullInTnc) ? null : object.get(map.get(MapSettings.AT_TNC));
            boolean isNull = value == null;


            Object data = null;
            if (String.valueOf(map.get(MapSettings.AT_TNC_TYPE))
                    .equals(String.valueOf(map.get(MapSettings.AT_BRPL_TYPE))))
            {
                if (map.get(MapSettings.AT_TNC_TYPE).equals(MapSettings.DataType.DATE)) {
                    data = (!isNull) ? convertDateToPattern((Date) value, DATE_PATTERN) : "";
                } else if (map.get(MapSettings.AT_TNC_TYPE).equals(MapSettings.DataType.TIMESTAMP)) {
                    try {
                        data = (!isNull) ? convertDateToPattern(
                                new SimpleDateFormat(DATE_TIME_PATTERN_MINIMAL).parse(String.valueOf(value))
                                , DATE_TIME_PATTERN_MINIMAL) : "";
//                        data = (!isNull) ? convertDateToPattern((Date) value, DATE_PATTERN) : "";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    data = (isNull) ? searchDefaultValue(String.valueOf(map.get(MapSettings.AT_BRPL_TYPE))) : value;
                }
            } else { // jika tipe datanya berbeda
                if (isNullInTnc) {
                    data = searchDefaultValue(String.valueOf(map.get(MapSettings.AT_BRPL_TYPE)));
                } else {
                    if (isNull) {
                        data = searchDefaultValue(String.valueOf(map.get(MapSettings.AT_BRPL_TYPE)));
                    } else {
                        String brplType = String.valueOf(map.get(MapSettings.AT_BRPL_TYPE));
                        String tncType = String.valueOf(map.get(MapSettings.AT_TNC_TYPE));

                        switch (brplType) {
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

}
