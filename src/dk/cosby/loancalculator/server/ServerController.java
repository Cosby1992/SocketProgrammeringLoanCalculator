package dk.cosby.loancalculator.server;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class ServerController{

    private final int PORT = 8000;
    public Button btn_start_server;
    public TextArea ta_server_info;

    public void initialize(){
        ta_server_info.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                ta_server_info.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
                //use Double.MIN_VALUE to scroll to the top
            }
        });
    }

    public void startServer(ActionEvent actionEvent) {

        try {

            ta_server_info.appendText("\nServer starting on port " + PORT);
            ServerSocket serverSocket = new ServerSocket(PORT);
            ta_server_info.appendText("\nPort open, waiting for connection");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            Socket socket = serverSocket.accept();
                            ta_server_info.appendText("\nConnection to host established");
                            ta_server_info.appendText("\nTimestamp: " + new Date());
                            ta_server_info.appendText("\nClient info: " + socket.getInetAddress().toString() + ":" + socket.getPort());


                            ta_server_info.appendText("\nWaiting for Client to send request");
                            ClientHandler clientHandler = new ClientHandler(socket, ta_server_info);

                            Thread thread1 = new Thread(clientHandler);
                            thread1.start();
                        }

                    }  catch (IOException e) {
                        ta_server_info.appendText("\nFailed to connect to client.");
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        } catch (java.net.BindException e){
            ta_server_info.appendText("\nServer is already running");
        } catch (IOException e) {
            ta_server_info.appendText("\nFailed to create ServerSocket");
            e.printStackTrace();
        }

    }

}
