#include "utils.h"
#include "networking.h"
#include <math.h>
#include <float.h>

#define BOARD_SIZE	1000
#define NUM_PLAYS	10
#define NUM_PLAYERS	2
#define COMPETITION

#define TEAM_NAME	"SuperShaq"

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
void random_player(Board *board, Moves *player_moves, unsigned short id);
void interactive_player(Board *board, Moves *player_moves, unsigned short id);
void talk_to_server_opponent(Board *board, Moves *player_moves, unsigned short id);

/* ----- GENERAL ----- */
void play_game(Board *board, Moves *player_moves, int num_plays, unsigned short id);
void update_board(Board *board, Point *p);

void init_server_connection(int argc, char **argv, int *num_plays, int *player_id);

#define distance(val1, val2)	sqrt((val1) * (val1) + (val2) * (val2))
#define turn_number(array)	(array)->size

#define coord(x, y)		coord[(x) * BOARD_SIZE + (y)]

#define set_move(board, moves, x, y, id)	{\
	board->coord(x, y).x = x;\
	board->coord(x, y).y = y;\
	board->coord(x, y).owner = id;\
	board->coord(x, y).value[id - 1] = DBL_MAX;\
	moves->points[id - 1][moves->move_number] = &board->coord(x, y);\
	board->num_points[id - 1]++;\
	update_board(board, &board->coord(x, y));\
}

#define print_server(out, ...)	{\
	fprintf(out, __VA_ARGS__);\
	fputs("<EOM>", out);\
	fflush(out);\
}


void (*strategy1)(Board*, Moves*, unsigned short) = &random_player;
void (*strategy2)(Board*, Moves*, unsigned short) = &random_player;

