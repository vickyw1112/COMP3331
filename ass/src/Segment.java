import com.sun.xml.internal.ws.util.ByteArrayBuffer;

import java.nio.ByteBuffer;

public class Segment implements Comparable<Segment> {
    private int payloadSize;
    private int headerSize = 16;
    private int ackNum;
    private int seqNum;
    private int checkSum; // todo
    private int flag;
    private int sourcePort;
    private int desPort;
    private byte[] payload;
    private int MSS; // todo
    //private static int count = 0;
    public static final int SYN = 0x01;
    public static final int FIN = 0x02;
    public static final int ACK = 0x04;
    public static final int DATA = 0x00;
    public static final int ACKSYN = 0x04 | 0x01; // 5
    public static final int ACKFIN = 0x04 | 0x02; // 6


    public Segment(int flag, int seqNum, int ackNum, byte[] buf){
        this.seqNum = seqNum;
        this.flag = flag;
        this.ackNum = ackNum;
        this.payload = buf;
        //this.MSS = MSS;
        if(buf != null)
            this.payloadSize = buf.length;
        else
            this.payloadSize = 0;
    }

    /**
     * for receiver to store the packets received
     * @param buf
     */
    public Segment(byte[] buf, int len){
        ByteBuffer byteBuffer = ByteBuffer.allocate(buf.length);
        byteBuffer.put(buf);
        this.flag = byteBuffer.getInt(0);
        this.seqNum = byteBuffer.getInt(4);
        this.ackNum = byteBuffer.getInt(8);
        this.checkSum = byteBuffer.getInt(12);
        if(len == 16) {
            this.payload = null;
            this.payloadSize = 0;
        }
        else {
            byte[] temp = new byte[len - 16];
            int i = 16, index;
            for(index = 0; i < len; i++, index++){
                temp[index] = buf[i];
            }
            this.payload = temp;
        }
    }

    public int getSegSize(){
        return this.headerSize + this.payloadSize;
    }

    public int getPayloadSize(){ return this.payloadSize; }

    public int getSeqNum(){
        return this.seqNum;
    }

    public int getAckNum(){ return this.ackNum; }

    public int getNextPackageSeqNum(){
        if(this.payloadSize == 0)
            return this.seqNum + 1;
        return this.seqNum + this.payloadSize;
    }

    public byte[] getPayload(){ return this.payload; }

    public boolean isAckFlag() {
        return (ACK == flag);
    }

    public boolean isFinFlag() {
        return (FIN == flag);
    }

    public boolean isSynFlag() {
        return (SYN == flag);
    }

    public boolean isAckSynFlag(){
        return this.flag == ACKSYN;
    }

    public boolean isDataFlag() {
        return (DATA == flag);
    }

    /**
     * get a byte stream segment
     * @return
     */
    public byte[] getByteSegment(){
        int len = getSegSize();
        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        byteBuffer.putInt(this.flag);
        byteBuffer.putInt(seqNum);
        byteBuffer.putInt(ackNum);
        byteBuffer.putInt(checkSum);
        if(len != 16)
            byteBuffer.put(this.payload);
        return byteBuffer.array();
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Segment) || obj == null)
            return false;
        else{
            if(((Segment) obj).seqNum != this.seqNum)
                return false;
        }
        return true;
    }

    @Override
    public int compareTo(Segment o) {
        return Integer.compare(o.seqNum, this.seqNum);
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        if(this.flag == ACK)
            str.append("A");
        else if(this.flag == SYN)
            str.append("S");
        else if(this.flag == DATA)
            str.append("D");
        else if(this.flag == ACKSYN)
            str.append("SA");
        else if(this.flag == FIN)
            str.append("F");
        str.append("     ");
        str.append(this.seqNum);
        str.append("     ");
        str.append(payloadSize);
        str.append("     ");
        str.append(this.ackNum);
        return new String(str);
    }
}
