import java.util.*;
import java.io.*;
import java.math.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



class Alinhamento_Global_S {
    
	/**
	 * The character that indicates the row and column for insertion and deletion
	 * penalties in the matrix.
	 */
	protected static final char INDEL_CHAR = '*';
	
    static int[][] fMatrix;
    static String x, y;
    
    public static void main(String args[]) throws Exception {
    	String currentDir = System.getProperty("user.dir") + "/";
    	Path arg1 = Paths.get( currentDir + args[0] );
    	Path arg2 = Paths.get( currentDir + args[1] );
    	x = readFile(arg1);
    	y = readFile(arg2);

    	needlemanWunsch();
    }	
    
    public static String readFile(Path file){
    	String temp = "";
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line = reader.readLine(); // ignores the first line
			while ((line = reader.readLine()) != null) {
				temp = temp.concat(line);
			}
		    } catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		return temp;
    }
    
    /*********************************************
     *******  Needleman–Wunsch algorithm  ********
     *********************************************
     * +1  Match: The two letters are the same
     * -1  Mismatch: The two letters are differential
     * -1  Indel (INsertion or DELetion) : One letter aligns to a gap in the other string.
     **/
    static int gap = -2;
    static int mismatch = -1;
    static int match = 1;
    
    /**
     * To compute an alignment that actually gives the maximum score among all possible
	 * alignment:
	 * - Start from the bottom right cell, and compare the value with the three 
     *   possible sources (Match, Insert, and Delete above) to see which it came from.
     * @throws Exception
     */
    public static void needlemanWunsch() {
    	computeMatrix();
    	String alignmentA = "";
    	String alignmentB = "";
    	int i,j;
    	i = x.length();
    	j = y.length();
    	while(i > 0 || j > 0){
    		// check if it came from a MATCH
    		if( i>0 && j>0 && (fMatrix[i][j] == fMatrix[i-1][j-1] + scoreSubstitution(x.charAt(i-1), y.charAt(j-1)))){ 
    			alignmentA = x.charAt(i-1) + alignmentA;
    			alignmentB = y.charAt(j-1) + alignmentB;
    			i--;
    			j--;
    		}
    		else if(i>0 && (fMatrix[i][j] == fMatrix[i-1][j-1] + gap)){
    			alignmentA = x.charAt(i-1) + alignmentA;
    			alignmentB = "_" + alignmentB;
    			i--;	
    		}
    		else{
    			alignmentA = "_"  + alignmentA;
    			alignmentB = y.charAt(j-1) + alignmentB;
    			j--;	
    		}
		}
    	System.out.println("A: " + alignmentA);
    	System.out.println("B: " + alignmentB);

    }
    public static void printTable(){
    	int r, c, rows, cols, ins, del, sub;
		rows = x.length()+1;
		cols = y.length()+1;
		computeMatrix();
		for (c = 0; c < cols; c++){
			for (r = 0; r < rows; r++)
			{
				System.out.print(fMatrix[r][c] + " ");
			}
			System.out.println();
		}
    }
    /**
	 * Computes the dynamic programming matrix.
	 */
    public static void computeMatrix() {
    	int r, c, rows, cols, ins, del, sub;

		rows = x.length()+1;
		cols = y.length()+1;
		
    	fMatrix =new int[rows][cols];
    	
		// initiate first row
		fMatrix[0][0] = 0;
		for (c = 1; c < cols; c++)
			fMatrix[0][c] = fMatrix[0][c-1] + scoreInsertion(y.charAt(c-1));
		for (r = 1; r < rows; r++)
		{
			// initiate first column
			fMatrix[r][0] = fMatrix[r-1][0] + scoreInsertion(x.charAt(r-1));
			for (c = 1; c < cols; c++)
			{
				ins = fMatrix[r][c-1] + scoreInsertion(y.charAt(c-1));
				sub = fMatrix[r-1][c-1] + scoreSubstitution(x.charAt(r-1),y.charAt(c-1));
				del = fMatrix[r-1][c] + scoreDeletion(x.charAt(r-1));

				// choose the greatest
				fMatrix[r][c] = Math.max(Math.max(ins, sub), del);
			}
		}
    }
	private static int scoreSubstitution (char a, char b) {
		if(a == INDEL_CHAR || b == INDEL_CHAR)
			return gap;
		if(a == b)
			return match;
		return mismatch;
	}
    public static int scoreInsertion(char c) {
    		return scoreSubstitution (INDEL_CHAR, c);
	}
	public static int scoreDeletion (char a) {
		return scoreSubstitution (a, INDEL_CHAR);
	}
}
    
