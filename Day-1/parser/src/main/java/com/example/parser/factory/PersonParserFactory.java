package com.example.parser.factory;

import com.example.parser.parsers.*;

public class PersonParserFactory {
    public static PersonParser createParser(String fileExtension) {
        return switch (fileExtension.toLowerCase()) {
            case "json" -> new JsonPersonParser();
            case "xml" -> new XmlPersonParser();
            case "yaml", "yml" -> new YamlPersonParser();
            case "csv" -> new CsvPersonParser();
            default -> throw new IllegalArgumentException("Unsupported format");
        };
    }
}