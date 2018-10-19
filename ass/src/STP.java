import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;

public class STP {
    private int MSS; // max segment size
    private int sendPort;
    private int receiverPort;
    private int MWS; // max window size
    private int gamma; // for timeout
    private int seed;
    private Timer timer;
    private DatagramSocket STPSocket;
    private int seqNum = 0; // for sender
    private int ackNum = 0; // for receiver
    private TreeSet<Segment> receiveBuf; // for receiver
    private List<Segment> sendBuf; // for sender
    private static final int MAX_BUFFER_SIZE = 1024;
    private InetAddress sendAddress;
    private InetAddress receiveAddress;
    private Thread listener;
    private List<Segment> acks;
    private int retransmitNum = 0;
    private List<Log> senderLog;
    private List<Log> receiveLog;
    private int sendBufSize;

    private State state = State.NONE;

    /**
     * for sender connects
     */
    public STP(int port, int MWS, int MSS,String address) {
        try {
            this.STPSocket = new DatagramSocket();
        } catch(Exception e){
            e.printStackTrace();
        }
        this.receiverPort = port;
        try {
            this.receiveAddress = InetAddress.getByName(address);
        }catch (UnknownHostException e){
            e.printStackTrace();
        }
        this.MWS = MWS;
        this.MSS = MSS;
        this.sendBuf = new LinkedList<>();
        this.acks = new ArrayList<>();
        this.senderLog = new ArrayList<>();
    }

    public List<Log> getSenderLog(){ return this.senderLog; }
    public List<Log> getReceiveLog(){ return this.receiveLog; }
    public int getSendBufSize(){ return this.sendBufSize; }
    /**
     * for receiver
     */
    public STP(int port){
        this.receiveBuf = new TreeSet<>();
        this.receiverPort = port;
        try {
            this.STPSocket = new DatagramSocket(port);
        } catch(SocketException e){
            e.printStackTrace();
        }
        this.receiveLog = new ArrayList<>();
    }
    /**
     * bind the socket to a specific port and address
     * for receiver
     * @param bindPoint
     * @throws IOException
     */
    public void bind(SocketAddress bindPoint) throws IOException {
        this.STPSocket.bind(bindPoint);
        System.out.println("Binding successfully");
    }

    /**
     * init connection
     * and analogy three handshakes
     * @throws IOException
     * @return boolean
     */
    public void connect(SocketAddress address) throws IOException{
        this.STPSocket.connect(address);
        handShake();
        // TODO for mutithreading
//        STP stp = this;
//        listener = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                byte[] buf = new byte[MAX_BUFFER_SIZE];
//                DatagramPacket datagramPacket = new DatagramPacket(buf, MAX_BUFFER_SIZE);
//                Segment ackPackage = null;
//                try {
//                    stp.STPSocket.receive(datagramPacket);
//                    int len  = datagramPacket.getLength();
//                    ackPackage = new Segment(datagramPacket.getData(), len);
//                } catch (IOException e){
//                    System.err.println("Can not receive ack package");
//                    e.printStackTrace();
//                }
//
//                for(Segment segment: stp.sendBuf){
//                    if(ackPackage.getAckNum() == segment.getNextPackageSeqNum()){
//                        stp.sendBuf.remove(segment);
//                        break;
//                    }
//                }
//            }
//        });
        System.out.println("connection");
    }

    /**
     * fake threeway handshake
     * @throws IOException
     */
    private void handShake() throws IOException{
//        Segment handShake1 = new Segment(Segment.SYN, 0, 0, null);
//        DatagramPacket DpSend1 = new DatagramPacket(handShake1.getByteSegment(), handShake1.getSegSize());
//
//        long start = System.currentTimeMillis();
//        Log log = new Log(handShake1, "snd", );
//        this.STPSocket.send(DpSend1);
        this.createSending(Segment.SYN, 0, 0, null, this.receiveAddress, this.receiverPort, this.senderLog);
        System.out.println("Threeway handshake 1/3.");
        byte[] buff = new byte[MAX_BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);
        long time = System.currentTimeMillis();
        this.STPSocket.receive(packet);
        //this.receiveAddress = packet.getAddress();
        Segment seg = new Segment(packet.getData(), 16);
        Log log = new Log(time, seg, "rcv");
        // add to log
        this.senderLog.add(log);
        if (seg.isAckSynFlag()) {
            System.out.println("Threeway handshake 2/3.");
            this.createSending(Segment.ACK, 1, 1, null, this.receiveAddress, this.receiverPort, this.senderLog);
            System.out.println("Threeway handshake 3/3.");
        }
    }

