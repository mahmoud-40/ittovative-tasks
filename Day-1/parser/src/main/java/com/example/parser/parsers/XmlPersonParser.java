package com.example.parser.parsers;

import com.example.parser.entity.Person;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlPersonParser implements PersonParser {
    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public Person parse(String content) throws Exception {
        return xmlMapper.readValue(content, Person.class);
    }
}