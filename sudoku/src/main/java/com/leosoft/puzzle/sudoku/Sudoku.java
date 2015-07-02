package com.leosoft.puzzle.sudoku;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class Sudoku {

    private int[] matrix, limit;
    private int size;
    private int LIMIT_MARK = (1 << 29);

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
                        markChange(index);
                    }
                }
            }
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int index = i * size + j;
                    if ((limit[index] & LIMIT_MARK) == LIMIT_MARK) {
                        refreshPoint(index);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void markChange(int index) {
        int i = index / size;
        int j = index % size;
        limit[index] = 0;
        for (int k = 0; k < size; k++) {
            if (k != j) {
                limit[i * size + k] |= LIMIT_MARK;
            }
            if (k != i) {
                limit[k * size + j] |= LIMIT_MARK;
            }
        }
        int s1 = ((i / 3) * 3);
        int e1 = ((i / 3 + 1) * 3);
        int s2 = ((j / 3) * 3);
        int e2 = ((j / 3 + 1) * 3);
        for (int x = s1; x < e1; x++) {
            for (int y = s2; y < e2; y++) {
                if (x != i && y != j) {
                    limit[x * size + y] |= LIMIT_MARK;
                }
            }
        }
    }

    private void refreshPoint(int index) {
        int i = index / size;
        int j = index % size;
        limit[index] = 0;
        if (matrix[index] == 0) {
            for (int k = 0; k < size; k++) {
                int matrxIndex = i * size + k;
                if (k != j && matrix[matrxIndex] != 0) {
                    limit[index] |= (1 << (matrix[matrxIndex] - 1));
                }
                matrxIndex = k * size + j;
                if (k != i && matrix[matrxIndex] != 0) {
                    limit[index] |= (1 << (matrix[matrxIndex] + 8));
                }
            }
            int s1 = ((i / 3) * 3);
            int e1 = s1 + 3;
            int s2 = ((j / 3) * 3);
            int e2 = s2 + 3;
            for (int x = s1; x < e1; x++) {
                for (int y = s2; y < e2; y++) {
                    int matrixValue = matrix[x * size + y];
                    if (x != i && y != j && matrixValue != 0) {
                        limit[index] |= (1 << (matrixValue + 17));
                    }
                }
            }
        }

    }

    LinkedList<Solution> queue = new LinkedList<Solution>();

    public void solve() {
        Solution s;
        while (push()) {
            while ((s = queue.poll()) != null) {
                int i = s.getIndex();
                int v = s.getValue();
                refreshChange(i, v);
                if (v != 0) {
                    break;
                }
            }
        }
    }

    private boolean push() {
        boolean result = false;
        int count = -1, index = -1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int v = limit[i * size + j];
                int newCount = Integer.bitCount(((v) | (v >> 9) | (v >> 18)) & 511);
                if (v > 0 && (index == -1 || newCount > count)) {
                    index = i * size + j;
                    count = newCount;
                }
            }
        }

        if (index != -1) {
            result = true;
            int limitValue = ((limit[index]) | (limit[index] >> 9) | (limit[index] >> 18));
            if (limitValue != 511) {
                queue.addFirst(new Solution(index, 0));
                for (int number = 0; number < size; number++) {
                    int possiable = 1 << number;
                    if ((possiable & limitValue) != possiable) {
                        queue.addFirst(new Solution(index, number + 1));
                    }
                }
            }
        }

        return result;
    }

    private void refreshChange(int index, int after) {
        int i = index / size;
        int j = index - i * size;
        int before = matrix[index];
        matrix[index] = after;
        for (int k = 0; k < size; k++) {
            if (k != j) {
                changePoint(i * size + k, before, after, -1);
            }
            if (k != i) {
                changePoint(k * size + j, before, after, 8);
            }
        }
        int s1 = ((i / 3) * 3);
        int e1 = s1 + 3;
        int s2 = ((j / 3) * 3);
        int e2 = s2 + 3;
        for (int x = s1; x < e1; x++) {
            for (int y = s2; y < e2; y++) {
                if (x != i && y != j) {
                    changePoint(x * size + y, before, after, 17);
                }
            }
        }
        if (after == 0) {
            refreshPoint(index);
        } else {
            limit[index] = 0;
        }
    }

    private void changePoint(int i, int before, int after, int offset) {
        if (before > 0 && limit[i] != 0) {
            limit[i] &= ~(1 << (before + offset));
        }
        if (after > 0 && limit[i] != 0) {
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
                System.out.print(matrix[i * size + j] + "\t");
            }
            System.out.println();
        }
        if (isDebug) {
            System.out.println();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int index = i * size + j;
                    System.out.print(Integer
                            .bitCount(((limit[index]) | (limit[index] >> 9) | (limit[index] >> 18)) & 511) + "\t");
                }
                System.out.println();
            }
        }
        System.out.println("---------------------------------------");
    }

}
