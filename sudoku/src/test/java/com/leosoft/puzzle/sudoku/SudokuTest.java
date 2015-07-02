package com.leosoft.puzzle.sudoku;

import org.junit.Test;

public class SudokuTest {
	@Test
	public void basic() {
		Sudoku sudoku = new Sudoku("sudoku.txt");
		sudoku.print(false);
		long start = System.currentTimeMillis();
		sudoku.solve();
		long end = System.currentTimeMillis();
		System.out.println("Time Cost: " + (end - start)  + " ms");
		sudoku.print(false);
	}
}
