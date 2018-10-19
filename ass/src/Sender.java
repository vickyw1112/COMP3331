import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Sender {
    public static void main(String[] args) {
        if(args.length != 14){
            System.err.println("wrong num of args, check your command and try again");
            return;
        }
        String address = args[0];
        int port = Integer.parseInt(args[1]);
        int MWS = Integer.parseInt(args[3]);
        int MSS = Integer.parseInt(args[4]);
        STP socket = new STP(port, MWS, MSS, address);
        String filename = args[2];
        SocketAddress socketAddress = new InetSocketAddress(address, port);
        try {
            socket.connect(socketAddress);
            File file = new File(filename);
            InputStream fileIn = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            while (fileIn.read(buffer) > 0) {
                socket.sendData(buffer);
            }
        } catch(IOException e){
            System.err.println("can not open file");
            e.printStackTrace();
        }

        long start = socket.getSenderLog().get(0).time;

        PrintStream out = null;
        File file = new File("Sender_Log.txt");
        try {
            out = new PrintStream(file);
        }catch(IOException e){
            e.printStackTrace();
        }
        for(Log log: socket.getSenderLog()){
            out.println(log.toString(start));
        }
        out.println("=======================================================================================\n");
        out.println("Size of file (bytes)"+"                                "+(int) file.length());
        out.println("Segments transmitted (including drop & RXT)"+"         "+socket.getSendBufSize());
        out.println("Number of Segments handled by PLD"+"                   "+0);
        out.println("Number of Segments dropped "+"                         "+0);
        out.println("Number of Segments Corrupted"+"                        "+0);
        out.println("Number of Segments Re-ordered"+"                       "+0);
        out.println("Number of Segments Duplicated"+"                       "+0);
        out.println("Number of Segments Delayed"+"                          "+0);
        out.println("Number of Retransmissions due to TIMEOUT"+"            "+0);
        out.println("Number of FAST RETRANSMISSION "+"                      "+0);
        out.println("Number of DUP ACKS received"+"                         "+0);
        out.println("=======================================================================================\n");
    }
}
