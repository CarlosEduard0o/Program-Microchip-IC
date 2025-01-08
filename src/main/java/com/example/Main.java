package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Main {

    private static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    
    public static void main(String[] args) throws Exception {
        File Location = new File("Your mplab_ipe location");

        ArrayList<String> PicKitSerialNumber = searchPicKit(Location, ".\\ipecmd.exe -T");

        for(int i = 0; i < PicKitSerialNumber.size(); i++){
            if (PicKitSerialNumber.get(i) != "") {
                String command = ".\\ipecmd.exe -TS" + PicKitSerialNumber.get(i) + " -P32MM0064GPL036 -M -F" + Location + "Your .hex file";
                Thread thread = new Thread(new ProgramIC(Location, command, i));
                thread.start();
            }
        }
    }

    public static ArrayList<String> searchPicKit(File whereToRun, String command) throws Exception {
        ArrayList<String> PicKitSerialNumber = new ArrayList<>();
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

        int posSubString = 0;
        
        switch (result.size()) {
            case 3:
                posSubString = result.get(2).indexOf(":");
                PicKitSerialNumber.add(result.get(2).substring(posSubString + 2));
                PicKitSerialNumber.add("");
                PicKitSerialNumber.add("");
                PicKitSerialNumber.add("");
                break;
            case 4:
                posSubString = result.get(2).indexOf(":");
                PicKitSerialNumber.add(result.get(2).substring(posSubString + 2));
                posSubString = result.get(3).indexOf(":");
                PicKitSerialNumber.add(result.get(3).substring(posSubString + 2));
                PicKitSerialNumber.add("");
                PicKitSerialNumber.add("");
                break;
            case 5:
                posSubString = result.get(2).indexOf(":");
                PicKitSerialNumber.add(result.get(2).substring(posSubString + 2));
                posSubString = result.get(3).indexOf(":");
                PicKitSerialNumber.add(result.get(3).substring(posSubString + 2));
                posSubString = result.get(4).indexOf(":");
                PicKitSerialNumber.add(result.get(4).substring(posSubString + 2));
                PicKitSerialNumber.add("");
                break;
            case 6:
                posSubString = result.get(2).indexOf(":");
                PicKitSerialNumber.add(result.get(2).substring(posSubString + 2));
                posSubString = result.get(3).indexOf(":");
                PicKitSerialNumber.add(result.get(3).substring(posSubString + 2));
                posSubString = result.get(4).indexOf(":");
                PicKitSerialNumber.add(result.get(4).substring(posSubString + 2));
                posSubString = result.get(5).indexOf(":");
                PicKitSerialNumber.add(result.get(5).substring(posSubString + 2));
                break;      
            default:
                throw new IllegalArgumentException("Invalid array size!");
        }

        boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
        outputStream.flush();
        outputStream.close();

        if(!isFinished){
            process.destroyForcibly();
        }
        return PicKitSerialNumber;
    }
}