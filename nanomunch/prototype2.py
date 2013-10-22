"""
This code implements the nanomunching game.
Prototype by
Eric Schles

we use polar coordinates so we have an obvious notion of up, down, right, left as denoted by r.

"""
import math
import numpy as np


"""
Code to determine up, down, right, left

if 0 <= theta <= 45 go right or if 292.5 < theta <= 360 go right
if 45 < theta <= 112.5 go up
if 112.5 < theta <= 202.5 go left
if 202.5 < theta <= 292.5 go down

"""


def what_direction(loc1,loc2):
    #an array that tells us which way we can go
    #right,up,left,down
    can_go = [0,0,0,0]

    loc2 = relative_location(loc1,loc2)              #relative direction tells us if our
                                                     #destination is above, below, left or right
    id, r, theta = convert_to_polar(loc2)            #We convert to polar so that we can get a feel for this
                                                     #numerically

    if 0 <= theta <= 45 or 292.5 < theta <= 360:     #go right
        can_go[0] = 1
    if 45 < theta <= 112.5:                          #go up
        can_go[1] = 1
    if 112.5 < theta <= 202.5:                       #go left
        can_go[2] = 1
    if 202.5 < theta <= 292.5:                       #go down
        can_go[3] = 1
    
    return can_go


def relative_location(start_position, end_position):
    x1 = start_position[0]
    y1 = start_position[1]
    x2 = end_position[0]
    y2 = end_position[1]
    
    if x1 < x2 and y1 < y2:
        return end_position
    if x1 < x2 and y1 > y2:
        y2 = -y2
        return [x2,y2]
    if x1 > x2 and y1 < y2:
        x2 = -x2
        return [x2, y2]
    if x1 > x2 and y1 > y2:
        x2 = -x2
        y2 = -y2
        return [x2,y2]
    if x1 == x2 and y1 < y2:
        return end_position
    if x1 == x2 and y1 > y2:
        y2 = -y2
        return [x2,y2]
    if x1 > x2 and y1 == y2:
        x2 = -x2
        return [x2, y2]
    if x1 > x2 and y1 > y2:
        x2 = -x2
        y2 = -y2
        return [x2,y2]
    
def convert_to_polar(loc):
    r = math.sqrt( (loc[0] ** 2) + (loc[1] ** 2) )
    theta = np.arctan2(yloc, xloc) * 180/ np.pi
    return id, r, theta


#bringing the data into memory
raw_data = open("data.txt", "r")

#format of location: id, x-coordinate, y-coordinate
location = []
#format of links: id one <--> id two   
links = []
first_mark = False
second_mark = False

for i in raw_data:
    
    if "nodeid,xloc,yloc" in i:
        first_mark = True
        
    if "nodeid1,nodeid2" in i:
        second_mark = True
        first_mark = False     
    
    if first_mark and i != '\n':
        i = i.split(",")
        if i[0] != 'nodeid':
            location.append([int( i[0] ) , int(i[1]),int(i[2].strip()) ])
        
    if second_mark and i != '\n':
        i = i.split(",")
        if i[0] != 'nodeid1':
            links.append([int(i[0]),int( i[1].strip() ) ])


#we need to add a some markers to our imported data.

#all not eatan is a variable which tells us if all the nodes have been eaten,
#once all the nodes have been eaten the game ends.

all_not_eaten = False

#for every node in the graph, create a list of all the places there are one hop away
#this list comes from links
where_we_can_go = {}

#since this is bidirectional, we'll need to do this for links.reverse as well
def one_away_links(links,where_we_can_go):
    for i in links:
    #add the start link
        if not i[0] in where_we_can_go:
            where_we_can_go.update({i[0]:[]})
        for j in where_we_can_go:
            if i[0] == j:
                where_we_can_go[j].append( i[1] )
    return where_we_can_go

where_we_can_go = one_away_links(links,where_we_can_go)   

for i in links:
    i.reverse() 
#reverse because links are bidirectional

where_we_can_go = one_away_links(links, where_we_can_go)



eaten = {}

#a list of all eaten nodes,
#0 means not eaten
#1 means eaten
#format: id:boolean - 0 or 1 (not eaten or eaten)

for i in where_we_can_go:
    eaten.update({i:0})

def check_eaten(eaten):
    all_eaten = True
    for i in eaten:
        if eaten[i] == 0:
            all_eaten = False
    return all_eaten

all_not_eaten = True

up_down_right_left = [1,0,0,0]


# ToDo:

# The main loop found here: http://cs.nyu.edu/courses/fall13/CSCI-GA.2965-001/nanomunchers.html
# The muncher selection optimization
# The functions for eating a node
# The functions for determining which nanomuncher lives


# while all_not_eaten:
    
    
#     #check to see if we should keep going
#     if check_eaten(eaten):
#         all_not_eaten = False
    


        
    
