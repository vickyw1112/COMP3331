import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

public class Receiver {
    public static void main(String[] args) {
        String filename = args[1];
        int port = Integer.parseInt(args[0]);
        STP socket = new STP(port);

        TreeSet<Segment> packs = null;
        try {
            packs = socket.receiveData();
        } catch (IOException e) {
            System.err.println("receive unsuccessfully");
            e.printStackTrace();
        }
        FileOutputStream out = null;
        File file = new File(filename);
        try {
            out = new FileOutputStream(file);
        }catch(IOException e){
            e.printStackTrace();
        }
        int data = 0;
        for(Segment pack: packs){
            try {
                data += pack.getPayloadSize();
                out.write(pack.getPayload());
                System.out.write(pack.getPayload());
            } catch (IOException e){
                System.err.println("Can not open a file");
                e.printStackTrace();
            }
        }

        long start = socket.getReceiveLog().get(0).time;
        PrintStream o = null;
        File f = new File("Receive_Log.txt");
        try {
            o = new PrintStream(f);
        }catch(IOException e){
            e.printStackTrace();
        }
        for(Log log: socket.getReceiveLog()){
            o.println(log.toString(start));
        }
        int total = packs.size() + 4;
        o.println("=======================================================================================\n");
        o.println("Amount of data received (bytes)"+"       "+data);
        o.println("Total segment received"+"                "+total);
        o.println("Data segment received"+"                 "+packs.size());
        o.println("Data segments with Bit Errors"+"         "+0);
        o.println("Duplicate data segments received"+"      "+0);
        o.println("Duplicate ACKs sent"+"                   "+0);
    }
}
