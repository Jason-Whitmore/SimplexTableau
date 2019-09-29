# Simplex-Tableau

This project implements the Simplex algorithm that optimizes a linear program that is in a canonical form matrix tableau.

## Linear Optimization

The field of linear optimization deals with optimizing systems of linear equations subject to a linear objective function. Although linear systems seem simple, they are fairly applicable to many practical problems, including economic and business interpretations. 

### Linear programs

A certain linear optimization problem can be described as a linear program (or LP). For this project, and for the Simplex algorithm, linear programs are contained within a Tableau: a partitioned matrix which supplies all of the information needed to optimize the LP.

Before the matrix can be created, the relevant constraints to the LP should be described using inequalities. For example, an economic problem, like maximizing profit, will be used. Constraints are written in the form B > c_1,r * x_1 + ... + c_n * x_n for n resources, where B is the maximum available amount of a resource, and c_i is the amount of the resource that one unit of x_i uses.

Let's say that you are running a lemonade stand that sells normal lemonade and strawberry lemonade. For a certain resource, like sugar, you have 1 kg in your inventory. A cup of normal lemonade requires 0.01 kg of sugar, and a cup strawberry lemonade requires 0.005 kg of sugar. The equation would then be 1 > 0.01 * x_1 + 0.005 * x_2, where x_1 and x_2 are how many cups of each type of drink can make.

Additional constraints:

Lemons: 20 > 1.5 * x_1 + x_2 (normal lemonade needs 1.5 lemons, strawberry needs 1, total of 20 lemons in inventory)

Strawberries: 50 > 6 * x_2 (normal lemonade uses 0 strawberries, strawberry lemonade needs 6, total of 50 strawberries in inventory)

Additionally, there is a implied constraint that both x_1 and x_2 must be greater to or equal to zero, since make negative cups of lemonade makes no sense.


For the objective function, we simply put the profit of each drink as the scalars in the linear equation. So if one cup of normal lemonade makes 1 dollar profit, and one cup of strawberry lemonade makes 1.1 dollars in profit, our objective function would be Z(x_1,x_2) = x_1 + 1.1 * x_2

#### Visualized

If you plot these inequalities for this problem, you get the following:

![alt text](https://github.com/Jason-Whitmore/SimplexTableau/blob/master/feasible.png "Feasible region")

The area bounded by the polygon is called the feasible region. As implied, that area represents the possible combinations of x_1 and x_2. Of course, there are plenty of configurations that that don't "max out" a resource, namely anything that isn't on the edges. The simplex algorithm will walk along the edges of the feasible region to each corner, where the optimal solution will be found

#### Tableau

In order to feed the linear program into the simplex algorithm correctly, we need to put the LP into a canonical form tableau. As mentioned before, a tableau is a partitioned matrix with the format:

|Z|C|
|----|----|
|B|A|


Where Z is the objective function value (in our case, total profit), C is the transposed vector containing the scalars that constribute to the objective function value (profit per type of drink), B is the vector containing the constraint constants for each type of resource (amount of a resource in inventory), and A is the matrix containing how much resource of each is used per x_i.

Additionally, since canonical forms rely on equalities instead of inequalities, we need to introduce "slack" variables to account for resources not used so that the equation is now an equality. In our sugar resource, it would then be 1 = 0.01 * x_1 + 0.005 * x_2 + x_3, where x_3 is the sugar we didn't use. Consequently, the objective function puts a 0 scalar in front of x_3, since we don't earn any money for sugar left in inventory.

Overall, canonical forms require all the column of an indentity matrix of size r - 1, where r is the number of rows in the tableau. In those columns, the objective function scalar must also be 0. It also requires all the values in the B vector to be nonnegative.

Our lemonade stand tableau would then become:


|0|-1|-1.1|0|0|0|
|----|----|----|----|----|----|
|1|0.01|0.005|1|0|0|
|20|1.5|1|0|1|0|
|50|0|6|0|0|1|


Notice that in the objective function row, the profits of 1 and 1.1 are negative. This is because a tableau technically minimizes an objective function, so we have to trick it by multiplying those constants by -1. This may also mean that the objective function value may be negative, depending on your application, and should also be multiplied by -1.

#### Simplex algorithm
Once the canonical form tableau has been created, it can be fed into the program as a text file, with each row being separated by a newline character, and each entry in the row separated with a space. The program will check to see if it is in the proper canonical form, then perform the simplex algorithm through a series of carefully chosen pivot operations. Once complete, the program will give the objective function value, in addition to the x_i values that produce that objective function.

Running the algorithm on the lemonade stand example produces the following results:

Objective function value: 16.944

x_1 = 7.777

x_2 = 8.333

x_3 = 0.8805

x_4 = 0.0

x_5 = 0.0

This is interpreted as follows: Under the constraints, making 7.7 cups of normal lemonade and 8.3 cups of strawberry lemonade will produce a maximized profit of 16.94 dollars. With this configuration, there will be .88 kg of sugar remaining in inventory, while all the lemons and strawberries have been used up.


### Use

Of course, you can use the program as described above, but the program is also designed to be used as a library for another program. In that case, you  can remove the main function at the bottom of the file, and then simply use the other constructor which takes in the tableau as a 2d array of doubles.

