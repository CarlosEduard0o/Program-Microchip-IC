package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Main {

    private static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    
    public static void main(String[] args) throws Exception {
        File Location = new File("Your mplab_ipe location");

        String PicKitSerialNumber = searchPicKit(Location, ".\\ipecmd.exe -T");

        String command = ".\\ipecmd.exe -TS" + PicKitSerialNumber + " -CI to be recorder -M -F" + Location + "Your .hex file";

        programIC(Location, command);
    }

    

    public static String searchPicKit(File whereToRun, String command) throws Exception {
        String PicKitSerialNumber = "";
        ArrayList<String> result = new ArrayList<>();
        ProcessBuilder builder = new ProcessBuilder();

        builder.directory(whereToRun);

        if(isWindows){
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("sh", "-c", command);
        }

        Process process = builder.start();

        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();
        
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))){
            String line;
            while((line = bufferedReader.readLine()) != null){
                result.add(line);
            }
        }

        if (result.size() == 3) {
            int posSubString = result.get(2).indexOf(":");
            PicKitSerialNumber = result.get(2).substring(posSubString + 2);
        }

        boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
        outputStream.flush();
        outputStream.close();

        if(!isFinished){
            process.destroyForcibly();
        }
        return PicKitSerialNumber;
    }



    public static void programIC(File whereToRun, String command) throws Exception {

            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(whereToRun);

            if(isWindows){
                builder.command("cmd.exe", "/c", command);
            } else {
                builder.command("sh", "-c", command);
            }

            Process process = builder.start();

            OutputStream outputStream = process.getOutputStream();
            InputStream inputStream = process.getInputStream();
            InputStream errorStram = process.getErrorStream();

            printStream(inputStream);
            printStream(errorStram);

            boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
            outputStream.flush();
            outputStream.close();

            if(!isFinished){
                process.destroyForcibly();
            }
        }

        

    private static void printStream(InputStream inputStream) throws IOException {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))){
            String line;
            while((line = bufferedReader.readLine()) != null){
                System.out.println(line);
            }
        }
    }
}