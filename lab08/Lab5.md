# COMP3331 18s2 Lab5
### Haibo Wang (z5135009)

### Exercise 1

Q1:
    
Max window size is 100. it is in slow start, CWND stops growing, because packet loss occurred during slow start, no more new ACK is received. After that, CWND is reset to 1, ssthresh is reset to half the previous congestion window size.

 ![q1.png](q1.png)
 

Q2:   


188.98 packets/sec

755920 bps

![haha.png](q2.png)


Q3:

when max_cwnd is greater than 50, throughputs have no difference, if it's lower than 50, the less max_cwnd, the less averge throughputs.

when max_cwnd is 50, averge throughput reached max 227.73 packects/sec or 910.92 kbps.
utilisation rate is
910.92 / 1000 = 91%

![51.png](51.png)
![50.png](50.png)

Q4:

Max congestion window size is 100
Throughput is higher than before.

Congestion window size goes back to 1 once, but the previous one goes back 7 times.
Because in Reno when duplicated ACKs occured, cwdn is reset to half of previous, whereas in Tahoe, it is reset to 1.
![](q4.1.png)
![](q4.2.png)

### Exercise 2

Q1:

I think it's fair, because according to the graph, throughput of different pairs tends to reach a simillar level.
![](fair.png)

Q2:

When a new flow is created, the pre-existing tcp flows will decrease their throughputs.

Tcp flow control, i think this behaviour is fair, according to AIMD, if the throughput of a is very large and for b, it is small, when a new flow joins, throughput of a and b will be cur to its half, and it will converge to fairness line.

### Exercise 3

Q1:

UDP will use the whole 4Mb link,
TCP will start from slow start and try to reach 4Mb utilisation.
![](tcp.png)

Q2:

Tcp has congestion control,
udp doesn't. Tcp will start from 1 congestion window size, as for udp, it will use the whole throughputs at the beginning.

Q3:

if network is not busy, using udp will be fast, but if nerwork is busy, it may cause congestion collapse or network unusable.
Because udp is not reliable, when transmit file, udp can not promise that the file transmits successfully.