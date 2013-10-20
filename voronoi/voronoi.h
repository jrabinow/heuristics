#include "utils.h"
#include "networking.h"
#include <math.h>
#include <float.h>
#include <pthread.h>

#define BOARD_SIZE	1000
#define NUM_PLAYS	10
#define NUM_PLAYERS	2
//#define COMPETITION

#define TEAM_NAME	"SupaDupaShaq"

typedef struct {
	unsigned short x, y, owner;
	double value[NUM_PLAYERS];
} Point;

typedef struct {
	Point *coord;
	unsigned int num_points[NUM_PLAYERS];
} Board;

typedef struct {
	Point **points[NUM_PLAYERS];
	int move_number, max_moves;
} Moves;

typedef struct {
	Board *b;
	short start_col, end_col;
} Pthread_Arg;

/* ----- STRATEGIES ----- */
Point *random_player(Board *board, Moves *player_moves, unsigned short id);
Point *interactive_player(Board *board, Moves *player_moves, unsigned short id);
Point *talk_to_server_opponent(Board *board, Moves *player_moves, unsigned short id);

/* ----- GENERAL ----- */
void play_game(Board *board, Moves *player_moves, int num_plays, unsigned short id);
void update_board(Board *board, Point *p);

#define distance(val1, val2)	sqrt((val1) * (val1) + (val2) * (val2))
#define turn_number(array)	(array)->size

#define coord(x, y)		coord[(x) * BOARD_SIZE + (y)]

#define set_move(board, x, y, id)	{\
	board->coord(x, y).x = x;\
	board->coord(x, y).y = y;\
	board->coord(x, y).owner = id;\
	board->coord(x, y).value[id - 1] = DBL_MAX;\
	board->num_points[id - 1]++;\
}

Point *(*strategy1)(Board*, Moves*, unsigned short) = &random_player;
Point *(*strategy2)(Board*, Moves*, unsigned short) = &random_player;

