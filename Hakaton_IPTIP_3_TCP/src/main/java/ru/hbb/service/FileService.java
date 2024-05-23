package ru.hbb.service;

import java.util.UUID;

public class FileService {

    /**
     * Читает содержимое файла по уникальному идентификатору
     * @param uuid уникальный идентификатор присваивается в методе createNewInputFile
     * @return строку
     */
    public String readFile(String path, String uuid) {
        try {
            String filepath = path + uuid + ".txt";
            return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filepath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createNewInputFile(String path, String data) {
        String uuid = UUID.randomUUID().toString();
        String filepath = path + uuid + ".txt";
        saveToFile(filepath, data);
        return uuid;
    }

    public String createNewInputFile(String path, String prompt, String data) {
        String uuid = UUID.randomUUID().toString();
        String filepath = path + uuid + ".txt";
        saveToFile(filepath, prompt + data);
        return uuid;
    }

    public void deleteFile(String path, String uuid) {
        try {
            String filepath = path + uuid + ".txt";
            java.nio.file.Files.delete(java.nio.file.Paths.get(filepath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToFile(String filename, String data) {
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(filename), data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
