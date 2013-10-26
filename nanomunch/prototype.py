"""
This code implements the nanomunching game.
Prototype by
Eric Schles

we use polar coordinates so we have an obvious notion of up, down, right, left as denoted by r.

"""
import math
import numpy as np

def convert_to_polar(id, xloc, yloc):
    r = math.sqrt( (xloc ** 2) + (yloc ** 2) )
    theta = np.arctan2(yloc, xloc) * 180/ np.pi
    return id, r, theta

"""
Code to determine up, down, right, left

if 0 <= theta <= 45 go right or if 292.5 < theta <= 360 go right
if 45 < theta <= 112.5 go up
if 112.5 < theta <= 202.5 go left
if 202.5 < theta <= 292.5 go down

"""

"""
For nodes:

We read in the connections for each node and keep a list of nodes's and the directions you can go.
To do this:

We do this by recording the theta's associated with each node edge
We should do this by updating the data structure.  
Then the munchers will simply read in data from the structure.
Then the munchers will update the data structure's state.

This may be slow, the better way to do this may be to treat munchers like packets,
keeping as much intelligence as possible outside of the network.
"""

#reading in the data
raw_data = open("data.txt", "r")
raw_data.readline() #strip the header

for i in raw_data:
    marker = False
    if not i == "\n":
        i = i.split(",")
    i


graph = {}
# structure is id: (xloc, yloc)
#I'm not sure exactly why I need this, but I'm sure I do.

link_map = {}
# structure is id: (up_id, down_id, left_id, right_id)

road_map = {}
# structure is id: (has up, has down, has left, has right, eaten, has_muncher)
# Each are booleans that are either true or false, 
# if has up is true then a muncher can go up, etc.
# if eaten is true, then the node has already been eaten
# if has muncher is true, then a muncher is already eating this node.

class muncher:
    #xloc, yloc here represent the initial location of the muncher
    def __init__(self, id):
        self.id = id

    def move_up(self,current_id, road_map, link_map):
        if road_map[current_id][0] and road_map[current_id][4] and road_map[current_id][5]:
            self.id = link_map[id][0] 
            road_map.update( {self.id:(road_map[self.id][0],road_map[self.id][1],road_map[self.id][2],road_map[self.id][3], True, True)} )
            return road_map
        #we update the road_map, so that the next node is eaten once a muncher lands on it for the first time.

    def move_down(self,current_id, road_map, link_map):
        if road_map[current_id][1] and road_map[current_id][4] and road_map[current_id][5]:
            self.id = link_map[id][1]
            road_map.update( {self.id:(road_map[self.id][0],road_map[self.id][1],road_map[self.id][2],road_map[self.id][3],True, True)} )
            return road_map

    def move_left(self,current_id, road_map, link_map):
        if road_map[current_id][2] and road_map[current_id][4] and road_map[current_id][5]:
            self.id = link_map[id][2]
            road_map.update( {self.id:(road_map[self.id][0],road_map[self.id][1],road_map[self.id][2],road_map[self.id][3],True, True)} )
            return road_map

    def move_right(self,current_id, road_map, link_map):
        if road_map[current_id][3] and road_map[current_id][4] and road_map[current_id][5]:
            self.id = link_map[id][3]
            road_map.update( {self.id:(road_map[self.id][0],road_map[self.id][1],road_map[self.id][2],road_map[self.id][3],True, True)} )
            return road_map



#def main_loop():
    
