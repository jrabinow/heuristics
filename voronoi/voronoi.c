#include "voronoi.h"

Point *random_player(Board *board, Point **player1, Point **player2, unsigned short id)
{
	short unsigned x, y;

	x = rand() % BOARD_SIZE;
	y = rand() % BOARD_SIZE;

	board->coord(x, y).x = x;
	board->coord(x, y).y = y;
	board->coord(x, y).player = id;
	board->coord(x, y).value[id - 1] = DBL_MAX;
	printf("%d %d\n", x, y);

	return &board->coord(x, y);
}

Point *interactive_player(Board *board, Point **player1, Point **player2, unsigned short id)
{
	short unsigned ret, x, y;

	do {
		puts("Please enter the x-coordinate of your move.");
		ret = scanf("%hu", &x);
		empty_buffer();
	} while(ret != 1);
	
	do {
		puts("Please enter the y-coordinate of your move.");
		ret = scanf("%hu", &y);
		empty_buffer();
	} while(ret != 1);

	board->coord(x, y).x = x;
	board->coord(x, y).y = y;
	board->coord(x, y).player = id;
	board->coord(x, y).value[id - 1] = DBL_MAX;
	printf("%d %d\n", x, y);

	return &board->coord(x, y);
}

void play_game(Board *board, Point **player1, Point **player2, int num_plays)
{
	Point *p = NULL;
	int i;

	for(i = 0; i < num_plays; i++) {
		p = strategy1(board, player1, player2, 1);
		player1[i] =  p;
		pull_on_matrix(board, p);
		p = strategy2(board, player1, player2, 2);
		player2[i] =  p;
		pull_on_matrix(board, p);
	}
}

void pull_on_matrix(Board *board, Point *p)
{
	int i, j;
	unsigned short player_id = p->player;
	register double val;
	register int tmp1, tmp2;

	for(i = 0; i < BOARD_SIZE; i++)
		for(j = 0; j < BOARD_SIZE; j++)
			if(p->x != i || p->y != j) {	// if distance != 0
				tmp1 = p->x - i;
				tmp2 = p->y - j;
				val = distance(tmp1, tmp2);
				board->coord(i, j).value[player_id - 1] += 10000 / (val * val);
				set_owner(board->coord(i, j), player_id);
			}
}

int main(int argc, char **argv)
{
	int num_plays = NUM_PLAYS;
	Point **player1 = NULL, **player2 = NULL;
	Board board;
	
	if(argc > 1)
		num_plays = atoi(argv[1]);

	board.coord = (Point*) xcalloc(BOARD_SIZE * BOARD_SIZE, sizeof(Point));
	player1 = (Point**) xmalloc(num_plays * sizeof(Point*));
	player2 = (Point**) xmalloc(num_plays * sizeof(Point*));

	play_game(&board, player1, player2, num_plays);

	/* BENCHMARKING
	 * $ time ./voronoi
	 */

/*	Point p;
	p.x = 50;
	p.y = 50;
	p.player = 42;
	for(i = 0; i < 100; i++)
		pull_on_matrix(board, &p);
*/

	free(board.coord);
	free(player1);
	free(player2);
	
	return 0;
}

