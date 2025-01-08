import org.junit.Test;

import com.example.Main;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class MainTest {

    @Test
    public void searchForPicKitTest() {
        File Location = new File("Your mplab_ipe location");
        ArrayList<String> PicKitSerialNumber = new ArrayList<>();
        try {
            PicKitSerialNumber = Main.searchPicKit(Location, ".\\ipecmd.exe -T");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("Your PIC KIT Serial Number", PicKitSerialNumber.get(0));
    }

    @Test
    public void programICTest(){

        File Location = new File("Your mplab_ipe location");
        ArrayList<String> PicKitSerialNumber = new ArrayList<>();
        String result = "";

        try {
            PicKitSerialNumber = Main.searchPicKit(Location, ".\\ipecmd.exe -T");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String command = ".\\ipecmd.exe -TS" + PicKitSerialNumber.get(0) + " -P32MM0064GPL036 -M -F" + Location + "Your .hex file";

        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(Location);

        builder.command("cmd.exe", "/c", command);

        Process process;
        try {
            process = builder.start();
            OutputStream outputStream = process.getOutputStream();
            InputStream inputStream = process.getInputStream();

            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))){
                String line;
                while((line = bufferedReader.readLine()) != null){
                    System.out.println(0 + " : " + line);
                    if(line.contains("Operation Succeeded")){
                        result = "The first slot finished successfully!";
                    } 
                }
            }

            boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
            outputStream.flush();
            outputStream.close();
    
            if(!isFinished){
                process.destroyForcibly();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("The first slot finished successfully!", result);
    }

    @Test
    public void programICFailedTest(){

        File Location = new File("Your mplab_ipe location");
        String result = "";

        String command = ".\\ipecmd.exe -TS" + "123456" + " -P32MM0064GPL036 -M -F" + Location + "Your .hex file";

        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(Location);

        builder.command("cmd.exe", "/c", command);

        Process process;
        try {
            process = builder.start();
            OutputStream outputStream = process.getOutputStream();
            InputStream inputStream = process.getInputStream();

            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))){
                String line;
                while((line = bufferedReader.readLine()) != null){
                    System.out.println(0 + " : " + line);
                    if(line.contains("Invalid Serial No.")){
                        result = "Failed!";
                    } 
                }
            }

            boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
            outputStream.flush();
            outputStream.close();
    
            if(!isFinished){
                process.destroyForcibly();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Failed!", result);
    }


}
