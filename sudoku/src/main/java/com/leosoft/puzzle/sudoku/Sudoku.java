package com.leosoft.puzzle.sudoku;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class Sudoku {

	private int[] matrix, limit;
	private int size;

	public Sudoku(String path) {
		try {
			List<String> lines = IOUtils.readLines(this.getClass().getResourceAsStream(path));
			size = lines.size();
			int arrayLength = size * size;
			matrix = new int[arrayLength];
			limit = new int[arrayLength];
			for (int i = 0; i < size; i++) {
				String[] numbers = lines.get(i).split(" ");
				for (int j = 0; j < numbers.length; j++) {
					int value = Integer.valueOf(numbers[j]);
					if (value != 0) {
						applyChange(i * size + j, value);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final Deque<Solution> stack = new ArrayDeque<Solution>(512);

	public void solve() {
		Solution s;
		while (pushPossiable()) {
			while ((s = stack.poll()) != null) {
				int i = s.getIndex();
				int v = s.getValue();
				applyChange(i, v);
				if (v != 0) {
					break;
				}
			}
		}
	}

	private boolean pushPossiable() {
		boolean result = false;
		int limitCount = -1, foundIndex = -1, limitValue = -1;
		LOOP:for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int index = i * size + j;
				if (matrix[index] == 0) {
					int newValue = ((limit[index]) | (limit[index] >> 9) | (limit[index] >> 18)) & 511;
					int newCount = Integer.bitCount(newValue);
					if (foundIndex == -1 || newCount > limitCount) {
						foundIndex = i * size + j;
						limitCount = newCount;
						limitValue = newValue;
						if(limitCount==8){
							break LOOP;
						}
					}
				}
			}
		}
		if (foundIndex != -1) {
			if (limitValue != 511) {
				stack.addFirst(new Solution(foundIndex, 0));
				for (int number = 0; number < size; number++) {
					int possiable = 1 << number;
					if ((possiable & limitValue) != possiable) {
						stack.addFirst(new Solution(foundIndex, number + 1));
					}
				}
			}
			result = true;
		}
		return result;
	}

	private void applyChange(int index, int value) {
		int i = index / size;
		int j = index % size;
		int before = matrix[index];
		matrix[index] = value;
		for (int k = 0; k < size; k++) {
			changePoint(i * size + k, before, value, -1);
			changePoint(k * size + j, before, value, 8);
		}
		int s1 = ((i / 3) * 3);
		int e1 = s1 + 3;
		int s2 = ((j / 3) * 3);
		int e2 = s2 + 3;
		for (int x = s1; x < e1; x++) {
			for (int y = s2; y < e2; y++) {
				changePoint(x * size + y, before, value, 17);
			}
		}
	}

	private void changePoint(int i, int before, int after, int offset) {
		if (before != 0) {
			limit[i] &= ~(1 << (before + offset));
		}
		if (after != 0) {
			limit[i] |= 1 << (after + offset);
		}
	}

	private final class Solution {

		private final int index;
		private final int value;

		public int getIndex() {
			return index;
		}

		public int getValue() {
			return value;
		}

		public Solution(final int index, final int possiable) {
			this.index = index;
			this.value = possiable;
		}

		@Override
		public String toString() {
			int x = index / size + 1;
			int y = index % size + 1;
			return "Solution [x=" + x + ", y=" + y + ", value=" + value + "]";
		}
	}

	public void print(boolean isDebug) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				System.out.print(matrix[i * size + j] + "  ");
			}
			if (isDebug) {
				System.out.print("\t");
				for (int j = 0; j < size; j++) {
					int index = i * size + j;
					System.out.print(Integer
							.bitCount(((limit[index]) | (limit[index] >> 9) | (limit[index] >> 18)) & 511) + "  ");
				}
			}
			System.out.println();
		}
		System.out.println("---------------------------------------------------------------");
	}

}
