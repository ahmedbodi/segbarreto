package br.com.utils;

/**
 * 
 * Conversor class
 * 
 */
public class Util {



	/**
	 * Convert a block (array of bytes) to a matrix representation
	 */
	public static void blockToMatrix(byte[] block, byte[][] matrix,
			boolean columnMapping) {
		int size = block.length / 3;
		if (columnMapping) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < size; j++) {
					matrix[i][j] = block[i + 3 * j];
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < size; j++) {
					matrix[i][j] = block[size * i + j];
				}
			}
		}
	}

	/**
	 * Convert a matrix to a block representation
	 */
	public static void matrixToBlock(byte[] block, byte[][] matrix,
			boolean columnMapping) {
		int size = matrix[0].length;
		if (columnMapping) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < size; j++) {
					block[i + 3 * j] = matrix[i][j];
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < size; j++) {
					block[size * i + j] = matrix[i][j];
				}
			}
		}
	}

	/**
	 * Copy a matrix
	 */
	public static void copyMatrix(byte[][] in, byte[][] out) {
		for (int i = 0; i < in.length; i++)
			for (int j = 0; j < in[i].length; j++)
				out[i][j] = in[i][j];
	}
}