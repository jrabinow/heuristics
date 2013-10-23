"""
Initial condition:
P is initially at point (330, 200) and H at position (0,0).

possible

"""

import numpy as np
from parakeet import jit
from sys import argv
import math
import random



#hunter moves diagonally
#we can keep a list of walls within the hunter class

#we should have funcitons the minimize the the amount of area the prey can occupy
#The function should calculate the area that minimizes and 
#then return to place a wall vertically or horizontally

#we should have some functions that are used and depend on n
#If number of walls is 2 or below we use strictly binary search as long as we are more than 100 spaces away

#If the number of walls is between 5 and 10 we should use a more intelligent search.
#we can do 1 to binary search wall placements but then after that we should use "intelligent placement"

#intelligent placement:

#goal: minimize area available to prey,
#varibles: area available to prey, cost to hunter of placing wall = N, 
#relative distance between hunter and prey



#MOVEMENT FUNCTIONS


#get ride of sqrt if lacking in speed
def euc_dist(hunter_loc, prey_loc):
    return math.sqrt( ( (hunter_loc[0] - prey_loc[0]) ** 2) + ( (hunter_loc[1] - prey_loc[1]) ** 2) )

# walls is a list of lists of walls
# format: [x coordinate, y coordinate, veritical or horizontal]
# player_location is the current location of a given player
# format: [x coordinate, y coordinate]
# player_direction is the direction the player is attempting to move
# format: [x coordinate, y coordinate]
def bouncing( player_loc , player_direction, walls,is_hunter):
    
    x_original = player_loc[0]
    y_original = player_loc[1]
    for i in walls:
        player_loc[0] = player_loc[0] + player_direction[0]
        player_loc[1] = player_loc[1] + player_direction[1]
        if i[0] != player_loc[0] and i[1] != player_loc[1]:
            continue
        elif i[1] == player_loc[1] and i[2] == "horizontal":
            player_loc[1] = y_original
        elif i[0] == player_loc[0] and i[2] == "vertical":
            player[0] = x_original
    if is_hunter:
        return player_loc, [player_loc[0] - x_original, player_loc[1] - y_original]
    else:
        return player_loc

#AREA FUNCTIONS


#wall_loc is an a list of lists
#format: [ [x-coordinate for wall i, y-coordinate for wall i, vertical or horizontal for wall i], ]
#prey_loc is a list
#format: [x-coordinate of prey, y-coordinate of prey]
def calculate_prey_area(prey_loc, wall_loc):
    
    top_wall = 500
    bottom_wall = 0
    left_wall = 0
    right_wall = 500
#This handles the case of four imposed walls
    min_above = None
    min_below = None
    min_right = None
    min_left = None

    min_above_dist = 10000
    min_below_dist = 10000
    min_right_dist = 10000
    min_left_dist = 10000
    
    #impose an ordering on walls and only consider those closest to the prey
    #we are pruning any useless walls
    for i in wall_loc:
        if i[2] == 'vertical':
            if prey_loc[0] < i[0]:
                if i[0] - prey_loc[0] < min_right_dist:
                    min_right_dist = i[0] - prey_loc[0]
                    min_right = i
            if prey_loc[0] > i[0]:
                if prey_loc[0] - i[0] < min_left_dist:
                    min_left_dist = prey_loc[0] - i[0]
                    min_left = i
        if i[2] == 'horizontal':
            if prey_loc[1] < i[1]:
                if i[1] - prey_loc[1] < min_above_dist:
                    min_above_dist = i[1] - prey_loc[1]
                    min_above = i
            if prey_loc[1] > i[1]:
                if prey_loc[1] - i[1] < min_below_dist:
                    min_below_dist = prey_loc[1] - i[1]
                    min_below = i
    
    #calculate the available area for the prey
    min_wall_loc = [min_right,min_left,min_above, min_below]
    if min_wall_loc[2] != None:
        top_wall = min_wall_loc[2][1]
    else:
        min_wall_loc[2] = [0,top_wall,"horizontal"]
    if min_wall_loc[3] != None:
        bottom_wall = min_wall_loc[3][1]
    else:
        min_wall_loc[3] = [0,bottom_wall,"horizontal"]
    if min_wall_loc[1] != None:
        left_wall = min_wall_loc[1][0]
    else:
        min_wall_loc[1] = [left_wall,0, "vertical"]
    if min_wall_loc[0] != None:
        right_wall = min_wall_loc[0][0]
    else:
        min_wall_loc[0] = [right_wall,0,"vertical"]

    area = 0
    for i in min_wall_loc:
        if i[2] == 'vertical':
            if prey_loc[0] < i[0]:
                area1 = calc_area(i, "left", top_wall, bottom_wall, right_wall, left_wall)
            if prey_loc[0] > i[0]:
                area2 = calc_area(i, "right", top_wall, bottom_wall, right_wall, left_wall)
        if i[2] == 'horizontal':
            if prey_loc[1] < i[1]:
                area3 = calc_area(i, "below", top_wall, bottom_wall, right_wall, left_wall)
            if prey_loc[1] > i[1]:
                area4 = calc_area(i, "above", top_wall, bottom_wall, right_wall, left_wall)
    if area1 == area2 == area3 == area4:
        area = area1
    return area


