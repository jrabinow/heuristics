#include <arpa/inet.h>	// Networking

#include "utils.h"

#define createSocket(domain)		socket(domain, SOCK_STREAM, 0)

extern int errno;

/****************** utils.c *****************/
//Message *readSocket(int fd);
//Message *readLine(FILE *file);

// For struct addrinfo
#include <netdb.h>

#define getIPAddr(addr, buffer)	inet_ntop(addr->ai_family, &((struct sockaddr_in*) addr->ai_addr)->sin_addr, buffer, sizeof(buffer))
#define getPort(addr)		ntohs(((struct sockaddr_in*) addr->ai_addr)->sin_port)
#define Connect(sockfd, addr)	connect(sockfd, addr->ai_addr, (socklen_t) sizeof(*addr->ai_addr))

int connectTo(char *serverName, int port);
int IPC_client(char *port);
