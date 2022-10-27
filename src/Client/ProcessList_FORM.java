package Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class ProcessList_FORM extends JFrame {
    private JPanel mainPanel;
    private JTextArea renderProcessArea;
    private JTextField inputIdProcessField;
    private JButton exitButton;
    private JButton killButton;
    private JButton startButton;
    private JPanel subPanel;
    private JScrollPane scrollBar;

    public ProcessList_FORM(String title, Client client, Socket serverConnect) throws Exception {

        super(title);
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        ProcessList_FORM _this = this;
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nameProcess = inputIdProcessField.getText();
                try {
                    client.sendMess("Start");
                    client.sendMess(nameProcess);
                    String request = client.getClientRecvMessage();
                    JOptionPane.showMessageDialog(_this, request, "Start", JOptionPane.INFORMATION_MESSAGE);
                    String processListUpdate = client.getClientRecvMessage();
                    _this.renderProcessArea(processListUpdate);
                } catch (Exception ioe) {
                    System.out.println("Error " + e);
                }

            }
        });
        killButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idKill = inputIdProcessField.getText();
                try {
                    client.sendMess("Kill");
                    client.sendMess(idKill);
                    String request = client.getClientRecvMessage();
                    JOptionPane.showMessageDialog(_this, request, "Kill", JOptionPane.INFORMATION_MESSAGE);
                    String processListUpdate = client.getClientRecvMessage();
                    _this.renderProcessArea(processListUpdate);
                } catch (Exception ioe) {
                    System.out.println("Error " + ioe);
                }
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.sendMess("Exit");
                    client.sendMess("Exit");
                    _this.setVisible(false);
                } catch (Exception ioe) {
                    System.out.println("Error " + ioe);
                }
            }
        });
    }
    public void renderProcessArea(String data) {
        renderProcessArea.setText(data);
        renderProcessArea.setBounds(0, 0, 200, 300);
        renderProcessArea.enableInputMethods(true);
    }
}
