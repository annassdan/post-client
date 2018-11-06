package com.project.ifish.postclient.configurations;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.project.ifish.postclient.PostClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@SuppressWarnings("unused")
public class CustomDateTimeSerializer extends JsonSerializer<Date> {


    @Override
    public void serialize(Date date, com.fasterxml.jackson.core.JsonGenerator jsonGenerator, com.fasterxml.jackson.databind.SerializerProvider serializerProvider) throws IOException, com.fasterxml.jackson.core.JsonProcessingException {
        SimpleDateFormat formatter = new SimpleDateFormat(PostClient.DATE_TIME_PATTERN);
        String formattedDate = formatter.format(date);
        jsonGenerator.writeString(formattedDate);

    }
}
