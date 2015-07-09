package com.leosoft.puzzle.sudoku;

import org.junit.Test;

public class SudokuTest {
	@Test
	public void basic() {
		Sudoku sudoku = new Sudoku("sudoku.txt");
		sudoku.print(true);
		long start = System.nanoTime();
		sudoku.solve();
		long end = System.nanoTime();
		System.out.println("Time Cost: " + (end - start)/1000000f + " ms");
		sudoku.print(true);
	}
}
