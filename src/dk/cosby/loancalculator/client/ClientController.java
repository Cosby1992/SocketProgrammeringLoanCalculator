package dk.cosby.loancalculator.client;

import dk.cosby.loancalculator.server.LoanCalc;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

public class ClientController {

    private final int PORT = 8000;
    private final String HOST = "localhost";
    public Button btn_connect_to_server;

    private Socket socket;

    public TextArea ta_client_info;
    public TextField tf_loan_amount;
    public TextField tf_loan_interest;
    public TextField tf_loan_duration;

    //takes care of textfield inputs using change listener
    @SuppressWarnings("Duplicates")
    public void initialize(){

        ta_client_info.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                ta_client_info.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
                //use Double.MIN_VALUE to scroll to the top
            }
        });

        tf_loan_amount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if (!newValue.matches("\\d*")) {
                    tf_loan_amount.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        tf_loan_interest.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if (!newValue.matches("\\d*")) {
                    tf_loan_interest.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        tf_loan_duration.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if (!newValue.matches("\\d*")) {
                    tf_loan_duration.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

    }

    public void connectToServer(ActionEvent actionEvent) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ta_client_info.appendText("Attempting to connect to server on " + HOST + ":" + PORT);
                    socket = new Socket(HOST, PORT);
                    ta_client_info.appendText("\nConnection to server succeeded");
                    ta_client_info.appendText("\nTimestamp: " + new Date());
                    ta_client_info.appendText("\nLocal info: " + socket.getLocalAddress() + ":" + socket.getLocalPort());
                    ta_client_info.appendText("\nReady to send request...");

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

        if(!tf_loan_amount.getText().isEmpty()
        && !tf_loan_interest.getText().isEmpty()
        && !tf_loan_duration.getText().isEmpty()){

            ta_client_info.appendText("\nSending request to server: ");
            ta_client_info.appendText("\nLoan amount: " + tf_loan_amount.getText());
            ta_client_info.appendText("\nLoan Interest: " + tf_loan_interest.getText());
            ta_client_info.appendText("\nLoan Duration: " + tf_loan_duration.getText() + " years");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Loan loan = new Loan(Double.valueOf(tf_loan_amount.getText()),
                            Double.valueOf(tf_loan_interest.getText()),
                            Integer.valueOf(tf_loan_duration.getText()));

                    try {
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                        System.out.println("Writing object to server");
                        objectOutputStream.writeObject(loan);

                        System.out.println("Recieving answer from server");
                        LoanCalc loanCalc = (LoanCalc) objectInputStream.readObject();

                        ta_client_info.appendText("\nRequest succesfully answered.");
                        ta_client_info.appendText("\nTotal pay: " + loanCalc.getTotalPay());
                        ta_client_info.appendText("\nMonthly pay: " + loanCalc.getMonthlyPay());


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
}