#Here we handle each corner case for where the prey could be, explicitly
#the general formula is (right_wall - left_wall) * (top_wall - bottom_wall)
#It is possible that the only case we need is (right_wall - left_Wall) * (top_wall - bottom_wall)
def calc_area(wall_loc,where, top_wall, bottom_wall, right_wall, left_wall):
    if where == "left":
        if bottom_wall == 0 and left_wall == 0:
            return wall_loc[0] * top_wall
        if bottom_wall != 0 and left_wall == 0:
            return wall_loc[0] * (top_wall - bottom_wall)
        if bottom_wall == 0 and left_wall != 0:
            return (wall_loc[0] - left_wall) * top_wall
        if bottom_wall != 0 and left_wall != 0:
            return (wall_loc[0] -left_wall) * (top_wall - bottom_wall)

    if where == "right":
        if bottom_wall == 0:
            return (right_wall - wall_loc[0]) * top_wall
        if bottom_wall != 0:
            return (right_wall - wall_loc[0]) * (top_wall - bottom_wall)
        
    if where == "below":
        if bottom_wall == 0:
            return (right_wall - left_wall) * wall_loc[1]
        if bottom_wall != 0:
            return (right_wall - left_wall) * (wall_loc[1] - bottom_wall)
    
    if where == "above":
        if left_wall == 0:
            return right_wall * (top_wall - wall_loc[1])
        if left_wall != 0:
            return (right_wall - left_wall) * (top_wall - wall_loc[1])



#WALL FUNCTIONS
#NOTE: wall functions are not completely correct, need to take account for the following condition:

# When H creates a wall, the wall must touch the point where H was before H moved. The wall must be vertical or
# horizontal and must touch neither H nor P nor go through another wall (though it may touch another wall). 
# That is, a wall that would hit P if built is not built. After creating a wall, H must wait at least N time 
# steps before attempting to create another wall. It may be of any length. If H tries to create a wall that 
# violates any of these rules, the system will generate a message on the screen, will not build the wall, but 
# will not otherwise penalize H.


#Notes:
#calculate_prey_area has been tested and proven to work
#obvious speed ups: use numpy areas and jit, however we aren't dealing with large areas or a lot of elements
#per area, also all the calculations are minimal, therefore it may not be necessary to speed up

#this function determines the relative position of the hunter and prey
#it returns a list with two elements, one for the vertical and one for the horizontal relative placement
#format of input:
#prey: [x coordinate of prey, y coordinate of prey]
#hunter: [x coordinate of hunter, y coordinate of hunter]
#format of output:
#position: [left of hunter or right of hunter or at hunter, above hunter or below hunter or at hunter]
#position is similar to standard x,y coordinates except it only gives you a relative x or y position
def relative_location(prey,hunter):
    position =[None,None]
    #to the right means the prey is to the right of the hunter
    #to the left means the prey is to the left of the hunter
    #at means the prey is in the same horizontal position as the hunter
    if prey[0] < hunter[0]:
        position[0] = "left"
    if prey[0] == hunter[0]:
        position[0] = "at"
    if prey[0] > hunter[0]:
        position[0] = "right"
    
    #below means the prey is below the hunter
    #above means the prey is above the hunter
    #at means the prey is in the same vertical position as the hunter    
    if prey[1] < hunter[1]:
        position[1] = "below"
    if prey[1] == hunter[1]:
        position[1] = "at"
    if prey[1] > hunter[1]:
        position[1] = "above"
    
    return position


