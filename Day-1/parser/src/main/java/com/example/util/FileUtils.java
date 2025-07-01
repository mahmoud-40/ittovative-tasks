package com.example.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static List<File> listFiles(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles((d, name) -> new File(d, name).isFile());
        List<File> fileList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    public static String readFileContent(File file) throws IOException {
        return Files.readString(file.toPath());
    }

    public static boolean deleteFile(File file) {
        return file.delete();
    }
}
