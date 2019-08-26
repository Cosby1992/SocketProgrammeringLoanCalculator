package dk.cosby.loancalculator.client;

import dk.cosby.loancalculator.server.BmiCalc;
import dk.cosby.loancalculator.server.LoanCalc;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

public class BmiCalcGui {

    private final int PORT = 8000;
    private final String HOST = "localhost";

    public TextField tf_height;
    public TextField tf_weight;
    public Button btn_loan_calculator;
    public TextArea ta_client_info;
    public Button btn_connect_to_server;

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public void initialize() {

        ta_client_info.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                ta_client_info.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
                //use Double.MIN_VALUE to scroll to the top
            }
        });

        tf_height.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if (!newValue.matches("\\d*")) {
                    tf_height.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        tf_weight.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if (!newValue.matches("\\d*")) {
                    tf_weight.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }



    @SuppressWarnings("Duplicates")
    public void connectToServer(ActionEvent actionEvent) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ta_client_info.appendText("Attempting to connect to server on " + HOST + ":" + PORT);
                    socket = new Socket(HOST, PORT);
                    ta_client_info.appendText("\nConnection to server succeeded");
                    ta_client_info.appendText("\nTimestamp: " + new Date());
                    ta_client_info.appendText("\nLocal info: " + socket.getInetAddress().getAddress() + ":" + socket.getLocalPort());
                    ta_client_info.appendText("\nReady to send request...");

                    oos = new ObjectOutputStream(socket.getOutputStream());
                    ois = new ObjectInputStream(socket.getInputStream());

                    btn_connect_to_server.setDisable(true);

                } catch (IOException e) {
                    ta_client_info.appendText("\nFailed to connect to server");
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


    public void sendRequest(ActionEvent actionEvent) {
        if(!tf_height.getText().isEmpty()
                && !tf_weight.getText().isEmpty()){

            ta_client_info.appendText("\nSending request to server: ");
            ta_client_info.appendText("\nLoan height: " + tf_height.getText());
            ta_client_info.appendText("\nLoan weight: " + tf_weight.getText());

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Bmi bmi = new Bmi(Integer.valueOf(tf_height.getText()),
                            Integer.valueOf(tf_weight.getText()));

                    try {

                        System.out.println("Writing object to server");
                        oos.writeObject(bmi);
                        oos.flush();

                        System.out.println("Recieving answer from server");
                        BmiCalc bmiCalc = (BmiCalc) ois.readObject();

                        ta_client_info.appendText("\nRequest succesfully answered.");
                        ta_client_info.appendText("\nDin bmi er: " + bmiCalc.getBmi());
                        ta_client_info.appendText("\nDet gør dig: " + bmiCalc.getStatus());

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        } else {
            ta_client_info.appendText("\nTextfield must contain numbers");
            ta_client_info.appendText("\nFill in the blanks and try again.");
        }
    }

    public void switchScene(ActionEvent actionEvent) throws IOException {
        Stage stage;
        Parent root;

        stage = (Stage) btn_loan_calculator.getScene().getWindow();
        root = FXMLLoader.load(getClass().getClassLoader().getResource("dk/cosby/loancalculator/client/client_gui.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
