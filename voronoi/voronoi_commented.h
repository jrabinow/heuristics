#include "utils.h"
#include <math.h>
#include <float.h>

#define BOARD_SIZE	1000
#define NUM_PLAYS	10		// default number of turns each player has (number of turns may be modified at runtime)
#define NUM_PLAYERS	2

/* x and y: point coordinates
 * player: player ID of whoever "owns" the point (player IDs start at 1, 0 is reserved for 
 * the first turn
 * value: array of sums of pulls each player has on the point */
typedef struct {
	unsigned short x, y, player;
	double value[NUM_PLAYERS];
} Point;
/* defining an array of predefined size significantly optimizes the program:
 * we don't have to individually allocate memory for every single Point */

typedef union {
	Point **plays;
} Type;
/* Union only contains one element now. Later, if we need an array of type T,
 * we can just add a T* (pointer to T) field in the union. Combined with the Array struct right below,
 * this gives us an array of T's.
 * */

typedef struct {
	Type t;
	unsigned size, mem_size;
} Array;

/* ----- STRATEGIES ----- */
/* Interactive strategy asking for person to manually enter coordinates. id is our own player ID */
Point *interactive_player(Point **board, Array *player1, Array *player2, unsigned short id);
/* self-explicit (I hope!) */
Point *random_player(Point **board, Array *player1, Array *player2, unsigned short id);


/* ----- GENERAL ----- */
/* Main game loop */
void play_game(Point **board, Array *player1, Array *player2, int num_plays);

/* Adds point p to board and updates values in each Point of the board to take p's pull into account */
void pull_on_matrix(Point **board, Point *p);

/* Adds point p to Array */
void array_add(Array *player, Point *p);

#define distance(val1, val2)	sqrt((val1) * (val1) + (val2) * (val2))
#define turn_number(array)	(array)->size

int board_size = BOARD_SIZE;

/* Having to search through the code to do something simple is always a pain.
 * To avoid having to search through the code when changing strategies, we use
 * 2 pointers to functions. To modify the strategy being used, just make the
 * pointers point to a different function
 */
Point *(*strategy1)(Point**, Array*, Array*, unsigned short) = &random_player;
Point *(*strategy2)(Point**, Array*, Array*, unsigned short) = &random_player;

