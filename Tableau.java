import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


/**
 * Class that provides the data structure and functionality to load and solve canonical form linear programs.
 */
public class Tableau{

    /**
     * The primary structure to hold the linear program inside of a 2d array or matrix.
     */
    private double[][] tableau;

    /**
     * The number of rows in the tableau.
     */
    private int numRows;

    /**
     * The number of columns in the tableau.
     */
    private int numCols;



    /**
     * Constructor for a tableau that takes in an external text file for input.
     * 
     * @param fileName Text file containing the tableau in canonical form. Rows should be separated with newlines, entries within rows by spaces.
     */
    public Tableau(String fileName){
        File f = new File(fileName);

        ArrayList<ArrayList<Double>> temp = new ArrayList<ArrayList<Double>>();

        try {
            Scanner s = new Scanner(f);

            while(s.hasNextLine()){
                String line = s.nextLine();
                line = line.trim();

                String[] lineSplit = line.split(" ");
                ArrayList<Double> rowNumbers = new ArrayList<Double>();

                for(int i = 0; i < lineSplit.length; i++){
                    rowNumbers.add(Double.parseDouble(lineSplit[i]));
                }

                temp.add(rowNumbers);
            }

            s.close();

        } catch (FileNotFoundException e) {
            System.err.println("File not found.");
            System.exit(1);
        }



        //Numbers collected in temp, now transfer over to tableau array.
        double[][] tableau = new double[temp.size()][temp.get(0).size()];

        for(int row = 0; row < tableau.length; row++){
            for(int col = 0; col < tableau[0].length; col++){
                tableau[row][col] = temp.get(row).get(col);
            }
        }

        setTableau(tableau);

    }

    /**
     * Constructure that takes in a pre formed matrix as the tableau
     * @param tableau The pre formed matrix
     */
    public Tableau(double[][] tableau){
        setTableau(tableau);
    }

    /**
     * Sets the tableau and changes dimension values to match
     * @param newTableau The tableau to set.
     */
    public void setTableau(double[][] newTableau){
        this.tableau = newTableau;

        this.numRows = newTableau.length;
        this.numCols = newTableau[0].length;
    }

    /**
     * Retrieves the tableau
     * @return The tableau as a 2d array of doubles
     */
    public double[][] getTableau(){
        return this.tableau;
    }

    /**
     * Retrieves the B vector (leftmost column except for the top left entry)
     * @return The B vector as a double array.
     */
    private double[] getBVector(){
        double[] ret = new double[numRows - 1];

        for(int row = 1; row < numRows; row++){
            ret[row - 1] = tableau[row][0];
        }

        return ret;
    }

    /**
     * Gets the Basic Feasible solution off of the tableau.
     * @return The BFS as an array of doubles where resource x_i is at index i - 1
     */
    private double[] getBFS(){
        double[] ret = new double[numCols - 1];

        double[] bVector = getBVector();

        for(int c = 1; c < numCols; c++){

            //check to see if OF row value is nonzero (if it is, then x_{c - 1} is 0)
            if(tableau[0][c] != 0){
                ret[c - 1] = 0;
            } else {

                //check to see if it's an I column
                int iColumnIndex = isIColumn(c);

                if(iColumnIndex >= 0){
                    ret[c - 1] = bVector[iColumnIndex];
                }
            }
        }

        return ret;
    }

    /**
     * Determines if the column is an I column (a column with a zeroes except for a one. Excludes first row)
     * @param colIndex The column index to determine if it's an i column
     * @return The row which the only 1 exists on. If it's not an I column, return -1
     */
    private int isIColumn(int colIndex){
        //Linear search down the column, search for only a single 1 entry
        boolean oneFound = false;
        int iColumnIndex = -1;

        for(int row = 1; row < numRows; row++){

            //make sure entries are either a 1 or a 0
            if(tableau[row][colIndex] == 0 || tableau[row][colIndex] == 1){

                if(!oneFound && tableau[row][colIndex] == 1){
                    oneFound = true;
                    iColumnIndex = row - 1;
                } else if(oneFound && tableau[row][colIndex] == 1){
                    //If another 1 entry is found, then this isn't an I col. Return invalid index.
                    return -1;
                }

            } else {
                //If entry isnt a 1 or 0, then there is no chance this is an I column
                return -1;
            }
            
        }

        return iColumnIndex;
    }


