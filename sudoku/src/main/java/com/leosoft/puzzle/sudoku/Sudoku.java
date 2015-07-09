package com.leosoft.puzzle.sudoku;

import java.util.LinkedList;
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
					int index = i * size + j;
					matrix[index] = Integer.valueOf(numbers[j]);
					if (matrix[index] != 0) {
						applyChange(index, matrix[index]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	LinkedList<Solution> queue = new LinkedList<Solution>();

	public void solve() {
		Solution s;
		while (push()) {
			while ((s = queue.poll()) != null) {
				int i = s.getIndex();
				int v = s.getValue();
				applyChange(i, v);
				if (v != 0) {
					break;
				}
			}
		}
	}

	private boolean push() {
		boolean result = false;
		int limitCount = -1, limitIndex = -1;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int index = i * size + j;
				int limitValue = limit[index];
				int temp = Integer.bitCount(((limitValue) | (limitValue >> 9) | (limitValue >> 18)) & 511);
				if (matrix[index] == 0 && (limitIndex == -1 || temp > limitCount)) {
					limitIndex = i * size + j;
					limitCount = temp;
				}
			}
		}

		if (limitIndex != -1) {
			result = true;
			int limitValue = ((limit[limitIndex]) | (limit[limitIndex] >> 9) | (limit[limitIndex] >> 18));
			if (limitValue != 511) {
				queue.addFirst(new Solution(limitIndex, 0));
				for (int number = 0; number < size; number++) {
					int possiable = 1 << number;
					if ((possiable & limitValue) != possiable) {
						queue.addFirst(new Solution(limitIndex, number + 1));
					}
				}
			}
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

	private class Solution {

		int index, value;

		@Override
		public String toString() {
			int x = index / size + 1;
			int y = index % size + 1;
			return "Solution [x=" + x + ", y=" + y + ", value=" + value + "]";
		}

		public int getIndex() {
			return index;
		}

		public int getValue() {
			return value;
		}

		public Solution(int index, int possiable) {
			this.index = index;
			this.value = possiable;
		}

	}

	public void print(boolean isDebug) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				System.out.print(matrix[i * size + j] + "  ");
			}
			if (isDebug) {
				System.out.print("\t\t");
				for (int j = 0; j < size; j++) {
					int index = i * size + j;
					System.out.print(Integer
							.bitCount(((limit[index]) | (limit[index] >> 9) | (limit[index] >> 18)) & 511) + "  ");
				}
			}
			System.out.println();
		}

		System.out.println("---------------------------------------");
	}

}
