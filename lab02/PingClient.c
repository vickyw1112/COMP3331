#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>

int main(int argc, char* argv[]){
    struct addrinfo hints;
    struct addrinfo *res;

    memset(&hints, 0, sizeof(hints));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_DGRAM;

    if(getaddrinfo(argv[1], argv[2], &hints, &res) != 0){
        perror("getaddrinfo");
        exit(1);
    }

    return 0;
}
