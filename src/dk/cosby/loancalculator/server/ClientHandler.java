package dk.cosby.loancalculator.server;

import dk.cosby.loancalculator.client.Loan;
import javafx.scene.control.TextArea;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

public class ClientHandler implements Runnable {

    private Socket socket;
    private LoanCalc loanCalc;
    private Loan clientLoanRequst;
    private TextArea ta_server_info;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;


    public ClientHandler(Socket socket, TextArea textArea) {
        this.socket = socket;
        ta_server_info = textArea;
    }

    @Override
    public void run() {

        try {


            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Input/output stream from socket created");


            while(true) {

                try {
                    System.out.println("Getting object from client");
                    Loan loan = (Loan) objectInputStream.readObject();

                    ta_server_info.appendText("\nRequest received from client " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                    ta_server_info.appendText("\nTimestamp: " + new Date());
                    ta_server_info.appendText("\nAmount: " + loan.getAmount());
                    ta_server_info.appendText("\nInterest: " + loan.getInterest());
                    ta_server_info.appendText("\nDuration: " + loan.getDuration() + " years");

                    clientLoanRequst = loan;

                    System.out.println("Calculating Loan");
                    LoanCalc loanCalc = new LoanCalc(loan.getAmount(), loan.getInterest(), loan.getDuration());

                    ta_server_info.appendText("\nLoan calculated");
                    ta_server_info.appendText("\nTotal payback amount: " + loanCalc.getTotalPay());
                    ta_server_info.appendText("\nMonthly payback amount: " + loanCalc.getMonthlyPay());
                    ta_server_info.appendText("\nWriting object to client");


                    System.out.println("Writing object to client");
                    objectOutputStream.writeObject(loanCalc);

                    ta_server_info.appendText("Object written to client");
                } catch (EOFException e){
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Loan getClientLoanRequst() {
        return clientLoanRequst;
    }

    public void setClientLoanRequst(Loan clientLoanRequst) {
        this.clientLoanRequst = clientLoanRequst;
    }

    public LoanCalc getLoanCalc() {
        return loanCalc;
    }

    public void setLoanCalc(LoanCalc loanCalc) {
        this.loanCalc = loanCalc;
    }

}
