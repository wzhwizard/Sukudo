package com.leosoft.puzzle.sudoku;

public class SudokuTest {
	public static void main(String[] args) {
        Sudoku s = new Sudoku("puzzles/sudoku.txt");
        s.print(false);
        long start = System.currentTimeMillis();
        s.solve();
        long end = System.currentTimeMillis();
        System.out.println((end - start));
        s.print(false);
    }
}
