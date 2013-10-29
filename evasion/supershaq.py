#!/usr/bin/env python
'''
Created on Oct 27, 2013

@author: shaqal
'''
import sys
import socket
import math 
#from lxml.html.builder import PRE  # dafuq???
from parakeet import jit

# Here the communication with server part starts! 'def' is used to define a function.

def readdata(sock):
    inpData = ''
    numLE = 7
    while True:
        chunk = sock.recv(maxlen)
        if not chunk: break
        if chunk == '':
            raise RuntimeError("socket connection broken")
        inpData = inpData + chunk
        lines = inpData.split('\n')
        if inpData.count("\n") >1:
            numWalls = int(lines[1])
            if inpData.count("\n") == numLE + numWalls:
                break

    inpData = inpData.strip()
    serversaid(inpData[:200])
    return inpData.split('\n')

def senddata(sock, msg):
    msg = msg + '\n'
    totalsent = 0
    MSGLEN = len(msg)
    while totalsent < MSGLEN:
        sent = sock.send(msg[totalsent:])
        if sent == 0:
            raise RuntimeError("socket connection broken")
        totalsent = totalsent + sent
    isaid(msg)

def serversaid(msg):
    print("Server: %s" % msg[:200])
def isaid(msg):
    print("Client: %s" % msg[:200])

teamname = "SuperShaq"
maxlen = 999999
dim = 500
PORT = 4567
ROLE = 'H'

if len(sys.argv)>1:
        ROLE = sys.argv[1]
        PORT = int(sys.argv[2])

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(('127.0.0.1', PORT))

chunk = s.recv(maxlen)
if chunk =="Team Name?\n":
    print "Server : Team Name?"
    senddata(s,teamname)

chunk = (s.recv(maxlen)).split()

nextWallStep = int(chunk[0])
totalNumWalls = int(chunk[1])

#print nextWallStep, totalNumWalls

#global variable set!
wallGrid = [[0 for i in range(0,500)] for j in range(0,500)]
wallList = {}
numWalls = 0
movesToNextW = 1
preyLoc = "(330,200)"
px,py = preyLoc[1:-1].split(',')
hunterLoc = "(0,0)"
hx,hy = hunterLoc[1:-1].split(',')
hunterDir = ('SE')
hx = int(hx)
hy = int(hy)
px = int(px)
py = int(py)

verticalWalls=0
horizontalWalls=0

#here the communication part ends ( a little more at the end of the program )

@jit
def updateWallGrid(x1,y1,x2,y2,add_or_remove):
    global wallGrid

    if add_or_remove == 1:
        if y1 == y2:
            for i in xrange(x1,x2+1):
                wallGrid[y1][j]=1

        if x1 == x2:
            for i in xrange(y1,y2+1):
                wallGrid[x1][i]=-1

    if add_or_remove == -1:
        if y1 == y2:
            for i in xrange(x1,x2+1):
                wallGrid[y1][j]=0

        if x1 == x2:
            for i in xrange(y1,y2+1):
                wallGrid[x1][i]=0

#this is the function that takes the raw data sent by the server and puts it into variables!
def updateBoard(data):
    global wallList
    global numWalls
    global movesToNextW
    global preyLoc
    global hunterLoc
    global hunterDir
    global hx
    global hy
    global px
    global py

    numWalls = int(data[1])
    newWallList = {}
    for i in xrange(0,numWalls):
        wallNum = data[2+i][:data[2+i].find(' ')]
        startP = data[2+i][data[2+i].find('(')+1:data[2+i].find(')')].split(',')
        endP = data[2+i][data[2+i].rfind('(')+1:data[2+i].rfind(')')].split(',')
        newWallList[wallNum] = (startP,endP)
        #print newWallList

    wallList = newWallList
    movesToNextW = int(data[numWalls+3])
    temp, hunterDir, hunterLoc = data[numWalls+4].split()
    temp, preyLoc = data[numWalls+5].split()
    hx,hy = hunterLoc[1:-1].split(',')
    px,py = preyLoc[1:-1].split(',')
    hx = int(hx)
    hy = int(hy)
    px = int(px)
    py = int(py)

