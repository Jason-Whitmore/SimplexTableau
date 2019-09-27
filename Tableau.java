import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.*;



public class Tableau{

    private double[][] tableau;

    int numRows;

    int numCols;




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

    public Tableau(double[][] tableau){
        setTableau(tableau);
    }


    public void setTableau(double[][] newTableau){
        this.tableau = newTableau;

        this.numRows = newTableau.length;
        this.numCols = newTableau[0].length;
    }


    public double[][] getTableau(){
        return this.tableau;
    }


    private double[] getBVector(){
        double[] ret = new double[numRows - 1];

        for(int row = 1; row < numRows; row++){
            ret[row - 1] = tableau[row][0];
        }

        return ret;
    }


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
        Tableau s = new Tableau("test.txt");

        System.out.println(s.isInCanonicalForm());

        double[] bfs = s.getBFS();

        for(int i = 0; i < bfs.length; i++){
            System.out.println(bfs[i]);
        }
        
    }
}