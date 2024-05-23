package ru.hbb.service;

import lombok.Getter;
import ru.hbb.netty.NettyClient;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class AIService {

    @Getter
    private static AIService instance;
    private FileService fileService;
    private static final String dir = "app/";
    private static String prompt;
    private static String prompt2;
    private static final String[] langs = {"python", "java", "c++", "csharp"};
    private volatile Map<String, Process> processMap;

    public AIService() throws IOException {
        instance = this;
        processMap = new HashMap<>();
        fileService = new FileService();
        init();
    }

    public synchronized void userConnect(String name) throws IOException {
        name = name.toLowerCase().replaceAll(" ", "").replaceAll("\n", "");
        ProcessBuilder processBuilder = new ProcessBuilder("ollama", "run", "qwen:7b");
        processBuilder.directory(new File(dir));
        Process process = processBuilder.start();
        processMap.put(name, process);
    }

    public synchronized void userDisconnect(String name) throws IOException {
        Process process = processMap.get(name);
        if (process == null) {
            return;
        }
        process.destroyForcibly();
        processMap.remove(name);
    }

    public synchronized void predict(String user, String request) throws IOException, InterruptedException {
        Process process = processMap.get(user);
        if (process == null || !process.isAlive()) {
            userConnect(user);
            predict(user, request);
            return;
        }

        OutputStream stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));

        String promptRequest = prompt + "\n" + request;
        writer.write(promptRequest);
        writer.newLine();
        writer.flush();
        writer.close();

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(10000);
                if (process.isAlive()) {
                    process.destroy();
                    userConnect(user);
                    NettyClient.getInstance().sendMessage("Error! So long request!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();

        Scanner scanner = new Scanner(stdout);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            NettyClient.getInstance().sendMessage(line);
        }
    }

//    public String checkCode(String request) throws InterruptedException, IOException {
//
//
//        return result;
//    }

    private void startGenerator(String uuid) throws InterruptedException, IOException {

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