@jit
def removeLeastImportantWall():

    maxw = '0'
    global verticalWalls
    global horizontalWalls

    if verticalWalls > horizontalWalls:
        farthestX = -1
        verticalWalls-=1
        for k, v in wallList.iteritems():
            if v[0][0]==v[1][0]:
                m=0
                if hx > int(v[0][0]):
                    for i in xrange(int(v[0][0]),hx+1):
                        if wallGrid[i][hy] == -1:
                            m+=1
                    if m>farthestX:
                        farthestX=m
                        maxw=k
                else:
                    for i in xrange(hx,int(v[0][0])+1):
                        if wallGrid[i][hy] == -1:
                            m+=1
                    if m>farthestX:
                        farthestX=m
                        maxw=k

    else:
        farthestY = -1
        horizontalWalls-=1
        for k, v in wallList.iteritems():
            if v[0][1]==v[1][1]:
                m=0
                if hy > int(v[0][1]):
                    for i in xrange(int(v[0][1]),hy+1):
                        if wallGrid[hx][i] == 1:
                            m+=1
                    if m>farthestY:
                        farthestY=m
                        maxw=k
                else:
                    for i in xrange(hy,int(v[0][1])+1):
                        if wallGrid[hx][i] == 1:
                            m+=1
                    if m>farthestY:
                        farthestY=m
                        maxw=k
    rem = wallList[maxw]
    for i in xrange(int(rem[0][0]),int(rem[1][0])+1):
        for j in xrange(int(rem[0][1]),int(rem[1][1])+1):
            wallGrid[i][j]=0

    return senddata(s, hunterDir+'wx'+maxw)

def movingTowardsPreyH():
    if ((hx < px) and (hunterDir == 'SE' or hunterDir == 'NE')) or\
        ((hx > px) and (hunterDir == 'SW' or hunterDir == 'NW')) :
        return True
    else:
        return False

def movingTowardsPreyV():
    if ((hy < py) and (hunterDir == 'SE' or hunterDir == 'SW')) or\
        ((hy > py) and (hunterDir == 'NW' or hunterDir == 'NE')) :
        return True
    else:
        return False

def wallBetweenHandPV():
    if(hx>px):
        for i in xrange(px,hx):
            if wallGrid[i][hy]==-1:
                return True
    else:
        for i in xrange(hx,px):
            if wallGrid[i][hy]==-1:
                return True

    return False

def wallBetweenHandPH():
    if(hy>py):
        for i in xrange(py,hy):
            if wallGrid[hx][i]==1:
                return True
    else:
        for i in xrange(hy,py):
            if wallGrid[hx][i]==1:
                return True

    return False

@jit
def gapInH():
    if hunterDir=='NE' or hunterDir=='SE':
        i=0
        while True:
            if wallGrid[hx+i][hy]==0 and hx + i < 499:
                i+=1
            else:
                break
        return i

    if hunterDir=='NW' or hunterDir=='SW':
        i=0
        while True:
            if wallGrid[hx-i][hy]==0 and hx - i > 0:
                i+=1
            else:
                break
        return i

@jit
def gapInV():
    if hunterDir=='SE' or hunterDir=='SW':
        i=0
        while True:
            if wallGrid[hx][hy+i]==0 and hy + i < 499:
                i+=1
            else:
                break
        return i

    if hunterDir=='NE' or hunterDir=='NW':
        i=0
        while True:
            if wallGrid[hx][hy-i]==0 and hy - i > 0:
                i+=1
            else:
                break
        return i

def buildHorizontalWall(x1,x2):
    global horizontalWalls

    minX = 0;
    maxX = 499;
    for i in xrange(hx,0,-1):
        if wallGrid[i][hy]==0:
            minX=i
        else:
            break
    for i in xrange(hx,500):
        if wallGrid[i][hy]==0:
            maxX=i
        else:
            break

    if x1>minX:
        minX=x1

    if x2<maxX:
        maxX=x2

    for i in xrange(minX,maxX+1):
        wallGrid[i][hy]=1

    horizontalWalls+=1
    senddata(s,hunterDir+'w('+str(minX)+','+str(hy)+'),('+str(maxX)+','+str(hy)+')')

def buildVerticalWall(y1,y2):
    global verticalWalls

    #if wallGrid[hx][hy]
    minY = 0;
    maxY = 499;
    for i in xrange(hy,0,-1):
        if wallGrid[hx][i]==0:
            minY=i
        else:
            break
    for i in xrange(hy,500):
        if wallGrid[hx][i]==0:
            maxY=i
        else:
            break

    if y1>minY:
        minY=y1

    if y2<maxY:
        maxY=y2

    for i in xrange(minY,maxY+1):
        wallGrid[hx][i]=-1

    verticalWalls+=1
    senddata(s,hunterDir+'w('+str(hx)+','+str(minY)+'),('+str(hx)+','+str(maxY)+')')

