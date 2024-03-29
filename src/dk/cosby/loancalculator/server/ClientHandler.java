package dk.cosby.loancalculator.server;

import dk.cosby.loancalculator.client.Bmi;
import dk.cosby.loancalculator.client.Loan;
import javafx.scene.control.TextArea;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

public class ClientHandler implements Runnable {


    private LoanCalc loanCalc;
    private Loan clientLoanRequst;
    private Bmi clientBmi;
    private BmiCalc bmiCalc;
    private TextArea ta_server_info;

    private final Socket socket;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;


    public ClientHandler(Socket socket, ObjectInputStream ois, ObjectOutputStream oos, TextArea textArea) {
        this.socket = socket;
        this.objectInputStream = ois;
        this.objectOutputStream = oos;
        ta_server_info = textArea;
    }

    @Override
    public void run() {

        while(socket.isConnected()) {

            try {
                System.out.println("Getting object from client");
                Object o = objectInputStream.readObject();

                if (o instanceof Loan) {
                    Loan loan = (Loan) o;

                    if (loan.getAmount() == 0 && loan.getInterest() == 0 && loan.getDuration() == 0) {
                        objectInputStream.close();
                        objectOutputStream.close();
                        socket.close();
                        break;

                    } else {
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
                        objectOutputStream.flush();

                        ta_server_info.appendText("\nObject written to client");
                    }
                } else if (o instanceof Bmi){
                    Bmi bmi = (Bmi) o;

                    if (bmi.getHeight() == 0 && bmi.getWeight() == 0) {
                        objectInputStream.close();
                        objectOutputStream.close();
                        socket.close();
                        break;

                    } else {
                        ta_server_info.appendText("\nRequest received from client " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                        ta_server_info.appendText("\nTimestamp: " + new Date());
                        ta_server_info.appendText("\nHeigh: " + bmi.getHeight());
                        ta_server_info.appendText("\nWeight: " + bmi.getWeight());

                        clientBmi = bmi;

                        System.out.println("Calculating Bmi");
                        BmiCalc bmiCalc = new BmiCalc(bmi.getHeight(), bmi.getWeight());

                        ta_server_info.appendText("\nBmi calculated");
                        ta_server_info.appendText("\nClient Bmi " + bmiCalc.getBmi());
                        ta_server_info.appendText("\nClient is: " + bmiCalc.getStatus());
                        ta_server_info.appendText("\nWriting object to client");


                        System.out.println("Writing object to client");
                        objectOutputStream.writeObject(bmiCalc);
                        objectOutputStream.flush();

                        ta_server_info.appendText("\nObject written to client");
                    }

                }

                } catch(EOFException e){
                    System.out.println("EOF error");
                    break;
                } catch(java.net.SocketException e){
                    try {
                        objectInputStream.close();
                        objectOutputStream.close();
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                } catch(IOException e){
                    e.printStackTrace();
                } catch(ClassNotFoundException e){
                    e.printStackTrace();
                }
            }

        System.out.println("Clienthandler disconnected");

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