#minimization function
#we need to add in the relative position of the hunter and the prey
#if the prey is on the left of the hunter we should not add a wall on the left of the hunter
#the return value, best wall is the position of where the wall should be placed, such that this maximizes
#placement of a give wall
def optimize_wall_location(prey,hunter,walls,relative_position):
    left_of_hunter = [hunter[0] - 1, 0, "vertical"]
    right_of_hunter = [hunter[0] + 1, 0, "vertical"]
    above_hunter = [0, hunter[1] + 1, "horizontal"]
    below_hunter = [0, hunter[1] - 1, "horizontal"]
    best_wall = []
    min_area = 250000
    #if conditions ensure that a wall is not placed on the prey
    #relative position stops the hunter from placing a wall that would put a wall between
    #the hunter and the prey
    if relative_position[0] == "right" or relative_position[0] == "at":
        if prey[0] != left_of_hunter[0]:
            walls.append(left_of_hunter)
            min_area = calculate_prey_area(prey, walls)
            del walls[-1]
            best_wall.append(left_of_hunter)
    if relative_position[0] == "left":
        if prey[0] != right_of_hunter[0]:
            if min_area != 250000:
                walls.append(right_of_hunter)
                if calculate_prey_area(prey, walls ) < min_area:
                    min_area = calculate_prey_area(prey, walls )
                    del best_wall[0]
                    best_wall.append(right_of_hunter)
                    del walls[-1]
            else:
                walls.append(right_of_hunter)
                min_area = calculate_prey_area(prey, walls )
                del best_wall[0]
                best_wall.append(right_of_hunter)
                del walls[-1]

    if relative_position[1] == "below" or relative_position[1] == "at":
        if prey[1] != above_hunter[1]:
            if min_area != 250000:
                walls.append(above_hunter)
                if calculate_prey_area(prey, walls ) < min_area:
                    min_area = calculate_prey_area(prey, walls )
                    del best_wall[0]
                    best_wall.append(above_hunter)
                    del walls[-1]
            else:
                walls.append(above_hunter)
                min_area = calculate_prey_area(prey, walls )
                del best_wall[0]
                best_wall.append(above_hunter)
                del walls[-1]

    if relative_position[1] == "above":
        if prey[1] != below_hunter[1]:
            if min_area != 250000:
                walls.append(below_hunter)
                if calculate_prey_area(prey, walls ) < min_area:
                    min_area = calculate_prey_area(prey, walls )
                    del best_wall[0]
                    best_wall.append(below_hunter)
                    del walls[-1]
            else:
                walls.append(below_hunter)
                min_area = calculate_prey_area(prey, walls.append(below_hunter) ) 
                del best_wall[0]
                best_wall.append(below_hunter)
                del walls[-1]
    return best_wall[0]

hunter_loc = [0,0]
prey_loc = [330,200]
walls = []
game_not_over = True

# with live play uncomment following line:
#walls_available = argv[1] #number of walls that can be placed

# for testing uncomment following line:
random.seed()
walls_available = random.randint(4,20) 

#grab hunter 
answer = str(raw_input('player1: Are you the hunter? (y or n)\n'))

if answer == 'y':
    is_hunter = True
else:
    is_hunter = False


# this allows us to test our strategies for a given player against a random strategy
# play as hunter

# format for walls: [x coordinate, y coordinate, horizontal over veritcal
# for example walls = [ [0, 420, "horizontal"], [320,0,"vertical"] ]

counter = 0

while game_not_over:

    total_area = calculate_prey_area(prey_loc, walls)
    relative_pos = relative_position(prey_loc, hunter_loc)
    if (counter % walls_available) == 0:
        
        best_wall = optimize_wall_location(prey_loc, hunter_loc, walls,relative_pos) 
        if best_wall != []:
            walls.append(best_wall)
    
    new_x_hunter = hunter_loc[0] + 1
    new_y_hunter = hunter_loc[1] + 1
    hunter_loc = bouncing(hunter_loc, [new_x_hunter, new_y_hunter],walls, is_hunter)
    if (counter % 2) == 0:
        random.seed()
        new_x_prey = random.randint(-1,1)
        random.seed()
        new_y_prey = random.randint(-1,1)
        prey_loc = bouncing(prey_loc, [new_x_prey, new_y_prey], walls) 

#ends the game
    if euc_dist(hunter_loc, prey_loc) <= 4:
        game_not_over = False

            
        
