import java.util.TreeSet;


public class SudokuSquare {
	
	int x = -1;
	int y = -1;
	int val = 0;
	
	TreeSet<Integer> domain = new TreeSet<Integer>();
	
	public SudokuSquare(int i, int j){
		x = i;
		y = j;
		for(int k=1; k<=9; k++){
			domain.add(k);
		}
	}

}
