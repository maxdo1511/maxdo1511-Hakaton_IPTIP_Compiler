package ru.hbb.service;

import lombok.Getter;

import java.io.File;
import java.io.IOException;

public class AIService {

    @Getter
    private static AIService instance;
    private FileService fileService;
    private static final String dir = "app/";
    private static String prompt;
    private static String prompt2;
    private static final String[] langs = {"python", "java", "c++", "csharp"};

    public AIService() throws IOException {
        instance = this;
        fileService = new FileService();
        init();
    }

    public String predict(String request) throws InterruptedException, IOException {
        String uuid = fileService.createNewInputFile(dir + "input/", prompt, request);
        startGenerator(uuid);
        String result = fileService.readFile(dir + "output/", uuid);
        result = removeTexts(result);
        fileService.deleteFile( dir + "input/", uuid);
        fileService.deleteFile( dir + "output/", uuid);
        String isCode = checkCode(result);
        System.out.println(result);
        System.out.println("==========================================================");
        System.out.println(isCode);
        return result;
    }

    public String checkCode(String request) throws InterruptedException, IOException {
        String uuid = fileService.createNewInputFile(dir + "input/", request);
        startGenerator(uuid);
        String result = fileService.readFile(dir + "output/", uuid);
        fileService.deleteFile( dir + "input/", uuid);
        fileService.deleteFile( dir + "output/", uuid);
        return result;
    }

    private void startGenerator(String uuid) throws InterruptedException, IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("ollama", "run", "qwen:7b");
        processBuilder.directory(new File(dir));
        processBuilder.redirectInput(new File(dir + "input/" + uuid + ".txt"));
        processBuilder.redirectOutput(new File(dir + "output/" + uuid + ".txt"));
        Process process = processBuilder.start();

        new Thread(() -> {
            try {
                Thread.sleep(10000);
                process.destroy();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        int exitCode = process.waitFor();
        System.out.println("Exit code: " + exitCode);
    }

    private String removeTexts(String data) {
        for (String lang : langs) {
            data = data.replace(lang, "");
        }
        String[] lines = data.split("```");
        if (lines.length > 1) {
            return lines[1];
        }
        return data;
    }

    private void init() throws IOException {
        String data = fileService.readFile(dir, "promt");
        prompt = data;
        data = fileService.readFile(dir, "promt2");
        prompt2 = data;
    }

}
