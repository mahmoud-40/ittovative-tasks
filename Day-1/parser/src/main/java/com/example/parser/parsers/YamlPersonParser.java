package com.example.parser.parsers;

import com.example.parser.entity.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlPersonParser implements PersonParser {
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @Override
    public Person parse(String content) throws Exception {
        return yamlMapper.readValue(content, Person.class);
    }
}
