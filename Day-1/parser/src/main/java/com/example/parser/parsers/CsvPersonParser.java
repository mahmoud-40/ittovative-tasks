package com.example.parser.parsers;

import com.example.parser.entity.Person;

public class CsvPersonParser implements PersonParser {
    @Override
    public Person parse(String content) {
        String[] parts = content.trim().split(",");
        if (parts.length < 4) throw new IllegalArgumentException("Invalid CSV format");
        return new Person(parts[0].trim(), Integer.parseInt(parts[1].trim()), parts[2].trim(), parts[3].trim());
    }
}
