package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class ProgramIC implements Runnable{

    private final File location;
    private final String command;
    private final int posicao;
    private static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    public ProgramIC (File location, String command, int posicao){
        this.location = location;
        this.command = command;
        this.posicao = posicao;
    }

    @Override
    public void run() {
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(location);

        if(isWindows){
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("sh", "-c", command);
        }

        Process process;
        try {
            process = builder.start();
            OutputStream outputStream = process.getOutputStream();
            InputStream inputStream = process.getInputStream();
    
            printStream(inputStream, posicao);
    
            boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
            outputStream.flush();
            outputStream.close();
    
            if(!isFinished){
                process.destroyForcibly();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void printStream(InputStream inputStream, int posicao) throws IOException {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))){
            String line;
            while((line = bufferedReader.readLine()) != null){
                System.out.println(posicao + " : " + line);
                if(posicao == 0 && line.contains("Operation Succeeded")){
                    System.out.println("The first slot finished successfully!");
                } else if (posicao == 1 && line.contains("Operation Succeeded")) {
                    System.out.println("The second slot finished successfully!");
                } else if (posicao == 2 && line.contains("Operation Succeeded")) {
                    System.out.println("The third slot finished successfully!");
                } else if (posicao == 3 && line.contains("Operation Succeeded")) {
                    System.out.println("The fourth slot finished successfully!");
                }
            }
        }
    }
    
}
