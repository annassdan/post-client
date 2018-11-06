package com.project.ifish.postclient.configurations;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.project.ifish.postclient.PostClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@SuppressWarnings("unused")
public class CustomTimeSerializer extends JsonSerializer<Date> {


    @Override
    public void serialize(Date o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        SimpleDateFormat formatter = new SimpleDateFormat(PostClient.TIME_PATTERN);
        String formattedDate = formatter.format(o);
        jsonGenerator.writeString(formattedDate);
    }


}
