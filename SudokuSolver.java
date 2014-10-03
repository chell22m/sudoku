import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;


public class SudokuSolver {
	
    private static SudokuSquare[][] sudoku = new SudokuSquare[9][9];
    public SudokuSquare current = null;
    private int numBacktrack = 0;
    private int numNode = 0;
	
	public SudokuSolver(String fileName) throws FileNotFoundException{
		
		Scanner input = new Scanner(new BufferedReader(new FileReader(fileName)));

		// Reads in all 81 integers for the sudoku solution
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				
				sudoku[i][j] = new SudokuSquare(i, j);

				// if the next value is an int then set it equal to the
				// solution[i][j]
				if (input.hasNextInt() == true) {
					sudoku[i][j].val = input.nextInt();

					// if the solution is not within 1 to 9 then an exception is
					// thrown
					if (sudoku[i][j].val < 0 || sudoku[i][j].val > 9) {
						System.out.println("Solution not within 1 - 9! Exit!");
						return;
				}
				}

				// If the solutions file doesn't have 81 int then an exception
				// occurs
				else{
					System.out.println("Not enough Integers! Exit!");
					return;
				}
			}
		}

	}
	
//    Doing naive search
	public Boolean backtrackingSearch(){
		current = sudoku[0][0];
		return backtracking(current);
	}
	
	public Boolean backtracking(SudokuSquare curr){
		numBacktrack++;
		if(goalTest()){
			return true;
		}
		curr = setSelectUnassignedVariable(curr);
		for(int i=1; i<=9; i++){
			curr.val = i;
			numNode++;
			if(constraint(curr.x, curr.y)){
//				System.out.println("backtracking!");
				if(backtracking(curr)){
					return true;
				}
				else{
					curr.val = 0;
				}
			}
			else{
				curr.val = 0;
			}
		}
		return false;
	}

	private SudokuSquare setSelectUnassignedVariable(SudokuSquare curr) {
		if(current.val == 0){
			return current;
		}
		for(int k=current.x; k<=8; k++){
			for(int i=current.y; i<=8; i++){
				if(sudoku[k][i].val == 0){
					return sudoku[k][i];
				}
			}
		}
		return null;
	}

	private boolean goalTest() {
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				if(sudoku[i][j].val == 0){
					return false;
				}
				if(constraint(i, j) == false){
					return false;
				}
			}
		}
		
		return true;
	}

	private boolean constraint(int i, int j) {
		for(int k=0; k<=8; k++){
			if(sudoku[i][j].val == sudoku[i][k].val && j!=k){
//				System.out.println("false1!");
				return false;
			}
			if(sudoku[i][j].val == sudoku[k][j].val && i!=k){
//				System.out.println("false2!");
				return false;
			}
		}
		int block_x = i/3;
		int block_y = j/3;
		for(int k=block_x*3; k<=(block_x*3 + 2); k++){
			for(int l=block_y*3; l<=(block_y*3 + 2); l++){
				if(i == k && j == l){
					continue;
				}
				if(sudoku[i][j].val == sudoku[k][l].val){
//					System.out.println("false3!");
					return false;
				}
			}
		}
		return true;
	}
	
//    Doing backtracking with Forward Checking search
	public Boolean backtrackingSearch3(){
		current = sudoku[0][0];
		return backtracking3(current);
	}
	
	public Boolean backtracking3(SudokuSquare curr){
		numBacktrack++;
		if(goalTest()){
			return true;
		}
		curr = setSelectUnassignedVariable(curr);
		TreeSet<Integer> domain = new TreeSet<Integer>();
		for(int m : curr.domain){
			domain.add(m);
		}
		for(int i : domain){
			curr.val = i;
			numNode++;
//			printSudoku();
			
			if(constraint(curr.x, curr.y)){
			
				if(forwardChecking(curr.x, curr.y)){
//					System.out.println("backtracking!");
					if(backtracking3(curr)){
						return true;
					}
					else{
						forwardCheckingUndo(curr.x, curr.y);
					}
				}
				else{
					forwardCheckingUndo(curr.x, curr.y);
				}
			}
		}
		curr.val = 0;
		return false;
	}
	
	
	private void forwardCheckingUndo(int x, int y) {
		for(int k=0; k<=8; k++){
			if (!sudoku[x][k].domain.contains(Integer.valueOf(sudoku[x][y].val))){
				sudoku[x][k].domain.add(Integer.valueOf(sudoku[x][y].val));
			}
			if (!sudoku[k][y].domain.contains(Integer.valueOf(sudoku[x][y].val))){
				sudoku[k][y].domain.add(Integer.valueOf(sudoku[x][y].val));

			}
		}
		int block_x = x/3;
		int block_y = y/3;
		for(int k=block_x*3; k<=(block_x*3 + 2); k++){
			for(int l=block_y*3; l<=(block_y*3 + 2); l++){
				if (!sudoku[k][l].domain.contains(Integer.valueOf(sudoku[x][y].val))){
					sudoku[k][l].domain.add(Integer.valueOf(sudoku[x][y].val));
				}
			}
		}
	}

	private boolean forwardChecking(int x, int y) {
		for(int k=0; k<=8; k++){
			sudoku[x][k].domain.remove(Integer.valueOf(sudoku[x][y].val));
			if(sudoku[x][k].domain.isEmpty() && sudoku[x][k].val == 0){
				return false;
			}
			sudoku[k][y].domain.remove(Integer.valueOf(sudoku[x][y].val));
			if(sudoku[k][y].domain.isEmpty() && sudoku[k][y].val == 0){
				return false;
			}
		}
		int block_x = x/3;
		int block_y = y/3;
		for(int k=block_x*3; k<=(block_x*3 + 2); k++){
			for(int l=block_y*3; l<=(block_y*3 + 2); l++){
				sudoku[k][l].domain.remove(Integer.valueOf(sudoku[x][y].val));

				if(sudoku[k][l].domain.isEmpty() && sudoku[k][l].val == 0){
					System.out.println("false3!");
					return false;
				}
			}
		}
		return true;
	}

	public void printSudoku(){
		System.out.println();
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				if(j%3 == 0 && j!=0){
					System.out.print("| ");
				}
				System.out.print(sudoku[i][j].val + " ");
			}
			System.out.println();
			if(i==2 || i==5){
				System.out.println("____________________");
			}
		}
		System.out.println();
	}

