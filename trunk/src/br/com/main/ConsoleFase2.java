package br.com.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import br.com.fase2.Keccak;
import br.com.fase2.KeccakPRG;
import br.com.utils.Printer;
import br.com.utils.Util;


public class ConsoleFase2 {
	
	private static InputStreamReader inputStreamReader = new InputStreamReader(System.in);
	private static BufferedReader reader = new BufferedReader(inputStreamReader);
	private static String lineRead;
	
	private static int bitrate = 1024;
	private static int diversifier = 0;
	private static int randomNumberLength = 0;
	private static Keccak k = new Keccak();
	private static KeccakPRG keccakPRG = new KeccakPRG(k);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		
		System.out.println("****************************************************************");
		System.out.println("* Bem vindo ao Projeto de Felipe Biasi e Guilherme Campos *");
		System.out.println("****************************************************************\n");

		mainMenu();
		
	}
	
	/*
	 * Escolha dos parametros de Keccak[r,c,d] dentre os possíveis valores:
	 * 		r pertence a {64x1, 64x2, 64x3, . . . , 64x24}
	 * 		c = 1600 - r
	 * 		d pertence a {0,1,2,...,255}
	 *    Os valores default sao:
	 * 		r = 1024
	 * 		c = 576
	 * 		d = 0
	 * Seleciona um arquivo e calcula o seu resumo Keccak[r,c,d]
	 * Seleciona um arquivo contendo uma semente de entropia e 
	 * 	calcula um numero especificado de bytes pseudo-aleatorios a partir dessa semente, usando o gerador pseudo-aleatorio KeccakPRG
	 * 	instanciado com a funcao de hash Keccak e complementacao pad10*1.
	 */

	private static void mainMenu(){
		k.setBitRate(bitrate);
		k.setDiversifier(diversifier);
		keccakPRG.init(0);
		
		boolean validValue = false;
		int key;
		printInstructions();
		while (!validValue) {
			try {
				key = new Integer(reader.readLine().trim());
				switch (key) {
				//Escolha dos parametros de Keccak
				case 1:
					//Escolha do bitrate
					String instructions = "Por favor, digite um tamanho para Bitrate (Multiplo de 64, entre 64 e 1536 - em bits): ";

					boolean validValue1 = false;
					System.out.print(instructions);
					while (!validValue1) {
						try {
							lineRead = reader.readLine().trim();
							int intValue = new Integer(lineRead);

							// Validacao
							if (64 <= intValue && intValue <= 1536 && intValue % 64 == 0) {

								bitrate = intValue;

								System.out.println("Bitrate tera " + intValue + " bits.\n");
								System.out.println("Capacity tera " + (1600 - intValue) + " bits.\n");

								validValue1 = true;
							} else {
								System.out.print("Valor invalido. " + instructions);
							}
						} catch (Exception e) {
							System.out.print("Valor invalido. " + instructions);
						}
					}
					
					//Escolha do diversifier
					String instructions1 = "Por favor, digite um tamanho para Diversifier (Entre 0 e 255 - em bits): ";

					boolean validValue2 = false;
					System.out.print(instructions1);
					while (!validValue2) {
						try {
							lineRead = reader.readLine().trim();
							int intValue = new Integer(lineRead);

							// Validacao
							if (0 <= intValue && intValue <= 255) {

								diversifier = intValue;

								System.out.println("Diversifier tera " + intValue + " bits.\n");

								validValue2 = true;
							} else {
								System.out.print("Valor invalido. " + instructions1);
							}
						} catch (Exception e) {
							System.out.print("Valor invalido. " + instructions1);
						}
					}
					
					printInstructions();
					k.setBitRate(bitrate);
					k.setDiversifier(diversifier);
					break;
				//Seleciona um arquivo para calcular seu resumo Keccak
				case 2:

					byte[] mDataaa = readDocument("Indique o caminho do arquivo para ter seu resumo keccak gerado: ");
					
					k.init(bitrate);
					k.update(mDataaa, mDataaa.length);
					byte[] hash = k.getHash(new byte[0]);
					
					System.out.println("Resumo gerado:");
					Printer.printVector(hash);
					printInstructions();
					System.out.println("");
					break;
				//Seleciona um arquivo com uma semente de entropia e calcula um numero pseudo-aleatorio KeccakPRG
				case 3:
					
					byte[] seed = readDocument("Indique o caminho do arquivo contendo a semente de entropia: ");
					
					keccakPRG.feed(seed, seed.length);
					
					printInstructions();
					
					break;
				case 4:
					//Escolhe um numero de tamanho aleatorio
					String instructions2 = "Por favor, digite quantos bytes serao gerados (Maior que 0): ";

					boolean validValue3 = false;
					System.out.print(instructions2);
					while (!validValue3) {
						try {
							lineRead = reader.readLine().trim();
							int intValue = new Integer(lineRead);

							// Validacao
							if (0 < intValue) {

								randomNumberLength = intValue;

								System.out.println("Serao gerados " + intValue + " bytes.\n");

								validValue3 = true;
							} else {
								System.out.print("Valor invalido. " + instructions2);
							}
						} catch (Exception e) {
							System.out.print("Valor invalido. " + instructions2);
						}
					}
					
					byte[] random = keccakPRG.fetch(new byte[randomNumberLength], randomNumberLength);
					
					System.out.println("Numero pseudo-aleatorio gerado:");
					Printer.printVector(random);
					
					printInstructions();
					
					break;
				case 0:
					validValue = true;
					System.out.println("[programa encerrado]");
					break;
				default:
					System.out.print("Valor invalido. ");
					printInstructions();
					break;
				}
			} catch (Exception e) {
				System.out.print("Valor invalido. ");
				printInstructions();
			}
		}
	}
	
	private static void printInstructions()
	{
		String instructions = "Por favor, escolha uma das opcoes abaixo:\n" +
		"[1] Escolher os parametros de Keccak (Default: r = 1024, c = 576 e d = 0)\n" +
		"[2] Selecionar um arquivo para calcular seu resumo Keccak\n" +
		"[3] Selecionar um arquivo com uma semente de entropia\n" +
		"[4] Calcular bytes pseudo aleatorios KeccakPRG\n" +
		"[0] Finalizar programa\n" +
		"[r | " + bitrate + "] [c | " + (1600 - bitrate) + "] [d | " + diversifier + "]\n" +
		"Opcao: ";
		System.out.print(instructions);
	}
	
	/**
	 * Le um arquivo de texto
	 */
	private static byte[] readDocument(String instructions) {
		boolean validValue = false;
		System.out.print(instructions);
		String file = "";
		while (!validValue) {
			try {
				file = reader.readLine().trim();
				
				// Validacao
				byte[] data = Util.readFile(file); 
				if (null != data) {
					return data;
				} else {
					System.out.print("O arquivo \"" + file + "\" nao foi encontrado. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("O arquivo \"" + file + "\" nao foi encontrado. " + instructions);
			}
		}
		return null;
	}
}
