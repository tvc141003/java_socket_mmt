package Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private final JFrame frame = new JFrame("Client");
    private JTextField ipInput;
    private JButton connect;
    private JButton processRunning;
    private JButton appRunning;
    private JButton turnOff;
    private JButton capScreen;
    private JButton keyStroke;
    private JButton editRegistry;
    private JButton exit;
    private JPanel mainContainer;
    private DataInputStream din = null;
    private DataOutputStream dout = null;
    private Socket s = null;
    BufferedReader bufferedReader;
    DataOutputStream dataOutputStream;
    private void createWindow(int w, int h){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(w, h);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.add(mainContainer);
    }

    public void sendMess(String message) {
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

    public String getClientRecvMessage() throws IOException {
        String dataMessage;
        String getLine;
        dataMessage = "";

        while ((getLine = bufferedReader.readLine()) != null) {
            if (getLine.compareTo("End message") == 0) break;

            if (dataMessage.compareTo("") == 0) dataMessage += getLine;
            else dataMessage += "\n" + getLine;
        }
        return dataMessage;
    }

    public void getFileScreen(String fileName) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(s.getInputStream());

        byte[] byteList = new byte[1024];
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        int countByte;

        while (true) {
            countByte = dataInputStream.read(byteList);
            fileOutputStream.write(byteList, 0, countByte);
            if (countByte < 1024) break;
        }
        fileOutputStream.close();
    }

    private void initAction(){
        Client client = this;
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    boolean isConnect = createClient();
                    if (isConnect) JOptionPane.showMessageDialog(null, "Connected!", "Information", JOptionPane.INFORMATION_MESSAGE);
                }catch (Exception k){
                    JOptionPane.showMessageDialog(null, k, "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        processRunning.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMess("1");
                    String processList = getClientRecvMessage();
                    //System.out.println(processList);
                    ProcessList_FORM processListGUI = new ProcessList_FORM("Show Process", client, s);
                    processListGUI.setVisible(true);
                    processListGUI.setSize(800, 600);
                    processListGUI.renderProcessArea(processList);

                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Not connect server", "ERROR", JOptionPane.WARNING_MESSAGE);
                }

            }
        });
        appRunning.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMess("2");
                    String processList = getClientRecvMessage();
                    ProcessList_FORM processListGUI = new ProcessList_FORM("Show Process", client, s);
                    processListGUI.setVisible(true);
                    processListGUI.setSize(800, 600);
                    processListGUI.renderProcessArea(processList);

                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Not connect server", "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        turnOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMess("5");
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(null, "Not connect server", "ERROR", JOptionPane.WARNING_MESSAGE);
                }

            }
        });
        capScreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMess("4");
                    getFileScreen("captureScreen.png");
                    JOptionPane.showMessageDialog(frame, "Successfully!", "Capture Screen", JOptionPane.INFORMATION_MESSAGE );
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Not connect server", "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        keyStroke.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMess("3");
                    //dataOutputStream.flush();
                    KeyLogger keyLogger = new KeyLogger(client);
                    keyLogger.setTitle("Key Logger");
                    keyLogger.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    keyLogger.setVisible(true);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Not connect server", "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }

        });
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.dispose();
                try {
                    sendMess("6");
                    dataOutputStream.close();
                    s.close();
                }catch (Exception k){
                    JOptionPane.showMessageDialog(null, "Not connect server", "ERROR", JOptionPane.WARNING_MESSAGE);
                }
                System.exit(0);
            }
        });
    }

    private boolean createClient() throws Exception{
        if (ipInput.getText().compareTo(InetAddress.getLocalHost().getHostAddress()) == 0){
            InetAddress IP = InetAddress.getByName(ipInput.getText());
            s = new Socket(IP, 1020);
            bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            dataOutputStream = new DataOutputStream(new DataOutputStream(s.getOutputStream()));
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Can't Connect", "ERROR", JOptionPane.WARNING_MESSAGE);
            return false;
        }

    }

    public Client() {
        createWindow(700, 400);
        initAction();
    }

    public void finalize(){
        try {
            s.close();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, e, "ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