def checkAndBuildWall():
    if movingTowardsPreyH() and movingTowardsPreyV():
        if abs(hx - px) < ((3*nextWallStep)/2 +2) and abs(hy - py) < 2*((3*nextWallStep)/2 +2):
            buildVerticalWall(0,499)
        elif abs(hy - py) < ((3*nextWallStep)/2 +2) and abs(hy - py) < 2*((3*nextWallStep)/2 +2):
            buildHorizontalWall(0,499)
        elif abs(hx - px) <= 2 and not wallBetweenHandPH():
            buildVerticalWall(0,499)
        elif abs(hy - py) <= 2 and not wallBetweenHandPV():
            buildHorizontalWall(0,499)
        else:
            senddata(s, hunterDir)
    elif movingTowardsPreyH() and not movingTowardsPreyV():
        if not wallBetweenHandPH() and abs(hx - px) < ((3*nextWallStep)/2 +2):
            buildVerticalWall(0,499)
        else:
            senddata(s, hunterDir)
        #elif not wallBetweenHandPH() and abs(hx - px) > ((3*nextWallStep)/2 +2):
            #buildHorizontalWall()

    elif not movingTowardsPreyH() and movingTowardsPreyV():
        if not wallBetweenHandPV() and abs(hy - py) < ((3*nextWallStep)/2 +2):
            buildHorizontalWall(0,499)
        else:
            senddata(s, hunterDir)
        #elif not wallBetweenHandPV() and abs(hy - py) > ((3*nextWallStep)/2 +2):
            #buildVerticalWall()

    else:
        senddata(s, hunterDir)
        '''if not wallBetweenHandPH() and wallBetweenHandPV():
            buildHorizontalWall(0,499)
        elif wallBetweenHandPH() and not wallBetweenHandPV():
            buildVerticalWall(0,499)
        elif not wallBetweenHandPH() and not wallBetweenHandPV():
            if gapInH() > gapInV():
                buildVerticalWall(0,499)
            else:
                senddata(s, hunterDir)
        else:'''

def calcHunterMove():

    if movesToNextW > 0:
        if numWalls == totalNumWalls:
            removeLeastImportantWall()
        else:
            senddata(s, hunterDir)

    else:
        checkAndBuildWall()

upCounter =0
rightCounter =0
downCounter =0
leftCounter =0
presentMotion =0
counterTo10 = 0
wallGrid2 = [[0 for i in xrange(0,500)] for j in xrange(0,500)]

def calcPreyMove():

    global upCounter
    global rightCounter
    global downCounter
    global leftCounter
    global presentMotion
    global wallGrid2
    for k, v in wallList.iteritems():

            if v[0][0]==v[1][0]:
                x= int(v[0][0])
                for i in range(int(v[0][1]),int(v[1][1])):
                    wallGrid2[x][i]=-1

            else:
                y= int(v[0][1])
                for i in range(int(v[0][0]),int(v[1][0])):
                    wallGrid2[i][y]=1

    if presentMotion == 0:
        for i in range(1,11):
            if wallGrid2[px][py-i] != 0:
                presentMotion+=1
                presentMotion%=4

        if presentMotion == 0:
            senddata(s, "NW")
            return None


    if presentMotion == 1:
        for i in range(1,11):
            if wallGrid2[px-i][py] != 0:
                presentMotion+=1
                presentMotion%=4

        if presentMotion == 1:
            senddata(s, "SW")
            return None

    if presentMotion == 2:
        for i in range(1,11):
            if wallGrid2[px][py-i] != 0:
                presentMotion+=1
                presentMotion%=4

        if presentMotion == 2:
            senddata(s, "SE")
            return None

    if presentMotion == 3:
        for i in range(1,11):
            if wallGrid2[px-i][py] != 0:
                presentMotion+=1
                presentMotion%=4

        if presentMotion == 3:
            senddata(s, "NE")
            return None

    if presentMotion == 0:
        senddata(s, "ZZ")
        return None



##play hunter mode
if ROLE == 'H':
    while True:
        updateBoard(readdata(s))
        #senddata(s, "NE")
        calcHunterMove()
        print "v:" + str(verticalWalls),"h:" + str(horizontalWalls)

##play prey mode
else :
    while True:

        updateBoard(readdata(s))
        #senddata(s, "NE")
        calcPreyMove()
        counterTo10+=1
        if counterTo10 ==100:
            counterTo10=0
            presentMotion+=1
            if presentMotion ==4:
                presentMotion=0



