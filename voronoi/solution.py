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

def player1(points):
    x = int(raw_input("Please enter the x-coordinate for your move"))
    y = int(raw_input("Please enter the y-coordinate for your move"))
    vor = Voronoi(points,incremental=True) #needed or you can't add points
    move = np.array([[x,y]])
    vor.add_points(move)
    print "You're new move looks like this:"
    voronoi_plot_2d(vor)
    plt.show()
    return move

def player2(points):
    x = int(raw_input("Please enter the x-coordinate for your move"))
    y = int(raw_input("Please enter the y-coordinate for your move"))
    print points
    vor = Voronoi(points,incremental=True) #needed or you can't add points
    move = np.array([[x,y]])
    vor.add_points(move)
    print "You're new move looks like this:"
    voronoi_plot_2d(vor)
    plt.show()
    return move



points = greeting(board)

give_up = False
while give_up == False:
    move = player1(points)
    points = np.append(points, move, axis=0)
    move = player2(points)
    points = np.append(points, move, axis=0)
    give_up = raw_input("give up? (enter 0 for no or 1 for yes)")
