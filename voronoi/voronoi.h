#include "utils.h"
#include <math.h>
#include <float.h>

#define BOARD_SIZE	1000
#define NUM_PLAYS	10
#define NUM_PLAYERS	2


typedef struct {
	unsigned short x, y, player;
	double value[NUM_PLAYERS];
} Point;

typedef union {
	Point **plays;
} Type;

typedef struct {
	Type t;
	unsigned size, mem_size;
} Array;

typedef struct {
	Point *coord;
	unsigned int num_points[NUM_PLAYERS];
} Board;

/* ----- STRATEGIES ----- */
Point *random_player(Board *board, Point **player1, Point **player2, unsigned short id);
Point *interactive_player(Board *board, Point **player1, Point **player2, unsigned short id);

/* ----- GENERAL ----- */
void play_game(Board *board, Point **player1, Point **player2, int num_plays);
void pull_on_matrix(Board *board, Point *p);

#define distance(val1, val2)	sqrt((val1) * (val1) + (val2) * (val2))
#define turn_number(array)	(array)->size

#define coord(x, y)		coord[(x) * BOARD_SIZE + (y)]

#define set_owner(point, id)	((point).player = (point).value[(id) - 1] > (point).value[(point).player - 1] ? (id) : (point).player)

Point *(*strategy1)(Board*, Point **, Point**, unsigned short) = &random_player;
Point *(*strategy2)(Board*, Point **, Point**, unsigned short) = &random_player;