    /**
     * send data for sender
     * ack = 1 after handshaking
     * @param buf
     * @throws IOException
     */
    public void sendData(byte[] buf) throws IOException{
        for(int offset = 0; offset < buf.length; offset += this.MSS){
            ByteBuffer temp = ByteBuffer.allocate(this.MSS);
            int remain = buf.length - offset;
            int len = remain < this.MSS ? remain : this.MSS;
            temp.put(buf, offset, len);
            Segment segment = new Segment(Segment.DATA, seqNum, 1, temp.array());
            seqNum += len;
            this.sendBuf.add(segment);
            this.sendBufSize++;
        }
        Segment pack;
        long time;
        Log log;
        for(int i = 0; i < this.sendBuf.size();){
            Segment cur = this.sendBuf.get(i);
            // log
            time = System.currentTimeMillis();
            log = new Log(time, cur, "snd");
            this.senderLog.add(log);
            this.STPSocket.send(new DatagramPacket(cur.getByteSegment(), cur.getSegSize()));

            DatagramPacket packet = new DatagramPacket(new byte[MAX_BUFFER_SIZE], MAX_BUFFER_SIZE);
            this.STPSocket.receive(packet);
            // log
            time = System.currentTimeMillis();
            log = new Log(time, cur, "rcv");
            this.senderLog.add(log);
            int len = packet.getLength();
            pack = new Segment(packet.getData(), len);
            if (pack.isAckFlag()) {
                i++;
            }
        }
        int i = 0;
        while(true) {
            this.createSending(Segment.FIN, 1, 1, null, this.receiveAddress, this.receiverPort, this.senderLog);
            DatagramPacket packet = new DatagramPacket(new byte[MAX_BUFFER_SIZE], MAX_BUFFER_SIZE);
            this.STPSocket.receive(packet);
            time = System.currentTimeMillis();
            // log
            int trueLen = packet.getLength();
            Segment segment = new Segment(packet.getData(), trueLen);
            log = new Log(time, segment, "rcv");
            this.senderLog.add(log);
            i++;
            if(i == 2){
                this.createSending(Segment.ACK, segment.getAckNum(), 2, null, this.receiveAddress, this.receiverPort, this.senderLog);
                break;
            }
        }



    }

    // cur is current package that needs to send
    // assuming it's in flight
    private int calculateInFlightBytes(Segment cur){
        int sum = 0;
        for(Segment segment: this.sendBuf){
            sum += segment.getPayloadSize();
            if(segment.equals(cur))
                break;
        }
        return sum;
    }

    // Max window size
    private boolean isTimeToSend(int flightbytes){
        if(flightbytes > this.MWS)
            return false;
        return true;
    }



    public TreeSet<Segment> receiveData() throws IOException{
        int trueLen = 0;
        while(true) {
            Log log;
            long time;
            DatagramPacket packet = new DatagramPacket(new byte[MAX_BUFFER_SIZE], MAX_BUFFER_SIZE);
            this.STPSocket.receive(packet);
            time = System.currentTimeMillis();
            trueLen = packet.getLength();
            this.sendAddress = packet.getAddress();
            Segment segment = new Segment(packet.getData(), trueLen);
            log = new Log(time, segment, "rcv");
            this.receiveLog.add(log);
            this.sendPort = packet.getPort();

            // send ACKSYN for three handshaking
            // received data is handshaking
            if (segment.isSynFlag()) {
                this.createSending(Segment.ACKSYN, 0, 1, null, sendAddress, this.sendPort, this.receiveLog);
            } else if (segment.isAckFlag()) {
                // get a FINACk from sender
                if(segment.getAckNum() > 1){
                    System.out.println("Connecting loss");
                    break;
                }
                System.out.println("Connecting successfully via STP");
            }
            // received package is data
            else if (segment.isDataFlag()) {
                // cur acknum = current segment's seqNum + it's payloadSize
                ackNum = segment.getNextPackageSeqNum();
                // if receive buffer does not have this date add it to buf
                receiveBuf.add(segment);
                // convert set to list
                // for checking every elements in the set
                List<Segment> set = new LinkedList<>();
                set.addAll(this.receiveBuf);
                int i;
                for (i = 0; i < set.size() - 1; i++) {
                    Segment cur = set.get(i);
                    Segment next = set.get(i + 1);
                    int nextSeq = cur.getNextPackageSeqNum();
                    if (nextSeq == next.getSeqNum()) {
                        continue;
                    } else {
                        ackNum = nextSeq;
                        break;
                    }
                }
                // after loop if i = set.size -1
                // which means data is ordered and consecutive
                if (i == set.size() - 1) {
                    ackNum = this.receiveBuf.last().getNextPackageSeqNum();
                }
                // send ACK to sender if client receive the correct package
                this.createSending(Segment.ACK, 1, ackNum, null, this.sendAddress, this.sendPort, this.receiveLog);
            }
            // received data is FIN
            else if (segment.isFinFlag()) {
                this.createSending(Segment.ACK, 1, ackNum, null, this.sendAddress, this.sendPort, this.receiveLog);
                this.createSending(Segment.FIN, 1, ackNum, null, this.sendAddress, this.sendPort, this.receiveLog);
            }
        }
        return this.receiveBuf;
    }

    private void createSending(int flag, int seqNum, int ackNum, byte[] buf, InetAddress address, int port, List<Log> list) throws IOException{
        long startTime = System.currentTimeMillis();
        Segment ackSeg = new Segment(flag, seqNum, ackNum, buf);
        Log log = new Log(startTime, ackSeg, "snd");
        list.add(log);
        DatagramPacket ackPacket = new DatagramPacket(ackSeg.getByteSegment(), ackSeg.getSegSize(), address, port);
        this.STPSocket.send(ackPacket);
    }


}
