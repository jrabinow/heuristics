#include "voronoi.h"

Point *random_player(Point **board, Array *player1, Array *player2, unsigned short id)
{
	short unsigned x, y;

	x = rand() % board_size;
	y = rand() % board_size;

	board[x][y].x = x;
	board[x][y].y = y;
	board[x][y].player = id;
	printf("%d %d\n", x, y);

	return &board[x][y];
}

Point *interactive_player(Point **board, Array *player1, Array *player2, unsigned short id)
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

	board[x][y].x = x;
	board[x][y].y = y;
	board[x][y].player = id;
	printf("%d %d\n", x, y);

	return &board[x][y];
}

void play_game(Point **board, Array *player1, Array *player2, int num_plays)
{
	Point *p = NULL;
	int i;

	for(i = 0; i < num_plays; i++) {
		p = strategy1(board, player1, player2, 1);
		array_add(player1, p);
		pull_on_matrix(board, p);
		p = strategy2(board, player1, player2, 2);
		array_add(player2, p);
		pull_on_matrix(board, p);
	}
}

void pull_on_matrix(Point **board, Point *p)
{
	int i, j;
	double val;
	register int tmp1, tmp2;

	for(i = 0; i < board_size; i++)
		for(j = 0; j < board_size; j++)
			if(p->x != i || p->y != j) {	// if distance != 0
				tmp1 = p->x - i;
				tmp2 = p->y - j;
				val = distance(tmp1, tmp2);
				board[i][j].value[p->player-1] += 10000 / (val * val);
			} else {
				board[i][j].value[p->player-1] = DBL_MAX;
				board[i][j].player = p->player;
			}
}

void array_add(Array *player, Point *p)
{
	player->t.plays[player->size++] = p;
	if(player->size >= player->mem_size)
		player->t.plays = (Point**) xrealloc(player->t.plays, player->mem_size <<= 1);
}

int main(int argc, char **argv)
{
	int i, num_plays = NUM_PLAYS;
	Point **board = NULL;
	Array player1, player2;
	
	if(argc > 1)
		num_plays = atoi(argv[1]);

	board = (Point**) xmalloc(board_size * sizeof(Point*));
	for(i = 0; i < board_size; i++)
		board[i] = (Point*) xcalloc(board_size, sizeof(Point));
	player1.size = player2.size = 0;
	player1.mem_size = player2.mem_size = num_plays;
	player1.t.plays = (Point**) xmalloc(num_plays * sizeof(Point*));
	player2.t.plays = (Point**) xmalloc(num_plays * sizeof(Point*));

	play_game(board, &player1, &player2, num_plays);

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

	for(i = 0; i < board_size; i++)
		free(board[i]);
	free(board);
	free(player1.t.plays);
	free(player2.t.plays);
	
	return 0;
}

