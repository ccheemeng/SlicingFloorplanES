---------- INPUT PARAMETERS ----------
<"seed:"> <seed>
<popSize> <numChildren> <sampleSize>
<x> <y>
<numRooms>

<distScale> <propScale> <posScale>

<name> <areaRel> <xRel> <yRel> <considerPos> <posX> <posY>

<room1Name>&<room2Name>

---------- PARAMETER DESCRIPTION ----------
General parameters
- long seed: omit line if not using a seed.
  If using a seed, ensure that the input line
  retains a space between the colon (":") and seed.
  "seed: 314" and not "seed:314"

- int popSize: number of individuals at the start of each generation

- int numChildren: number of children produced each generation.
  Each child is derived from two parents via cycle crossover.
  For each child, two parents are selected from a random sample
  of the population via tournament selection.
  All children will form the next generation, with the remaining
  spots filled by a random sample of the previous generation.
  Thus numChildren == popSize will imply a complete replacement
  of the previous generation.
  Should be > 0.
  Must be <= popSize.

- int sampleSize: the size of the random sample on which
  tournament selection is performed to derive the parents
  for a child.
  Must be <= popSize.

- double x: the absolute horizontal dimension of the plot.

- double y: the absolute vertical dimension of the plot.

- int numRooms: the number of rooms the solver should handle.

Cost function scale parameters
- the sum of the following three scale parameters should be > 0.

- int distScale: the distance cost is the average topological distance
  of one room from another as specified in the connection parameters.
  distScale is the relative influence this distance cost has on
  the overall cost function.

- int propScale: the proportion cost is a function of how differently
  a particular individual's rooms align with the desired relative
  dimensions specified by xRel and yRel.
  propScale is the relative influence this proportion cost has on
  the overall cost function.

- int posScale: the position cost is a function of how far a
  particular individual's rooms are from the desired positions
  specified by posX and posY.
  posScale is the relative influence this position cost has on the
  overall cost function.

Room parameters
- the number of lines of the following seven parameters should be
  == numRooms.

- String name: the name of a particular room.

- double areaRel: the relative area of the room.

- double xRel: the relative horizontal dimension of the room.
  Set this or yRel to 0 to cause the solver to ignore this particular
  room's proportions.

- double yRel: the relative vertical dimension of the room.
  Set this or xRel to 0 to cause the solver to ignore this particular
  room's proportions.

- String considerPos: set to any variation of "true" (not case
  sensitive) to have the solver consider this particular room's
  proximity to its desired position specified by posX and posY.

- double posX: the desired horizontal position of the room.
  May be outside of the plot's horizontal bounds.

- double posY: the desired vertical position of the room.
  May be outside of the plot's vertical bounds.

Connection parameters
- For an adjacency between any two given rooms, one one-way connection
  specification is sufficient to define an adjacency. Defining
  multiple similar adjacencies will bias the solver to prioritise that
  particular adjacency.

- String room1Name: the name of the room that should connect to the
  room specified by room2Name.
  Must be equal to an instance of name.

- String room2Name: the name of the room that should connect to the
  room specified by room1Name.
  Must be equal to an instance of name.

---------- EXAMPLE INPUT ----------
seed: 314
35 7 5
13000 8000
11

1 0 1

livingA 11 0 0 false 0 0
livingB 8 0 0 false 0 0
entry 4 2 3 true 6500 0
dining 10 0 0 false 0 0
kitchen 10 0 0 false 0 0
eating 10 0 0 false 0 0
hallway 5 0 0 false 0 0
mBedroom 10 0 0 true 0 8000
mBathroom 4 1 1 false 0 0
bedroom 10 0 0 true 13000 8000
bathroom 4 1 1 false 0 0

entry&livingB
livingA&livingB
livingB&dining
livingA&eating
livingA&hallway
dining&kitchen
eating&kitchen
hallway&bedroom
hallway&bathroom
hallway&mBedroom
mBedroom&mBathroom