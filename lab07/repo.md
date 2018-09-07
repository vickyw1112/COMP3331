# COMP3331 18s2 lab04
#### Haibo Wang(z5135009)
### Exercise 1
Q1: 

1. 

    IP address of gaia.cs.umass.edu is 128.119.245.12, port number is 80.    
    And for client, IP address is 192.168.1.102, port number is 1161.

2. 

    Sequence number: 232129013.

3. 
    
   **seg4**

    Sequence number: 232129013
    sent from: 0.026477s
    time when ACK recevied: 0.053937s
    RTT: 0.02746s
    EstimatedRtt: 0.02746s
    length: 565
**seg5**

    Sequence number: 232129578
    sent from: 0.041737s
    time when ACK recevied: 0.077294s
    RTT: 0.035557s
    EstimatedRtt: 0.028472125s
    length: 1460
**seg7**

    Sequence number: 232131038
    sent from: 0.054026s
    time when ACK recevied: 0.124085s
    RTT: 0.070059s
    EstimatedRtt: 0.033670484375s
    length: 1460
**seg8**

    Sequence number: 232132498
    sent from: 0.054690s
    time when ACK recevied: 0.169118s
    RTT: 0.114428s
    EstimatedRtt: 0.04376512s
    length: 1460
**seg10**

    Sequence number: 232133958
    sent from: 0.077405s
    time when ACK recevied: 0.217299s
    RTT: 0.139894s
    EstimatedRtt: 0.0557813s
    length: 1460
**seg11**

    Sequence number: 232135418
    sent from:0.078157
    time when ACK recevied: 0.267802s
    RTT: 0.189645s
    EstimatedRtt: 0.07251424s
    length: 1460

4.
    
    see above

5.
    
    5840bytes
    By inspection, it is not throttled.
    This reviver window grows until it reaches the maximum receiver buffer size

6.

    no retransmitted segmens

    check the sequence number and ack number
    
7.

    2920bytes
    receiver ack every other received segment for example: packet 80 and 87
    
8. 

    TotalBytes: 232293103 - 232129012 = 164091 bytes
    TotalTime: 7.595557
    AverageThroughput: 164091 / 7.595557 = 21603.550602 bytes/s

### Exercise 2

1.
    
    seqNum = 2818463618

2.

    seqNum = 1247095791
    ACK = 2818463619
    If all data prior to server already received, the ACK will be next bytes of prior sequence number, by contrast the ACK will be prior sequence number.

3. 

    seqNum = 2818463619
    ACK = 124705791
    the segment contains 33 bytes data.

4.

    client
    because client send first FIN
    simultaneous close

5.

    client to server: 33 bytes
    server to client: 40 bytes
    it will be same 





    
    
    



