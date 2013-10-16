#ifndef _UTILS_H
#define _UTILS_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>

typedef enum { false = 0, true } bool;

/* ----- WRAPPER FUNCTIONS ----- */
// exit program with failed status if malloc and consorts fail
// -> no more error checking necessary
// free() when done
void *xmalloc(size_t size);
void *xcalloc(size_t nmemb, size_t size);
char *xstrdup(const char *str);
void *xrealloc(void *ptr, size_t size);
FILE *xfopen(const char *path, const char *mode);
FILE *xfdopen(int fd, const char *mode);
#ifndef WIN32
#include <unistd.h>
#include <fcntl.h>
#include <signal.h>
int xopen(const char *path, int flags);
void xpipe(int *pipefd);
pid_t xfork(void);
void xdup2(int oldfd, int newfd);
#endif
void register_signal_handler(int signum, void (*sighandler)(int));


/* ----- STRING APPENDING ----- */

// Appends str2 to str1 in a new memory location
// free() when done
char *const_append(const char *str1, const char *str2);

// Appends str2 to str1. str1 MUST be dynamically allocated
// free() when done
char *append(char *str1, const char *str2);


/* ----- GENERAL-PURPOSE ----- */

// sets str to lower case
void str_tolower(char *str);

// convert from hexadecimal format string to integer
int hexatoi(const char *hex);

// return pointer to first occurence of char in s not equal to c. If str is made up entirely
// of c characters, returns NULL
char *neg_strchr(char *s, int c);

// returns pointer to preallocated memory area containing base 10 string representation of val
// buffer passed as second argument must be sufficiently large.
char *itoa(int val, char *buffer);

// reads a complete line (no length limit) from file.
// free() when done
char *readLine(FILE *stream);
#include <ctype.h>
char *read_file_descriptor(int fd);

/* separates str along separator chars into non-empty tokens. If str is composed only
 * of separator chars, returnArray will point to NULL.
 * Otherwise, returnArray will point to dynamically allocated array with one string token
 * per array element.
 * return value is size of array.
 * free() *returnArray, (*returnArray)[0], (*returnArray)[1] ... when done */
size_t split_str(const char *str, const char separator, char ***returnArray);

// returns true if str1 and str2 are 2 different strings
#define equals(str1, str2)	(strcmp(str1, str2) == 0)

#define empty_buffer()	{\
	int __c__;\
	while((__c__ = getchar()) != EOF && __c__ != '\n');\
}


/* ----- FILESYSTEM ----- */

// returns 1 if path is dir, -1 in case of error and 0 otherwise
#include <sys/stat.h>
int is_dir(char *path);

// creates a new dynamically allocated string of the form "path/filename"
// free() when done
char *make_path(const char *path, const char *filename);

// iterates over every file in filesystem under path
#include <dirent.h>
void dirwalk(const char *path);


/* ----- TERMINAL FUN ----- */
#ifndef _WIN32
typedef enum {BLACK, RED, GREEN, YELLOW, BLUE, PINK, CYAN, WHITE} Color;
typedef enum {NORMAL, BOLD} Font;

typedef struct {
	Color c;
	Color bgc;
	Font f;
	char set;
} ColorEnv;

//static ColorEnv session;

void setColorEnv(Color c, Color bgc, Font f);
void printString(char *str, Color c, Color bgc, Font f);
#define reset_color_profile()		printf("\x1B[0m")

// input character display
#include <termios.h>
/*#define turn_echoing_off() {\
	struct termios t;\
	tcgetattr(0, &t);\
	t.c_lflag &= ~ECHO;\
	tcsetattr(0, TCSANOW, &t);\
}

#define turn_echoing_on() {\
	struct termios t;\
	tcgetattr(0, &t);\
	t.c_lflag |= ECHO;\
	tcsetattr(0, TCSANOW, &t);\
}
*/
/*
// call once for getchar to return char as soon as it is entered on stdin (without pressing enter)
// call a second time to disable
#include <termios.h>
#include <unistd.h>
void toggle_instant_getchar(void);
*/
#endif

#endif
