#include "voronoi.h"

static FILE *out;

void random_player(Board *board, Moves *player_moves, unsigned short id)
{
	short unsigned x, y;

	x = rand() % BOARD_SIZE;
	y = rand() % BOARD_SIZE;

	print_server(out, "(%hu,%hu,%hu)", id, x, y);
	set_move(board, player_moves, x, y, id);
}

void interactive_player(Board *board, Moves *player_moves, unsigned short id)
{
	short unsigned ret, x, y;

	do {
		puts("Please enter the x-coordinate of your move.");
		ret = scanf("%hu", &x);
		empty_buffer(stdin);
	} while(ret != 1);
	
	do {
		puts("Please enter the y-coordinate of your move.");
		ret = scanf("%hu", &y);
		empty_buffer(stdin);
	} while(ret != 1);

	print_server(out, "(%hu,%hu,%hu)", id, x, y);
	set_move(board, player_moves, x, y, id);
}

void talk_to_server_opponent(Board *board, Moves *player_moves, unsigned short id)
{
	char *line = NULL, *last_move_ptr = NULL, **splitArray = NULL;
	int count, x, y;

	empty_buffer(out);
	line = readLine(out);
	last_move_ptr = strrchr(line, '(');
	if(last_move_ptr != NULL) {	// if not first move
		count = split_str(last_move_ptr + 1, ',', &splitArray);
		free(line);
		if(count != 3) {
			fputs("Error: invalid line.\n", stderr);
			exit(EXIT_FAILURE);
		}
		x = atoi(splitArray[1]);
		y = atoi(splitArray[2]);
		id = atoi(splitArray[0]);
		set_move(board, player_moves, x, y, id);
		if(strstr(splitArray[2], "<EOM>") == NULL)
			empty_buffer(out);
		for(count--; count >= 0; count--)
			free(splitArray[count]);
	}

	empty_buffer(out);
	empty_buffer(out);
	line = readLine(out);
	if(strstr(line, "<EOM>") == NULL)
		empty_buffer(out);
	free(line);
}

void play_game(Board *board, Moves *player_moves, int num_plays, unsigned short id)
{
	int i;

	while(player_moves->move_number < player_moves->max_moves) {
		strategy1(board, player_moves, 1);
		strategy2(board, player_moves, 2);
		player_moves->move_number++;
	}

	for(i = 0; i < NUM_PLAYERS; i++)
		printf("Player %d: %d points.\n", i + 1, board->num_points[i]);
}

void update_board(Board *board, Point *p)
{
	int i;
	unsigned short player_id = p->owner;
	register double val;
	register int tmp1, tmp2;

	if(board->coord[0].owner == 0) {	// if 1st move, we just take everything
		for(i = 0; i < BOARD_SIZE * BOARD_SIZE; i++)
			if(i != p->x * BOARD_SIZE + p->y) {	// if distance != 0
				tmp1 = p->x - i / BOARD_SIZE;
				tmp2 = p->y - i % BOARD_SIZE;
				val = distance(tmp1, tmp2);
				board->coord[i].value[player_id - 1] += 10000 / (val * val);
				board->coord[i].owner = player_id;
			}
		board->num_points[player_id - 1] = BOARD_SIZE * BOARD_SIZE;
	} else {
		for(i = 0; i < BOARD_SIZE * BOARD_SIZE; i++)
			if(i != p->x * BOARD_SIZE + p->y) {	// if distance != 0
				tmp1 = p->x - i / BOARD_SIZE;
				tmp2 = p->y - i % BOARD_SIZE;
				val = distance(tmp1, tmp2);
				board->coord[i].value[player_id - 1] += 10000 / (val * val);
				// if we have more pull than the current owner, we take the point
				if(board->coord[i].value[player_id - 1] > board->coord[i].value[board->coord[i].owner - 1]) {
					board->num_points[board->coord[i].owner - 1]--;
					board->coord[i].owner = player_id;
					board->num_points[player_id - 1]++;
				}
			}
	}
}

int main(int argc, char *argv[])
{
	int i, player_id = 1;
	Moves player_moves;
	Board board;

#ifdef COMPETITION
	init_server_connection(argc, argv, &player_moves.max_moves, &player_id);
	if(player_id == 1) {
		strategy1 = &random_player;
		strategy2 = &talk_to_server_opponent;
	} else {
		strategy1 = &talk_to_server_opponent;
		strategy2 = &random_player;
	}
#else
	player_moves.max_moves = NUM_PLAYS;
	out = stdout;
#endif
	board.coord = (Point*) xcalloc(BOARD_SIZE * BOARD_SIZE, sizeof(Point));

	for(i = 0; i < NUM_PLAYERS; i++)
		player_moves.points[i] = (Point**) xmalloc(player_moves.max_moves * sizeof(Point*));
	player_moves.move_number = 0;

	play_game(&board, &player_moves, player_moves.max_moves, player_id);

	/* BENCHMARKING
	 * $ time ./voronoi
	 */

	/*	Point p;
		p.x = 50;
		p.y = 50;
		p.player = 42;
		int i;
		for(i = 0; i < BOARD_SIZE; i++)
		update_board(&board, &p);
		*/

	free(board.coord);
	for(i = 0; i < NUM_PLAYERS; i++)
		free(player_moves.points[i]);
#ifdef COMPETITION
	shutdown(fileno(out), SHUT_RDWR);
	fclose(out);
#endif
	return 0;
}

void init_server_connection(int argc, char **argv, int *num_plays, int *player_id)
{
	char *line = NULL;
	char **splitArray = NULL;
	int count;

	if(argc != 2) {
		fprintf(stderr, "Usage: %s PORTNUM\n", argv[0]);
		exit(0);
	}
	out = xfdopen(connectTo("localhost", atoi(argv[1])), "r+");
	empty_buffer(out);
	fprintf(out, TEAM_NAME"<EOM>");

	line = readLine(out);
	count = split_str(line, ',', &splitArray);
	free(line);
	if(count != 4) {
		fputs("Invalid line read\n", stderr);
		exit(EXIT_FAILURE);
	}
	*num_plays = atoi(splitArray[1]);
	*player_id = atoi(splitArray[3]);
	for(count--; count >= 0; count--)
		free(splitArray[count]);
}
