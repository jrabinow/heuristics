#Our solution assumes the newest version of numpy and scipy
#You will need gcc, gfortran, liblapack-dev-base, matlibplot, and cython
#This code was inspired by http://docs.scipy.org/doc/scipy-dev/reference/generated/scipy.spatial.Voronoi.html
#by Eric Schles

import numpy as np
from scipy.spatial import Voronoi, voronoi_plot_2d
import matplotlib.pyplot as plt

#setting up the board
board = [[0,0],[900,860],[500,47],[431,734]]
#we give it some initial points because we have to, otherwise the game freaks out
#We need a board for testing, so we use a 20 x 20, the real board should be 1000 x 1000
#xrange is used to give an iterator which uses far less memory

##################################################
#
# Testing functionality with all positions filled
##################################################

# for i in xrange(20):
#     for j in xrange(20):
#         board.append([j,i])
# points = np.array(board)
# vor = Voronoi(points)

# voronoi_plot_2d(vor)
# plt.show()

#setting up the game - for player to player testing
def greeting(board):
    print "Hello, and welcome to gravitational voronoi!"
    print "currently the board looks like this."
    points = np.array(board)
    vor = Voronoi(points)
    voronoi_plot_2d(vor)
    plt.show()
    return points

def player1(points,p1_stones_placed):
    x = int(raw_input("Please enter the x-coordinate for your move"))
    y = int(raw_input("Please enter the y-coordinate for your move"))
    vor = Voronoi(points,incremental=True) #needed or you can't add points
    move = np.array([[x,y]])
    vor.add_points(move)
    print "You're new move looks like this:"
    voronoi_plot_2d(vor)
    plt.show()
    p1_stones_placed += 1
    return move, p1_stones_placed

def player2(points,p2_stones_placed):
    x = int(raw_input("Please enter the x-coordinate for your move"))
    y = int(raw_input("Please enter the y-coordinate for your move"))
    print points
    vor = Voronoi(points,incremental=True) #needed or you can't add points
    move = np.array([[x,y]])
    vor.add_points(move)
    print "You're new move looks like this:"
    voronoi_plot_2d(vor)
    plt.show()
    p2_stones_placed += 1
    return move, p2_stones_placed

#######################################################
#
#Functions used to compute pull for color 'i'
#
#########################################################

## ((x2 - x1)^2 + (y2 - y1)^2)^1/2 = distance between two points
def euc_dist(x1,y1,x2,y2):
    return math.sqrt( ( (x2 - x1) ** 2 ) + ( (y2 - y1) ** 2 ) )

#used to compute the distances to a point x
def distance_to_x( list_of_points, point_x):
    list_of_distances_to_x = []
    for i in list_of_points:
        list_of_distances_to_x.append( euc_dist(i[0],i[1], x[0], x[1] ) )
        #assumes passed a list of lists points of the form [x,y]
    return list_of_distances_to_x

#used to compute the pull on a point x
def pull(list_of_distances_to_x):
    summation = 0
    for i in list_of_distances_to_x:
        summation += ( 1.0 / (i*i) )
    # 1.0 is used to avoid integer division
    return summation

#based on the number returned by pull, we calculate the color of the point x,
#we do this by taking the color with the greatest pull.

#this is an interface function for calling all of the above functions
def greatest_pull(x,placed_stones,list_of_colors):
    
    max_pull = 0
    color_with_greatest_pull = list_of_colors[0]
    for i in list_of_colors:
        list_of distances_x = []
        list_of_distances = distance_to_x(placed_stones, x)
        summation = pull(list_of_distances)
        if max_pull < summation:
            max_pull = summation
            color_with_greatest_pull = i
    return color_with_greatest_pull
            

################################################
#
# Functions for calculating the total area
#
#################################################

#the board will store all the current colors of a given point
def calculate_score(board):
    p1_sum = 0
    p2_sum = 0
    unalloc_sum = 0
    for i in xrange(20):
        for j in xrange(20):
            if board[j][i][2] == 'p1':
                p1_sum += 1
            if board[j][i][2] == 'p2':
                p2_sum += 1
            else:
                unallow_sum += 1
    return p1_sum, p2_sum, unalloc_sum


##Initialization of board and other variables
points = greeting(board)
p1_stones_placed = 0
p2_stones_placed = 0

#play game loop
give_up = False
while give_up == False:
    move , p1_stones_placed = player1(points, p1_stones_placed)
    points = np.append(points, move, axis=0)
    move , p2_stones_placed  = player2(points, p2_stones_placed)
    points = np.append(points, move, axis=0)
    give_up = raw_input("give up? (enter 0 for no or 1 for yes)")

## TO DO: ##


# Now points will have to have a color "p1" or "p2" as well as coordinates so we can keep track of who placed
# what stone.  Also we will need to update the board each time a new stone is played so that a master list is 
# maintained.  None of this is reflected in the main loop at present, but is easily added.

# So, things to do:

# make points = [x-coor, y-coor, color]
# make board = list of all points and their colors
# add the area and gravitional pull calculation functions to the main loop

# Then we will be ready to start writing strategies


# Write strategies for calculating moves
# --In this part we will create strategies that can be used to efficiently place stones
# --The goal is to get as much area as possible
# --Once this is done we will move onto playing with many players

# Once all of the above is done we move to java for the 1000 x 1000 board
