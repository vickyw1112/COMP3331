#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <sys/stat.h>

#define MAX_HEADER_LEN 8190

int main(int argc, char const *argv[]){
    // create socket
    int s = socket(AF_INET, SOCK_STREAM, 0);
    if (s < 0){
        perror(NULL);
        exit(1);
    }
    // bind the socket to the listing port
    struct sockaddr_in serverAddress;
    serverAddress.sin_family = AF_INET;
    serverAddress.sin_addr.s_addr = INADDR_ANY;
    int portNum = atoi(argv[1]);
    if(portNum < 0)
        portNum = 80;
    serverAddress.sin_port = htons(portNum);
    if(bind(s, (struct sockaddr *)&serverAddress, sizeof(serverAddress)) < 0){
        perror(NULL);
        exit(1);
    }      
    
    //wait for a browser to request a connection
    int maxBacklog = 5;   
    // listen for a connection
    if(listen(s, maxBacklog)){
        perror(NULL);
        exit(1);
    }
    
    char request[MAX_HEADER_LEN];
    while(1){
        struct sockaddr_in clientAddr;
        // open a new connection for this conversation
        socklen_t clientLen = sizeof(clientAddr);
        int ss = accept(s, (struct sockaddr *)&clientAddr, &clientLen);
        FILE *ss_stream = fdopen(ss, "r");
        fgets(request, MAX_HEADER_LEN - 1, ss_stream);

        char method[5];
        char query[100];
        if(sscanf(request, "%s %s ", method, query) != 2){
            fclose(ss_stream);
            continue;
        }
        printf("%s\n", request);

        // read until header finishes
        while(fgets(request, MAX_HEADER_LEN - 1, ss_stream)){
            if(strcmp(request, "\r\n") == 0)
                break;
        }

        int fd;
        if((fd = open(query + 1, O_RDONLY)) < 0){
            perror(NULL);
            char *msg = "HTTP/1.1 404 Not Found\r\nConnection: close \r\nContent-Type: text/html\r\n\r\n<html><body>404 Not Found<body><html>";
            write(ss, msg, strlen(msg));
            fclose(ss_stream);
            continue;
        }
        
        // send HTTP header
        int len;
        "HTTP/1.1 200 OK\r\n";
        len = lseek(fd, 0, SEEK_END);
        lseek(fd, 0, SEEK_SET);
        char resHeader[100];
        char *token = strtok(query, ".");
        char *ext;
        char *mimeType;
        while(token != NULL){
            ext = token;
            token = strtok(NULL, ".");
        }
        if(strcmp(ext, "html") == 0){ 
            mimeType = "text/html";
        } else if(strcmp(ext, "jpg") == 0 ||
                strcmp(ext, "jpeg") == 0){
            mimeType = "image/jpeg";
        } else if(strcmp(ext, "png") == 0){
            mimeType = "image/png";
        } else{
            mimeType = "application/octet-stream";
        }

        
        sprintf(resHeader, "HTTP/1.1 200 OK\r\n"
            "Content-Length: %d\r\n"
            "Content-Type: %s\r\n"
            "Connection: close\r\n\r\n", len, mimeType);
        write(ss, resHeader, strlen(resHeader));
        char msg[1000];
        while((len = read(fd, msg, sizeof(msg))) > 0){
            write(ss, msg, len);
        }

        printf("Response sent.\n");

        close(fd);
        fclose(ss_stream);
    }

    close(s);    
    return 0;
}


