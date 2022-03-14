package com.example.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Helper class for reading local files
 */
public class FileReader {
    /**
     * Read file content to string
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        return readFileWithEncoding(filePath, StandardCharsets.UTF_8);
    }

    /**
     * read file content to string by specifying encoding
     *
     * @param filePath
     * @param charset
     * @return
     */
    public static String readFileWithEncoding(String filePath, Charset charset) {
        final int BUFFER_SIZE = 65536;
        char[] buffer = new char[BUFFER_SIZE];
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath),charset);) {
            int howManyRead = -1;
            while ( (howManyRead = br.read(buffer)) != -1) {
                for (int j = 0; j < howManyRead; j++) {
                    sb.append(buffer[j]);
                }
            }
        } catch(IOException e){
            e.printStackTrace();
            throw new IllegalArgumentException("file path: " + new File(filePath).getAbsolutePath());
        }
        return sb.toString();
    }
}
