#include "voronoi.h"

Point *random_player(Board *board, Point **player1, Point **player2, unsigned short id)
{
	short unsigned x, y;

	x = rand() % BOARD_SIZE;
	y = rand() % BOARD_SIZE;

	set_move(board, x, y, id);

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

	set_move(board, x, y, id);
	
	return &board->coord(x, y);
}

void play_game(Board *board, Point **player1, Point **player2, int num_plays)
{
	Point *p = NULL;
	int i;

	for(i = 0; i < num_plays; i++) {
		p = strategy1(board, player1, player2, 1);
		player1[i] = p;
		pull_on_matrix(board, p);
		p = strategy2(board, player1, player2, 2);
		player2[i] = p;
		pull_on_matrix(board, p);
	}

	for(i = 0; i < NUM_PLAYERS; i++)
		printf("Player %d: %d points.\n", i + 1, board->num_points[i]);
}

void pull_on_matrix(Board *board, Point *p)
{
	int i;
	unsigned short player_id = p->owner;
	register double val;
	register int tmp1, tmp2;

	for(i = 0; i < BOARD_SIZE * BOARD_SIZE; i++)
		if(i != p->x * BOARD_SIZE + p->y) {	// if distance != 0
			tmp1 = p->x - i / BOARD_SIZE;
			tmp2 = p->y - i % BOARD_SIZE;
			val = distance(tmp1, tmp2);
			board->coord[i].value[player_id - 1] += 10000 / (val * val);
			if(board->coord[i].owner == 0) {
				board->coord[i].owner = player_id;
				board->num_points[player_id - 1]++;
			} else if(board->coord[i].value[player_id - 1] > board->coord[i].value[board->coord[i].owner - 1]) {
				board->num_points[board->coord[i].owner - 1]--;
				board->coord[i].owner = player_id;
				board->num_points[player_id - 1]++;
			}
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
		int i;
		for(i = 0; i < BOARD_SIZE; i++)
		pull_on_matrix(&board, &p);
		*/

	free(board.coord);
	free(player1);
	free(player2);

	return 0;
}

