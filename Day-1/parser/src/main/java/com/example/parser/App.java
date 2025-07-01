package com.example.parser;

import com.example.parser.factory.PersonParserFactory;
import com.example.parser.parsers.PersonParser;
import com.example.util.Constants;
import com.example.util.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import com.example.parser.entity.Person;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printMenu();
            String option = scanner.nextLine();
            running = handleOption(option);
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Search and parse files in input_files");
        System.out.println("2. Exit");
        System.out.print("Choose an option: ");
    }

    private static boolean handleOption(String option) {
        switch (option) {
            case "1" -> processFiles();
            case "2" -> {
                System.out.println("Exiting application.");
                return false;
            }
            default -> System.out.println("Invalid option. Try again.");
        }
        return true;
    }

    private static void processFiles() {
        List<File> files = FileUtils.listFiles(Constants.INPUT_DIR);
        if (files.isEmpty()) {
            System.out.println("No files found in " + Constants.INPUT_DIR);
            return;
        }
        File file = files.get(0);
        String fileName = file.getName();
        String ext = getFileExtension(fileName);
        if (!Constants.SUPPORTED_FORMATS.contains(ext)) {
            System.out.println("Unsupported file format: " + ext);
            return;
        }
        try {
            String content = FileUtils.readFileContent(file);
            PersonParser parser = PersonParserFactory.createParser(ext);
            Person person = parser.parse(content);
            System.out.println("Parsed entity: " + person);
            if (FileUtils.deleteFile(file)) {
                System.out.println("File deleted: " + fileName);
            } else {
                System.out.println("Failed to delete file: " + fileName);
            }
        } catch (Exception e) {
            System.out.println("Error parsing file: " + e.getMessage());
        }
    }

    private static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return (lastDot == -1) ? "" : fileName.substring(lastDot + 1).toLowerCase();
    }
}
