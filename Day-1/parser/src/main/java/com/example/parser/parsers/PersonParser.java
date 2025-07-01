package com.example.parser.parsers;

import com.example.parser.entity.Person;

public interface PersonParser {
    Person parse(String content) throws Exception;
}