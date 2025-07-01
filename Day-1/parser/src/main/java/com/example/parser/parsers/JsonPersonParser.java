package com.example.parser.parsers;

import com.example.parser.entity.Person;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonPersonParser implements PersonParser {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Person parse(String content) throws Exception {
        return objectMapper.readValue(content, Person.class);
    }
}