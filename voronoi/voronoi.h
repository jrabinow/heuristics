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

/* ----- STRATEGIES ----- */
Point *interactive_player(Point **board, Array *player1, Array *player2, unsigned short id);
Point *random_player(Point **board, Array *player1, Array *player2, unsigned short id);

/* ----- GENERAL ----- */
void play_game(Point **board, Array *player1, Array *player2, int num_plays);
void pull_on_matrix(Point **board, Point *p);
void array_add(Array *player, Point *p);

#define distance(val1, val2)	sqrt((val1) * (val1) + (val2) * (val2))
#define turn_number(array)	(array)->size

int board_size = BOARD_SIZE;

Point *(*strategy1)(Point**, Array*, Array*, unsigned short) = &random_player;
Point *(*strategy2)(Point**, Array*, Array*, unsigned short) = &random_player;

