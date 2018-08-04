#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>

int main(int argc, char* argv[]){
    // criteria to filter the correct sockaddr
    struct addrinfo hints;

    memset(&hints, 0, sizeof(hints));
    hints.ai_family = AF_INET; // use IPv4
    hints.ai_socktype = SOCK_DGRAM; // use UDP

    // store the result addrinfo list
    struct addrinfo *res;

    if(getaddrinfo(argv[1], argv[2], &hints, &res) != 0){
        perror("getaddrinfo");
        exit(1);
    }
    
    int s;
    // create socket
    if((s = socket(AF_INET, SOCK_DGRAM,0)) < 0){        
        perror("socket");
        exit(1);
    }

    char buf[100];
    strcpy(buf, "hello");
    if(sendto(s, buf, strlen(buf), 0, res->ai_addr, res->ai_addrlen) < 0){
        perror(NULL);
        exit(1);
    }
    close(s);
    return 0;
}
