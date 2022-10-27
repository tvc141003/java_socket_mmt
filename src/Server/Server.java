package Server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

import com.github.kwhat.jnativehook.*;
public class Server {
    private final JFrame frame = new JFrame("Server");
    private JButton toggleServer;
    private JPanel container;
    private JLabel ipShow;
    private boolean serverOpen = false;
    private DataInputStream din = null;
    private DataOutputStream dout = null;
    private ServerSocket ss =null;
    private Socket s = null;
    private Handle optimize = null;
    BufferedReader bufferedReader;
    DataOutputStream dataOutputStream;

    private void createWindow(int w, int h){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(w, h);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.add(container);
    }

    public void sendMessage(String message) throws IOException {
        try {
            dataOutputStream.writeBytes(message);
            dataOutputStream.writeBytes("\n");
            dataOutputStream.writeBytes("End message");
            dataOutputStream.writeBytes("\n");
            //System.out.println(message);
        } catch (IOException e) {
            System.out.println("Error " + e);
        }
    }

    public String getRecvMessage() throws IOException {
        String dataMessage;
        String getLine;
        dataMessage = "";
        getLine = "";
        while ((getLine = bufferedReader.readLine()) != null) {
            if (getLine.compareTo("End message") ==0) break;
            if (dataMessage.compareTo("") == 0) dataMessage += getLine;
            else dataMessage += "\n" + getLine;
        }

        return dataMessage;
    }

    private void getIP(){
        String txt;
        try{
            InetAddress IP= InetAddress.getLocalHost();
            txt = IP.getHostAddress();
        }catch (Exception e){
            txt = "Not found!";
        }
        ipShow.setText("IP: " + txt);
    }

    private void createServer() throws Exception{
        ss = new ServerSocket(1020);
        JOptionPane.showMessageDialog(null, "Successfully!", "Information", JOptionPane.INFORMATION_MESSAGE);
        s = ss.accept();
        bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        dataOutputStream = new DataOutputStream(new DataOutputStream(s.getOutputStream()));
    }

    public void sendCaptureScreen(String fileName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        byte[] byteList = new byte[1024 * 1024];
        int countByte = 0;
        while ((countByte = fileInputStream.read(byteList)) > 0) {
            dataOutputStream.write(byteList, 0, countByte);
        }
        fileInputStream.close();
    }

    private void handleClient(){
        try {
            boolean connect = true;
            Handle handle = new Handle();
            while (connect) {
                String msg = getRecvMessage();
                if (msg.compareTo("1") == 0) {
                    String processList = handle.getProcessList();
                    sendMessage(processList);
                    while (true) {
                        String typeButton = getRecvMessage();
                        String valueButton = getRecvMessage();
                        if (typeButton.compareTo("Exit") == 0) {
                            break;
                        } else if (typeButton.compareTo("Kill") == 0) {
                            String isKill = handle.killProcessList(valueButton);
                            sendMessage(isKill);
                            String processListUpdate = handle.getProcessList();
                            sendMessage(processListUpdate);

                        } else if (typeButton.compareTo("Start") == 0) {
                            String isStart = handle.startApplication(valueButton);
                            sendMessage(isStart);
                            String processListUpdate = handle.getProcessList();
                            sendMessage(processListUpdate);
                        }
                    }
                } else if (msg.compareTo("2") == 0) {
                    String applicationList = handle.getApplication();
                    sendMessage(applicationList);
                    while (true) {
                        String typeButton = getRecvMessage();
                        String valueButton = getRecvMessage();

                        if (typeButton.compareTo("Kill") == 0) {
                            String isKill = handle.killApplication(valueButton);
                            sendMessage(isKill);
                            String request = handle.getApplication();
                            sendMessage(request);
                        } else if (typeButton.compareTo("Start") == 0) {
                            String isStart = handle.startApplication(valueButton);
                            sendMessage(isStart);
                            String request = handle.getApplication();
                            //System.out.println(request);
                            sendMessage(request);
                        } else if (typeButton.compareTo("Exit") == 0) {
                            dataOutputStream.flush();
                            break;
                        }
                    }
                } else if (msg.compareTo("3") == 0) {
                    //System.out.println("3");
                    //dataOutputStream.flush();

                    HandleKeyLogger handleKeyLogger = new HandleKeyLogger();
                    handleKeyLogger.run();
                    boolean isHook = true;
                    boolean isExit = true;
                    while(isExit) {
                        String request = getRecvMessage();
                        //System.out.println(request);
                        switch (request) {
                            case "HOOK": {
                                isHook = false;
                                try {
                                    PrintWriter out = new PrintWriter(HandleKeyLogger.file.toString());
                                    out.print("");
                                    out.close();
                                } catch (FileNotFoundException ex) {
                                    System.out.println("Error " + ex );
                                }
                                break;
                            }

                            case "UNHOOK":{
                                isHook = true;
                                break;
                            }
                            case "PRINT":{
                                if (!isHook) {
                                    try {
                                        String readFile = Files.readString(HandleKeyLogger.file);
                                        if ("".equals(readFile)) readFile = "\0";
                                        sendMessage(readFile);

                                        PrintWriter out = new PrintWriter(HandleKeyLogger.file.toString());
                                        out.print("");
                                        out.close();
                                    } catch (Exception ex) {
                                        System.out.println("Error " + ex);
                                    }
                                } else sendMessage("\0");
                                break;
                            }
                            case "ERASE": {
                                break;
                            }
                            case "EXIT":{
                                isExit = false;

                                //dataOutputStream.flush();
                                break;
                            }
                        }
                    }
                }
                else if (msg.compareTo("4") == 0) {
                    handle.captureScreen();
                    sendCaptureScreen("captureScreen.png");
                } else if (msg.compareTo("5") == 0) {
                    //sendMessage("5");
                    handle.turnOff();
                }
                else if (msg.compareTo("6") == 0) {
                    //sendMessage("6");
                    break;
                }
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, e, "ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void initAction(){
        toggleServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    if (!serverOpen){
                        createServer();
                        Thread thread = new Thread(Server.this::handleClient);
                        thread.start();
                    }
                }catch (Exception k) {
                    JOptionPane.showMessageDialog(null, k, "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    public Server() {
        createWindow(220, 100);
        initAction();
        getIP();
    }

    public void finalize(){
        try {
            bufferedReader.close();
            s.close();
            ss.close();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, e, "ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
