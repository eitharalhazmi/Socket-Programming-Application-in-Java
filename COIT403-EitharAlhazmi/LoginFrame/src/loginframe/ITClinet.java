// Eithar Alhazmi
// 2105616
package loginframe;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class ITClinet extends Frame implements ActionListener, Runnable {

    Button sendButton;
    Button browseButton;
    TextField textf;
    TextArea text_area;
    Socket socket;
    PrintWriter pwriter;
    BufferedReader in;

    public ITClinet() {
        super("Client Side Chatting");
        setLayout(new FlowLayout());
        sendButton = new Button("Send");
        browseButton = new Button("Browse");
        sendButton.addActionListener(this);
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle file selection
                FileDialog fileDialog = new FileDialog(new Frame(), "Select File", FileDialog.LOAD);
                fileDialog.setVisible(true);
                String directory = fileDialog.getDirectory();
                String file = fileDialog.getFile();
                if (directory != null && file != null) {
                    File selectedFile = new File(directory + file);
                    textf.setText(file);
                    sendFile(selectedFile);
                }
            }
        });
        textf = new TextField(15);
        text_area = new TextArea(12, 20);
        text_area.setBackground(Color.white);
        addWindowListener(new WindowClosingListener());
        add(textf);
        add(sendButton);
        add(browseButton);
        add(text_area);
        try {
            socket = new Socket(InetAddress.getLocalHost(), 1248);
            pwriter = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread th_read = new Thread(this);
        th_read.setDaemon(true);
        th_read.start();
        setFont(new Font("Arial", Font.BOLD, 20));
        setSize(500, 400);
        setLocation(100, 100);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            // Handle send button click
            String message = textf.getText();
            if (!message.isEmpty()) {
                sendMessage(message);
            }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = in.readLine();
                if (message == null) {
                    break;
                }
                text_area.append("Received from " + message + "\n");
            }
        } catch (SocketException e) {
            // Handle the SocketException gracefully (no error message needed)
        } catch (IOException e) {
            e.printStackTrace(); // Handle other IO exceptions
        } finally {
            close();
        }
    }

    private class WindowClosingListener extends WindowAdapter {

        public void windowClosing(WindowEvent we) {
            close();
            System.exit(0);
        }
    }

    private void sendMessage(String message) {
        // Send a message to the server
        pwriter.println(message);
        textf.setText("");
    }

    private void sendFile(File file) {
        try {
            // Send a file request to the server
            String fileName = file.getName();
            pwriter.println("FILE:" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        // Start the client application
        ITClinet client = new ITClinet();
    }
}
