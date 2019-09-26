import java.util.*;



public class Simplex{

    private double[][] tableau;

    int numRows;

    int numCols;




    public Simplex(String filename){
        double[][] t = {{4,1},{2,3}};
        setTableau(t);
    }


    public void setTableau(double[][] newTableau){
        this.tableau = newTableau;

        this.numRows = newTableau.length;
        this.numCols = newTableau[0].length;
    }


    public double[][] getTableau(){
        return this.tableau;
    }


    private double[] getBFS(){
        double[] ret = new double[numCols - 1];

        for(int i = 1; i < numCols; i++){
            ret[i - 1] = tableau[0][i];
        }

        return ret;
    }

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



    private double getOFValue(){
        return tableau[0][0];
    }

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

    public boolean isOptimal(){

        for(int i = 1; i < numCols; i++){
            if(tableau[0][i] < 0){
                return false;
            }
        }

        return true;
    }


    public void optimize(){


        while(!isOptimal()){
            //Find pivot position using both minimum ratio and smallest index rule


            //perform pivot
        }
    }

    public void printTableau(){
        for(int r = 0; r < numRows; r++){
            for(int c = 0; c < numCols; c++){
                System.out.print(tableau[r][c] + " ");
            }
            System.out.println();
        }
    }


    public static void main(String args[]){
        Simplex s = new Simplex("blah");

        s.printTableau();
        s.pivot(1,0);
        s.printTableau();
    }
}