    /**
     * Checks to see if tableau is in canonnical form, which is an important requirement before the simplex algorithm can be performed.
     * Requirements: 
     * -Non negative B vector (leftmost column excluding index [0][0])
     * -All Identity columns exist in the constraints section of the tableau of size numRows - 1
     * -In each column with an identity column, there must be a 0 in the C vector (top row excluding index [0][0])
     * @return True if tableau is in canonical form, else false
     */
    private boolean isInCanonicalForm(){

        //check B vector
        for(int r = 1; r < numRows; r++){
            if(tableau[r][0] < 0){
                return false;
            }
        }

        //look for identity columns

        //figure out how many columns need to be found
        int numICols = numRows - 1;

        //We're going to count them as we find them, and hope we find them all.
        int numIColsFound = 0;

        for(int i = 0; i < numICols; i++){
            //Calculate target column to match
            int[] targetCol = new int[numRows];
            targetCol[i + 1] = 1;

            
            for(int c = 1; c < numCols; c++){
                for(int r = 0; r < numRows; r++){
                    //If entry doesn't match, it isn't the I col that is being searched for.
                    if(tableau[r][c] != targetCol[r]){
                        break;
                    }

                    //Reached the last row with all entries matched => found I col
                    if(r == numRows - 1){
                        numIColsFound++;
                    }
                }
            }

        }

        return numICols == numIColsFound;
    }


    /**
     * Quickly accesses the objective function value in the tableau
     * @return The objective function value
     */
    private double getOFValue(){
        return tableau[0][0];
    }

    /**
     * Performs a pivot operation on an entry in the tableau
     * @param row The row of the target entry
     * @param column The column of the target entry
     */
    private void pivot(int row, int column){

        //perform operations on each row such that all numbers in the column except for the pivot become zero
        for(int r = 0; r < numRows; r++){
            
            //Don't perform operations on the pivot row yet - that must be done last
            if(r != row){

                double scalar = -1 * (tableau[r][column] / tableau[row][column]);

                for(int c = 0; c < numCols; c++){
                    tableau[r][c] += scalar * tableau[row][c];
                }

            }
        }


        //Now adjust the pivot row
        double scalar = 1.0 / tableau[row][column];

        for(int c = 0; c < numCols; c++){
            tableau[row][c] *= scalar;
        }

    }

    /**
     * Determines if the tableau in it's current state is optimal. Checks the top row of the tableau
     * @return True if the tableau is optimal, else false.
     */
    public boolean isOptimal(){

        for(int i = 1; i < numCols; i++){
            if(tableau[0][i] < 0){
                return false;
            }
        }

        return true;
    }

    /**
     * Optimizes the tableau using the Simplex algorithm. 
     */
    public void optimize(){

        ///Keep pivoting until an optimal tableau is achieved.
        while(!isOptimal()){
            //Find pivot position using both minimum ratio and smallest index rule

            int bestRow = -1;
            int bestCol = -1;
            double bestRatio = Double.POSITIVE_INFINITY;

            //loop from last col to first col to adhere to minimum index rule (prevents cycling)
            for(int c = numCols - 1; c > 0; c--){
                if(tableau[0][c] < 0){
                    for(int r = numRows - 1; r > 0; r--){
                        if(tableau[r][c] != 0){
                            double ratio = tableau[r][0] / tableau[r][c];
    
                            if(ratio >= 0 && ratio < bestRatio){
                                bestRow = r;
                                bestCol = c;
    
                                bestRatio = ratio;
                            }
                        }
                    }
                }
                
                
            }

            pivot(bestRow, bestCol);
        }
    }



    public static void main(String args[]){
        if(args.length != 1){
            System.err.println("One argument required: name of text file containing the linear program as a canonical form tableau.");
            System.err.println("Format: Rows in the tableau are separated using new line characters, while entries within the row are separated with spaces.");
            System.exit(1);
        }

        Tableau s = new Tableau(args[0]);

        boolean isInCF = s.isInCanonicalForm();

        if(!isInCF){
            System.err.println("Input tableau is not in canonical form. Cannot solve.");
            System.exit(2);
        }

        //Input tableau is in canonical form. Solving...

        s.optimize();


        //Obtain solution when finished

        double optOFValue = s.getOFValue();

        double[] solution = s.getBFS();

        System.out.println("Optimial solution: ");

        for(int i = 0; i < solution.length; i++){
            System.out.println("x_" + (i + 1) + " = " + solution[i]);
        }

        System.out.println("\nOptimal solution objective function value: " + optOFValue);
        
    }
}