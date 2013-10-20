#include "networking.h"

int connectTo(char *server_name, int port)
{
	int sockfd = createSocket(PF_INET);
	struct addrinfo *adresses = NULL, *iterator = NULL;
	char portBuff[11];	// 9 < log(2^32,10) < 10. We add one for terminating '\0'

	// DNS resolution. getaddrinfo creates a linked list (pointed to by adresses),
	// 1 element == 1 IP Address. Function IPv6 compatible.
	if(getaddrinfo(server_name, itoa(port, portBuff), NULL, &adresses) != 0) {
		fprintf(stderr, "Unable to resolve '%s' to a valid IP address.\n", server_name);
		exit(EXIT_FAILURE);
	}

	iterator = adresses;
	
	// Iterate over linked list until we connect successfully or we run out of addresses.
	while(Connect(sockfd, iterator) == -1) {
		iterator = iterator->ai_next;
		if(iterator == NULL)
			break;
	}
	if(iterator == NULL) {
		fprintf(stderr, "Unable to connect to %s on port %d. Quitting now…\n", server_name, port);
		exit(EXIT_FAILURE);
	}
	freeaddrinfo(adresses);
	return sockfd;
}

int IPC_client(char *port)
{
	int sockfd = createSocket(AF_UNIX);
	if(sockfd == -1) {
		perror("Error creating socket ");
		exit(EXIT_FAILURE);
	}

	
	return sockfd;
}
