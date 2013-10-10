'''
Created on Oct 10, 2013

@author: shaqal
'''

import math
import random
import sys

import matplotlib.pyplot as plt
import numpy as np


turns = 10
board_size = 100

# using a 1D array to use the point enumeration instead of position. 
# for a[x] => a[i][j] , i = floor(i/a) and j = floor(j%x) 

# 0 for Green, 1 for red, 2 for blue
boardRBG = [0 for i in xrange(0, board_size ** 2)] 

boardPull = [0 for i in xrange(0, board_size ** 2)]

stonesByR = []
stonesByB = []

def dist(x1, y1, x2, y2):
    return math.sqrt(((x2 - x1) ** 2) + ((y2 - y1) ** 2))

# used to compute the pull by a point x on the board
def pullOnMatrixByX(x):
    pullByX = [0 for i in xrange(0,board_size ** 2)]
    
    distanceToXList = [(dist(math.floor(i / board_size),\
                              i % board_size, \
                              math.floor(x / board_size),\
                               x % board_size)) \
                       for i, val in enumerate(boardRBG)]
    
    for i,val in enumerate(distanceToXList):
        if val == 0:
            pullByX[i]= 10000;
            continue
        pullByX[i]= (10000 / (val * val))  # 1.0 is used to avoid integer division
       
    return pullByX

# total pull recalculated Note: can do this more efficiently by adding the pull by just the new stone in each turn
def finalPullList():
    
    pull = [0 for i in xrange(0,board_size ** 2)]
    #pull1 = [0 for i in xrange(0,board_size ** 2)]
    #pull2 = [0 for i in xrange(0,board_size ** 2)]
    
    for r in stonesByR:
        ptemp = pullOnMatrixByX(r)
        pull =[pull[i]+ ptemp[i] for i in xrange(0,board_size**2)]
        #pull1 =[ptemp[i] for i in xrange(0,board_size**2)]
    for b in stonesByB:
        ptemp = pullOnMatrixByX(b)
        pull =[pull[i]- ptemp[i] for i in xrange(0,board_size**2)]
        #pull2 =[-1*ptemp[i] for i in xrange(0,board_size**2)]
    
    #pull =[pull1[i] +pull2[i] for i in xrange(0,board_size**2) ]
    
    print "pull"
    print pull
    
    return pull

# players move by placing stones at a index in row major array



def player1():
    #x = int(raw_input("Please enter the x-coordinate for your move"))
    #y = int(raw_input("Please enter the y-coordinate for your move"))

    #your strategy1 Goes here
    x = random.randint(0,board_size)
    y = random.randint(0,board_size)
    print str(x)+" "+str(y)
    return (x,y)

def player2():
    #x = int(raw_input("Please enter the x-coordinate for your move"))
    #y = int(raw_input("Please enter the y-coordinate for your move"))
   
    #your strategy2 goes here
    x = random.randint(0,board_size)
    y = random.randint(0,board_size)
    print str(x)+" "+str(y)
    return (x,y)

# used only for display purpose
def drawBoard():

    gBoard = np.asarray([i for i, val in enumerate(boardRBG) if val == 0])
    rBoard = np.asarray([i for i, val in enumerate(boardRBG) if val == 1])
    bBoard = np.asarray([i for i, val in enumerate(boardRBG) if val == 2])
      
    plt.plot(np.floor(gBoard / board_size), np.floor(gBoard % board_size), 'go', \
    np.floor(rBoard / board_size), np.floor(rBoard % board_size), 'ro', \
    np.floor(bBoard / board_size), np.floor(bBoard % board_size), 'bo')
    plt.show()

def main(argv):
    #turns = int(raw_input("Number of turns per player"))
    #drawBoard()
    
    global boardPull
    global boardRBG
    global stonesByR
    global stonesByB
    
    for i in xrange(0,turns):
        x,y = player1()
        stonesByR.append(x*board_size+y)
        
        #Update pull
        boardPull = finalPullList()
        
        #Update board colors
        for i in xrange(0,board_size**2):
            if boardPull[i]>0:
                boardRBG[i]=1
            elif boardPull[i]<0:
                boardRBG[i]=2
            else:
                boardRBG[i]=0
                
        #drawBoard()
        
        x,y = player2()
        stonesByB.append(x*board_size+y)
        
        #Update pull
        boardPull = finalPullList()
        
        #Update board colors
        for i in xrange(0,board_size**2):
            if boardPull[i]>0:
                boardRBG[i]=1
            elif boardPull[i]<0:
                boardRBG[i]=2
            else:
                boardRBG[i]=0
        #drawBoard()
    drawBoard()

if __name__ == "__main__":
    main(sys.argv)
    
