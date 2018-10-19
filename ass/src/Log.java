import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class Log {
    private String event;
    public long time;
    private String typeOfPackage;
    private int seqNum;
    private int dataSize;
    private int ackNum;
    private static DecimalFormat df2 = new DecimalFormat(".###");


    public Log(long time, Segment seg, String type){
        this.time = TimeUnit.MILLISECONDS.toSeconds(time);
        this.ackNum = seg.getAckNum();
        this.seqNum = seg.getSeqNum();
        this.dataSize = seg.getPayloadSize();
        this.event = type;
        if(seg.isAckSynFlag())
            this.typeOfPackage = "SA";
        else if(seg.isSynFlag())
            this.typeOfPackage = "S";
        else if(seg.isFinFlag())
            this.typeOfPackage = "F";
        else if(seg.isAckFlag())
            this.typeOfPackage = "A";
        else if(seg.isDataFlag())
            this.typeOfPackage = "D";
    }

    public String toString(long start){
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================================================================\n");
        sb.append("event        time        type-of-packet      seqNum      size        ackNum\n");
        long time = this.time - start;
        sb.append(this.event+"              "+df2.format(time)+"            "+this.typeOfPackage+"              "+this.seqNum+"             "+this.dataSize+"           "+this.ackNum);
        sb.append("\n");
        return new String(sb);
    }
}
