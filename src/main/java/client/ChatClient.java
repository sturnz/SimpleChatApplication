package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;

public class ChatClient extends JFrame implements KeyListener {

    private final int       port;
    private final String    address;

    // Connection
    private transient Socket          connectionToServer;
    private transient BufferedReader  fromServerReader;
    private transient PrintWriter     toServerWriter;

    // GUI
    private JTextArea       outputTextArea;
    private JTextField      inputTextField;
    private JScrollPane     windowScrollPane;

    /**
     * Sets the port and asks user for IP-address.
     * @param port  number of port
     */
    public ChatClient(int port) {
        super("Chat");
        this.port   = port;
        address     = JOptionPane.showInputDialog("IP-address");

        if (address != null) {
            receiveMessage();
        }
    }

    private void receiveMessage() {
        try {
            connectionToServer  = new Socket(address, port);
            fromServerReader    = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));
            toServerWriter      = new PrintWriter(new OutputStreamWriter(connectionToServer.getOutputStream()));

            initGui();

            while (true) {
                String message = fromServerReader.readLine();
                outputTextArea.append(message);
                outputTextArea.append("\n");
                windowScrollPane.getVerticalScrollBar().setValue(windowScrollPane.getVerticalScrollBar().getMaximum());

                if (message.equals("exit")) {
                    break;
                }
            }
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Connection to server \"" + address + "\" failed.");
            dispose();
        }
        finally {
            if (connectionToServer != null) {
                try {
                    connectionToServer.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fromServerReader != null) {
                try {
                    fromServerReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (toServerWriter != null) {
                toServerWriter.close();
            }
        }
    }

    /**
     * Sets attributes of the GUI, displaying the chat history and an input field for the user to type a message.
     */
    private void initGui() {
        outputTextArea      = new JTextArea();
        inputTextField      = new JTextField();
        windowScrollPane    = new JScrollPane(outputTextArea);

        outputTextArea.setEditable(false);
        outputTextArea.setBorder(BorderFactory.createTitledBorder("Chat"));
        inputTextField.setBorder(BorderFactory.createTitledBorder("Your message"));
        inputTextField.addKeyListener(this);

        add(windowScrollPane,   BorderLayout.CENTER);
        add(inputTextField,     BorderLayout.SOUTH);

        setVisible(true);
        setLocationRelativeTo(null);
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new ChatClient(2710);
    }

    /**
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() ==  KeyEvent.VK_ENTER) {
            String message = inputTextField.getText();
            if (!message.isEmpty()) {
                toServerWriter.println(message);
                toServerWriter.flush();
                inputTextField.setText("");
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}
