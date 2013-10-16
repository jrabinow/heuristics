#include "voronoi.h"

/* TODO: Write a strategy() function that communicates with the architecture team program */

/* Strategy for randomly playing on the board */
Point *random_player(Point **board, Array *player1, Array *player2, unsigned short id)
{
	short unsigned x, y;

	/* get random number fitting inside board */
	x = rand() % board_size;
	y = rand() % board_size;

	/* we need to tell the point its own coordinates. This is because we will be accessing the
	 * point from Arrays player1 and player2 */
	board[x][y].x = x;
	board[x][y].y = y;
	/* Tells the point it "belongs" to player number id */
	board[x][y].player = id;
	printf("%d %d\n", x, y);

	/* returns address of point */
	return &board[x][y];
}

/* Strategy for asking a user to manually input coordinates. This function closely follows the
 * pattern of random_player() */
Point *interactive_player(Point **board, Array *player1, Array *player2, unsigned short id)
{
	short unsigned ret, x, y;
	
	/* Interact with user in a secure manner (ever tried entering a letter when scanf expects a number?) */
	do {
		puts("Please enter the x-coordinate of your move.");
		ret = scanf("%hu", &x);
		empty_buffer();	/* scanf leaves the final '\n' character untouched on standard input. We don't want that.
				 * (why? try writing a program that asks for 2 numbers with scanf, then displays both
				 * numbers on screen. Simple, huh? Just try it). See utils.h for definition of empty_buffer() */
	} while(ret != 1);	/* if scanf succesfully read a number from standard input, it returned 1 -> we repeat until
				 * ret == 1 */
	
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

/* Main game loop. Iterates num_plays times */
/* Some strategies may need to update the board themselves, in which case
 * we should NOT call pull_on_matrix.*/
void play_game(Point **board, Array *player1, Array *player2, int num_plays)
{
	Point *p = NULL;
	int i;

	/* Repeat num_plays times {
	 *	get a point from strategy1
	 *	add point to list of moves
	 *	update board with pull of point on each coordinate
	 *
	 *	same thing for strategy2
	 *} */

	for(i = 0; i < num_plays; i++) {
		p = strategy1(board, player1, player2, 1);
		array_add(player1, p);
		pull_on_matrix(board, p);
	
		p = strategy2(board, player1, player2, 2);
		array_add(player2, p);
		pull_on_matrix(board, p);
	}
}

/* TODO: add an efficient max() function that will determine which player has the
 * greatest pull on each point. Use the function to update the point's player attribute */
/* TODO: define board as a struct. One of the attributes will be an array of the number of
 * points currently owned by each player (to know how many points are "owned" by
 * player id N, check the array at index N-1). Add a line inside this loop to update the values
 * as we go along the board. */
/* TODO: multithreading this function might pay off. To investigate */
void pull_on_matrix(Point **board, Point *p)
{
	int i, j;
	double val;
	register int tmp1, tmp2;

	/* For every point in the board {
	 * 	If we have just added the point to the board,
	 * 		set the pull to "infinity" (DBL_MAX)
	 *	Otherwise
	 *		calculate the pull of the newly added point
	 *		on the point currently being examined, and update
	 *		the pull value on that point for the player whose turn it just was
	 * } */
	for(i = 0; i < board_size; i++)
		for(j = 0; j < board_size; j++)
			if(p->x != i || p->y != j) {	// if distance != 0
				tmp1 = p->x - i;
				tmp2 = p->y - j;
				/* distance() is a macro (== an inline function) defined in voronoi.h */
				val = distance(tmp1, tmp2);
				board[i][j].value[p->player-1] += 10000 / (val * val);
			} else {
				board[i][j].value[p->player-1] = DBL_MAX;
				board[i][j].player = p->player;
			}
}

void array_add(Array *player, Point *p)
{
	/* Add point to next element in array */
	player->t.plays[player->size++] = p;
	/* if we need more memory, allocate some more. Since we know the number of moves
	 * before starting to play, this will be removed later on. Putting the TODO flag here
	 * can't hurt */
	if(player->size >= player->mem_size)
		player->t.plays = (Point**) xrealloc(player->t.plays, player->mem_size <<= 1);
}

/* Allocating memory is inefficient. It's one of the reasons python and java are so slow:
 * they are CONSTANTLY allocating and deallocating memory (and calling the garbage collector)
 * For example, in Java or Python, every time you want to calculate something that needs a
 * buffer, you allocate the buffer, do the calculation and deallocate it once the calculation
 * is done. If you're doing the same calculation over and over, that sucks from a performance
 * point of view.
 * In C, we can avoid that. We make the buffer global, and we allocate the buffer A SINGLE
 * TIME at the very start. We run the program, do the calculations, etc etc. We deallocate the
 * buffer only once we are completely sure that we won't need it anymore. BAM. Instant
 * performance gain.
 * Notice that ALL memory allocation and whatnot will be done in main. Once we're in the game
 * loop, everything is already allocated.
 *
 * A note on global variables: they are to be avoided, yes, but in this case it's worth it. The
 * problem with passing an additional parameter to a function is that:
 * 1 - it's not as efficient (although granted that's really marginal, and in some cases the
 * compiler can optimize that)
 * 2 - You need to update every single function header. This may sound trivial, until you're
 * passing "just another" variable to your functions. And then another. And another. It ends
 * up being a giant pain in the ass.
 * 3 - Once you've finished passing all your previously global variables as parameters to your
 * functions, you wind up with a giant function header that's completely illegible and has 36
 * different parameters, each of which you completely forgot the use for.
 */

/* TODO:
 * - Board is currently a 2-dimensional array of Points. We need to declare a struct Board.
 *   One of the fields of the board will be a single-dimensional array of Point (NOT Pointers
 *   to points).
 *   This will be useful later on when we want to copy the board. Right now we have to loop
 *   through one dimension and copy the array one row at a time. With single-dimensional, we
 *   can just call the memcpy() function and it's all done.
 *
 * - Command-line parsing. The architecture team recently released that specification => we
 *   now know what will be a parameter decided at runtime and what will not.
 */
int main(int argc, char **argv)
{
	int i, num_plays = NUM_PLAYS;
	Point **board = NULL;
	Array player1, player2;
	
	/* Parse command-line arguments */
	if(argc > 1)
		num_plays = atoi(argv[1]);

	/* allocate memory for the board */
	board = (Point**) xmalloc(board_size * sizeof(Point*));
	for(i = 0; i < board_size; i++)
		board[i] = (Point*) xcalloc(board_size, sizeof(Point));

	/* Initialize arrays for plays */
	player1.size = player2.size = 0;
	player1.mem_size = player2.mem_size = num_plays;
	player1.t.plays = (Point**) xmalloc(num_plays * sizeof(Point*));
	player2.t.plays = (Point**) xmalloc(num_plays * sizeof(Point*));

	/* Launch the main game loop */
	play_game(board, &player1, &player2, num_plays);

	/* Cleanup */
	for(i = 0; i < board_size; i++)
		free(board[i]);
	free(board);
	free(player1.t.plays);
	free(player2.t.plays);
	
	return 0;
}

