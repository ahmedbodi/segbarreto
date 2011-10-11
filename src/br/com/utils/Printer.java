/*
 * Printer.java
 * 
 * Métodos auxiliares para impressão de vetores e matrizes
 * 
 */

package br.com.utils;

/**
 * Imprime um vetor com um nome
 */
public class Printer {
	public static void printVector(String name, byte[] b){
		System.out.printf(name + ": ");
		for (int i = 0; i < b.length; i++){ 
            System.out.printf("%2s ", Util.byteToHex(b[i]));
		}
		System.out.println();
	}
	
	/**
	 * Imprime um vetor apenas
	 */
	public static void printVector(byte[] b){
		System.out.printf("| ");
		for (int i = 0; i < b.length; i++){ 
            System.out.printf("%2s", Util.byteToHex(b[i]));
            if (i < (b.length - 1)){
            	System.out.printf(" ");
            }
		}
		System.out.println(" |\n");
	}
	
	/**
	 * Imprime um vetor como um plaintext
	 */
	public static void printVectorAsPlainText(String name, byte[] b){
		System.out.printf(name + ": ");
		for (int i = 0; i < b.length; i++){ 
            System.out.printf("%2s", Util.byteToHex(b[i]));
		}
		System.out.println();
	}
	
	/**
	 * Recupera uma string a partir de um vetor
	 */
	public static String getVectorAsPlainText(byte[] b){
		String vectorString = new String();
		for (int i = 0; i < b.length; i++){ 
            vectorString += Util.byteToHex(b[i]);
		}
		return vectorString;
	}
	
	/**
	 * Imprime uma matrix com um nome
	 */
	public static void printMatrix(String name, byte[][] b){
		System.out.println(name);
		for (int i = 0; i < b.length; i++){ 
			printVector(b[i]);
		}
		System.out.println();
	}
}
