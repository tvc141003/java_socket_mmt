package Server;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class Handle {
    public String getProcessList() throws IOException {

        String command = "powershell.exe gps | select ProcessName,Id,Description";

        Process cmdProcess = Runtime.getRuntime().exec(command);
        cmdProcess.getOutputStream().close();

        String processList = new String();
        processList = "";
        String getLine="";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
        while ((getLine = bufferedReader.readLine()) != null) {
            if (processList.compareTo("") == 0) {
                processList += getLine;
            } else processList += "\n" + getLine;
        }
        bufferedReader.close();
        //System.out.println(processList);
        return processList;
    }

    public String killProcessList(String id) throws IOException {
        String processList = getProcessList();
        int index = processList.indexOf(id);
        if (id.isEmpty() || index == -1) return "Fail";
        String command = "powershell.exe gps | taskkill /F /PID " + id;
        String isSuccess = "Success";

        try {
            Process killProcessPowerShell = Runtime.getRuntime().exec(command);
            killProcessPowerShell.getOutputStream().close();
            isSuccess = "Success";
        } catch (IOException e) {
            System.out.println("Error " + e);
            isSuccess = "Fail";
        } finally {
            return isSuccess;
        }
    }

    public String getApplication() throws IOException {

        String command = "powershell.exe gps | where {$_.MainWindowTitle } | select ProcessName,Id,Description";

        Process cmdProcess = Runtime.getRuntime().exec(command);
        cmdProcess.getOutputStream().close();

        String applicationList = new String();
        applicationList = "";
        String getLine="";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
        while ((getLine = bufferedReader.readLine()) != null) {
            if (applicationList.compareTo("") == 0) {
                applicationList += getLine;
            } else applicationList += "\n" + getLine;
        }
        bufferedReader.close();
        return applicationList;
    }

    public String startApplication(String nameApplication) throws IOException {
        String command = "powershell.exe gps | Start-Process " + nameApplication;
        String isSuccess = "Success";

        try {
            Process startApplication = Runtime.getRuntime().exec(command);
            isSuccess = "Success";
        } catch (IOException e) {
            System.out.println("Error " + e);
            isSuccess = "Fail";
        } finally {
            return isSuccess;
        }
    }

    public String killApplication(String id) throws IOException {
        String applicationList = getApplication();
        int index = applicationList.indexOf(id);
        if (id.isEmpty() || index == -1) return "Fail";
        String command = "powershell.exe gps | taskkill /F /PID " + id;
        String isSuccess = "Success";

        try {
            Process killProcessPowerShell = Runtime.getRuntime().exec(command);
            killProcessPowerShell.getOutputStream().close();
            isSuccess = "Success";
        } catch (IOException e) {
            System.out.println("Error " + e);
            isSuccess = "Fail";
        } finally {
            return isSuccess;
        }
    }

    public void captureScreen() throws IOException {
        try {
            BufferedImage bufferedImage = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(bufferedImage, "png", new File("captureScreen.png"));
            //bufferedImage.flush();
        } catch (AWTException e) {
            System.out.println("Error " + e);
        }
    }

    public void turnOff() throws IOException {
        String command = "powershell.exe shutdown -s -t 1";

        Process cmdProcess = Runtime.getRuntime().exec(command);
        cmdProcess.getOutputStream().close();
    }
}