//	Doing backtracking with Minimum Remaining Values Forward Checking search
	public Boolean backtrackingSearch3b(){
		current = sudoku[0][0];
		return backtracking3b(current);
	}
	
	public Boolean backtracking3b(SudokuSquare curr){
		numBacktrack++;
		if(goalTest()){
			return true;
		}
		curr = setSelectMRV(curr);
		TreeSet<Integer> domain = new TreeSet<Integer>();
		for(int m : curr.domain){
			domain.add(m);
		}
		for(int i : domain){
			curr.val = i;	
			numNode++;
			if(constraint(curr.x, curr.y)){
			
				if(forwardChecking(curr.x, curr.y)){
//					System.out.println("backtracking!");
					if(backtracking3b(curr)){
						return true;
					}
					else{
						forwardCheckingUndo(curr.x, curr.y);
					}
				}
				else{
					forwardCheckingUndo(curr.x, curr.y);
				}
			}
		}
		curr.val = 0;
		return false;
	}
	
	
	
	private SudokuSquare setSelectMRV(SudokuSquare curr) {
		int x = 0;
		int y = 0;
		int smallestDomain = 9;

		for(int k=current.x; k<=8; k++){
			for(int i=current.y; i<=8; i++){
				if(sudoku[k][i].val == 0 && sudoku[k][i].domain.size() <= smallestDomain){
					x = k;
					y = i;
					smallestDomain = sudoku[k][i].domain.size();
				}
			}
		}
//		System.out.println("chosen: " + sudoku[x][y].x + " " + sudoku[x][y].y + " " + sudoku[x][y].domain.toString());
		return sudoku[x][y];
	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		if(args.length > 3){
			System.out.println("Usage: java SudokuSolver <inputFile> <techniques | optional (2 max)>");
			System.exit(0);
		}
		if (args.length == 1){
			System.out.println("Doing naive search");
			long programStartTime = System.nanoTime();  
			SudokuSolver puzzle = new SudokuSolver(args[0]);
			puzzle.printSudoku();
			long searchStartTime = System.nanoTime();  
			puzzle.backtrackingSearch();
			long endTime = System.nanoTime(); 
			puzzle.printSudoku();
			System.out.println("Total Time: " + (endTime - programStartTime));
			System.out.println("Search Time: " + (endTime - searchStartTime));
			System.out.println("Backtrack: " + puzzle.numBacktrack);
			System.out.println("Node: " + puzzle.numNode);
		}
		if (args.length == 2){
			if(args[1].equals("FC")){
				System.out.println("Doing backtracking with FC search");
				long programStartTime = System.nanoTime();  
				SudokuSolver puzzle = new SudokuSolver(args[0]);
				puzzle.printSudoku();
				long searchStartTime = System.nanoTime();  
				puzzle.backtrackingSearch3();
				long endTime = System.nanoTime(); 
				puzzle.printSudoku();
				System.out.println("Total Time: " + (endTime - programStartTime));
				System.out.println("Search Time: " + (endTime - searchStartTime));
				System.out.println("Backtrack: " + puzzle.numBacktrack);
				System.out.println("Node: " + puzzle.numNode);
			}else{
				System.out.println("Usage: java SudokuSolver <inputFile> FC");
				System.exit(0);
			}
		}
		if (args.length == 3){
			if((args[1].equals("MRV") && args[2].equals("FC")) || (args[2].equals("MRV") && args[1].equals("FC"))){
				System.out.println("Doing backtracking with MRV FC search");
				long programStartTime = System.nanoTime();  
				SudokuSolver puzzle = new SudokuSolver(args[0]);
				puzzle.printSudoku();
				long searchStartTime = System.nanoTime();  
				puzzle.backtrackingSearch3b();
				long endTime = System.nanoTime(); 
				puzzle.printSudoku();
				System.out.println("Total Time: " + (endTime - programStartTime));
				System.out.println("Search Time: " + (endTime - searchStartTime));
				System.out.println("Backtrack: " + puzzle.numBacktrack);
				System.out.println("Node: " + puzzle.numNode);

			}else{
				System.out.println("Usage: java SudokuSolver <inputFile> FC MRV || java SudokuSolver <inputFile> MRV FC");
				System.exit(0);
			}
		}
	}

}
