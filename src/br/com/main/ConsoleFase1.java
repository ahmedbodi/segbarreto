/*
 * Console.java
 * 
 * Classe main que possui as opções para o usuário interagir com o sistema.
 * 
 * 
 * Projeto prático – cifrador híbrido completo – 1ª fase
 * 
 * Felipe Piazza Biasi - 5947899
 * Guilherme Melo Prestes Campos - 5947583
 * 
 */

package br.com.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;

import br.com.fase1.*;
import br.com.utils.Printer;
import br.com.utils.Util;

public class ConsoleFase1 {
	
	private static InputStreamReader inputStreamReader = new InputStreamReader(System.in);
	private static BufferedReader reader = new BufferedReader(inputStreamReader);
	private static String lineRead;
	
	private static int keyBits = 0;
	private static int keyLength = 0;
	private static int ivLength = 0;
	private static int macLength = 0;
	private static byte[] cipherKey = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		
		System.out.println("****************************************************************");
		System.out.println("* Bem vindo ao Projeto de Felipe Biasi e Guilherme Campos *");
		System.out.println("****************************************************************\n");
		
		mainMenu();

	}

	private static void mainMenu(){
		String instructions = "Por favor, escolha uma das opcoes abaixo:\n" +
				"[1] Escolher uma senha\n" +
				"[2] Selecionar um tamanho de IV e de MAC entre o minimo de 64 bits e o tamanho completo do bloco\n" +
				"[3] Selecionar um arquivo para ser apenas autenticado\n" +
				"[4] Selecionar um arquivo com seu respectivo MAC para ser validado\n" +
				"[5] Selecionar um arquivo para ser cifrado e autenticado\n" +
				"[6] Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado\n" +
				"[7] Selecionar um arquivo para ser cifrado e autenticado, e um arquivo correspondente de dados associados para ser autenticado\n" +
				"[8] Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado, um arquivo correspondente de dados associados para ser autenticado\n" +
				"[0] Finalizar programa\n" +
				"Opcao: ";
		boolean validValue = false;
		int key;
		System.out.print(instructions);
		while (!validValue) {
			try {
				key = new Integer(reader.readLine().trim());
				switch (key) {
				//Selecionar um tamanho de chave dentre os valores admissiveis e escolher uma senha alfanumeÅ½rica
				case 1:
					keyLengthInput();
					cipherKeyInput();
					System.out.print(instructions);
					break;
				//Selecionar um tamanho de IV e de MAC entre o minimo de 64 bits e o tamanho completo do bloco"
				case 2:
					macLengthInput();
					ivSizeInput();
					System.out.print(instructions);
					break;
				//Selecionar um arquivo para ser apenas autenticado
				case 3:
					if(variableAreFilled(true, true)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						
						String[] filePath = new String[2];
						byte[] aData = readDocument("Indique o caminho do arquivo para ser apenas autenticado: ", filePath);
						marvin.setCipher(curupira1);
						marvin.setKey(cipherKey, keyBits);
						marvin.init();
						
						marvin.update(aData, aData.length);
						byte[] buffer = new byte[macLength / 8];
						buffer = marvin.getTag(buffer, macLength);
						
						//Save .mac file
						filePath[0] = filePath[0] + ".mac";
						System.out.println("Arquivo foi autenticado.");
						saveDocument("Arquivo \"" + filePath[0] + "\" foi salvo.\n", filePath[0], buffer);
					}
					System.out.print(instructions);
					break;
				//Selecionar um arquivo com seu respectivo MAC para ser validado
				case 4:
					if(variableAreFilled(true, false)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						String[] filePath = new String[2];
						byte[] aData = readDocument("Indique o caminho do arquivo para ser validado: ", filePath);
						
						byte[] savedMac;
						
						if(useDefaultValues(true, false)){
							String defaultDocument = filePath[0] + ".mac";
							savedMac = readDocument(defaultDocument);
							if(null == savedMac){
								System.out.println("Arquivo \".mac\" nÃ£o foi encontrado!");
								savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
							}
						}
						else{
							savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
						}
						
						marvin.setCipher(curupira1);
						marvin.setKey(cipherKey, keyBits);
						marvin.init();

						marvin.update(aData, aData.length);
						
						
						int bufferSize = savedMac.length;
						
						byte[] buffer = new byte[bufferSize];
						buffer = marvin.getTag(buffer, bufferSize * 8);
						
						if(Printer.getVectorAsPlainText(buffer).equals(Printer.getVectorAsPlainText(savedMac)))
							System.out.println("Arquivo validado.\n");
						else
							System.out.println("Autenticacao invalida: as tags nao sao iguais.\n");	
					}
					System.out.print(instructions);
					break;
				//Selecionar um arquivo para ser cifrado e autenticado
				case 5:
					if(variableAreFilled(true, true)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						LetterSoup letterSoup = new LetterSoup();
						
						String[] filePath = new String[2];
						byte[] mData = readDocument("Indique o caminho do arquivo para ser cifrado e autenticado: ", filePath);	
						
						letterSoup.setCipher(curupira1);
						marvin.setCipher(curupira1);
						letterSoup.setMAC(marvin);
						letterSoup.setKey(cipherKey, keyBits);
						
						SecureRandom rand = new SecureRandom();
						byte[] iv = new byte[ivLength / 8];
						rand.nextBytes(iv);

						letterSoup.setIV(iv, ivLength / 8);

						byte[] cData = letterSoup.encrypt(mData, mData.length, null);
						
						byte[] buffer = new byte[macLength / 8];
						buffer = letterSoup.getTag(new byte[macLength / 8], macLength);
												
						System.out.println("Arquivo foi cifrado e autenticado.");
						
						String newFilePath = "";
						//Salva o arquivo .ciph
						newFilePath = filePath[0] + ".ciph";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.", newFilePath, cData);

						//Salva o arquivo .mac
						newFilePath = filePath[0] + ".mac";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.", newFilePath, buffer);
						
						//Salva o arquivo .iv
						newFilePath = filePath[0] + ".iv";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.\n", newFilePath, iv);
				}
				System.out.print(instructions);
				break;
				//Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado
				case 6:
					if(variableAreFilled(true, false)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						LetterSoup letterSoup = new LetterSoup();
						
						String[] filePath = new String[2];
						byte[]cData = readDocument("Indique o caminho do arquivo \".ciph\" para ser decifrado: ", filePath);
						
						byte[] savedMac;
						byte[] savedIv;
						
						if(useDefaultValues(true, true)){
							String defaultDocument = filePath[0].substring(0, filePath[0].length() - 5) + ".mac";
							savedMac = readDocument(defaultDocument);
							if(null == savedMac){
								System.out.println("Arquivo \".mac\" nÃ£o foi encontrado!");
								savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
							}
							
							defaultDocument = defaultDocument.substring(0, defaultDocument.length() - 4) + ".iv";
							savedIv = readDocument(defaultDocument);
							if(null == savedIv){
								System.out.println("Arquivo \".iv\" nÃ£o foi encontrado!");
								savedIv = readDocument("Indique o caminho do arquivo \".iv\": ", filePath);
							}
						}
						else{
							savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
							savedIv = readDocument("Indique o caminho do arquivo \".iv\": ", filePath);
						}
						
						letterSoup.setCipher(curupira1);
						marvin.setCipher(curupira1);
						letterSoup.setMAC(marvin);
						letterSoup.setKey(cipherKey, keyBits);

						letterSoup.setIV(savedIv, savedIv.length);
						
						byte[] mData = letterSoup.decrypt(cData, cData.length, null);
						
						int bufferSize = savedMac.length;
						byte[] buffer = new byte[bufferSize];
						buffer = letterSoup.getTag(new byte[bufferSize], bufferSize * 8);
						
						if(Printer.getVectorAsPlainText(buffer).equals(Printer.getVectorAsPlainText(savedMac))){
								System.out.println("Arquivo validado e decifrado.");
								//Salva arquivo original
								String newFilePath = filePath[0].split("\\.")[0] + "." + filePath[0].split("\\.")[1];
								
								saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.\n", newFilePath, mData);
						}
						else
							System.out.println("Autenticacao invalida.\n");
					}
					System.out.print(instructions);
					break;
				//Selecionar um arquivo para ser cifrado e autenticado, e um arquivo correspondente de dados associados para ser autenticado
				case 7:
					if(variableAreFilled(true, true)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						LetterSoup letterSoup = new LetterSoup();
						
						String[] filePath = new String[2];
						String[] assocFilePath = new String[2];
						byte[] mData = readDocument("Indique o caminho do arquivo para ser cifrado e autenticado: ", filePath);	
						byte[] assocData = readDocument("Indique o caminho do arquivo de dados associados para ser autenticado: ", assocFilePath);	
						
						letterSoup.setCipher(curupira1);
						marvin.setCipher(curupira1);
						letterSoup.setMAC(marvin);
						letterSoup.setKey(cipherKey, keyBits);
						
						SecureRandom rand = new SecureRandom();
						byte[] iv = new byte[ivLength / 8];
						rand.nextBytes(iv);
						
						letterSoup.setIV(iv, ivLength / 8);

						byte[] cData = letterSoup.encrypt(mData, mData.length, null);
						
						letterSoup.update(assocData, assocData.length);
						
						byte[] buffer = new byte[macLength / 8];
						buffer = letterSoup.getTag(new byte[macLength / 8], macLength);
										
						System.out.println("Arquivo foi cifrado e autenticado.");
						
						String newFilePath = "";
						//Salva o arquivo .ciph
						newFilePath = filePath[0] + ".ciph";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.", newFilePath, cData);

						//Salva o arquivo .mac
						newFilePath = filePath[0] + ".mac";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.", newFilePath, buffer);
						
						//Salva o arquivo .iv
						newFilePath = filePath[0] + ".iv";
						saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.\n", newFilePath, iv);
				}
					System.out.print(instructions);
					break;
				//Selecionar um arquivo cifrado com seus respectivos IV e MAC para ser validado e decifrado, um arquivo correspondente de dados associados para ser autenticado
				case 8:
					if(variableAreFilled(true, false)){
						Curupira1 curupira1 = new Curupira1();
						Marvin marvin = new Marvin();
						LetterSoup letterSoup = new LetterSoup();
						
						String[] filePath = new String[2];
						String[] assocFilePath = new String[2];
						
						byte[]cData = readDocument("Indique o caminho do arquivo \".ciph\" para ser decifrado: ", filePath);

						
						byte[] savedMac;
						byte[] savedIv;
						
						if(useDefaultValues(true, true)){
							String defaultDocument = filePath[0].substring(0, filePath[0].length() - 5) + ".mac";
							savedMac = readDocument(defaultDocument);
							if(null == savedMac){
								System.out.println("Arquivo \".mac\" nÃ£o foi encontrado!");
								savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
							}
							
							defaultDocument = defaultDocument.substring(0, defaultDocument.length() - 4) + ".iv";
							savedIv = readDocument(defaultDocument);
							if(null == savedIv){
								System.out.println("Arquivo \".iv\" nÃ£o foi encontrado!");
								savedIv = readDocument("Indique o caminho do arquivo \".iv\": ", filePath);
							}
						}
						else{
							savedMac = readDocument("Indique o caminho do arquivo \".mac\": ", filePath);
							savedIv = readDocument("Indique o caminho do arquivo \".iv\": ", filePath);
						}
						
						byte[] assocData = readDocument("Indique o caminho do arquivo de dados associados: ", assocFilePath);	
						
						letterSoup.setCipher(curupira1);
						marvin.setCipher(curupira1);
						letterSoup.setMAC(marvin);
						letterSoup.setKey(cipherKey, keyBits);
						
						letterSoup.setIV(savedIv, savedIv.length);
						
						byte[] mData = letterSoup.decrypt(cData, cData.length, null);
						
						letterSoup.update(assocData, assocData.length);
						
						int bufferSize = savedMac.length;
						byte[] buffer = new byte[bufferSize];
						buffer = letterSoup.getTag(new byte[bufferSize], bufferSize * 8);
						
						if(Printer.getVectorAsPlainText(buffer).equals(Printer.getVectorAsPlainText(savedMac))){
								System.out.println("Arquivo validado e decifrado.");
								//Salva arquivo original
								String newFilePath = filePath[0].split("\\.")[0] + "." + filePath[0].split("\\.")[1];
								saveDocument("Arquivo \"" + newFilePath + "\" foi salvo.\n", newFilePath, mData);
						}
						else
							System.out.println("Autenticacao invalida.\n");
					}
					System.out.print(instructions);
					break;
				case 0:
					validValue = true;
					System.out.println("[programa encerrado]");
					break;
				default:
					System.out.print("Valor invalido. " + instructions);
					break;
				}
			} catch (Exception e) {
				System.out.print("Valor invalido. " + instructions);
			}
		}
	}

	/**
	 * Método para a opção 1 do programa: adiciona uma senha
	 */
	private static void cipherKeyInput() {
		int maxSize = keyLength / 8;
		
		boolean hexaPass = useHexadecimalPassword();
		
		
		String instructions;
		
		if(hexaPass){
			instructions = "Por favor, digite uma senha hexadecimal de ate " + maxSize * 2 + " caracteres: ";
		}
		else{
			instructions = "Por favor, digite uma senha em ASCII de ate " + maxSize + " caracteres: ";
		}

		// string de instrucao
		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim();
				String stringValue = lineRead;
				
				int stringSize;
				if(hexaPass){
					stringSize = lineRead.length() / 2;
				}
				else{
					stringSize = lineRead.length();
				}

				// Validacao
				if (0 <= stringSize && stringSize <= maxSize && !stringValue.equals("")) {

					keyBits = maxSize * 8;

					cipherKey = new byte[maxSize];
					
					byte[] stringBytes;
					if(hexaPass){
						stringBytes = Util.convertStringToVector(stringValue);
						//Preenche a cipherKey com a senha inserida
						for(int i = 0; i < stringSize; i++){
							cipherKey[i] = stringBytes[i];
						}	
					}						
					else{
						stringBytes = stringValue.getBytes();
						//Preenche a cipherKey com a senha inserida
						for(int i = 0; i < stringSize; i++){
							cipherKey[i] = stringBytes[i];
						}
					}
					
					// Output
					System.out.println("Senha adicionada com sucesso. Chave de " + keyBits + " bits criada.\n");

					validValue = true;
				} else {
					System.out.print("Valor invalido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor invalido. " + instructions);
			}
		}
	}
	
	/**
	 * Escolhe o tamanho da chave
	 */
	private static void keyLengthInput() {
		// string de instrucao
		String instructions = "Por favor, digite um tamanho para a senha (entre 96, 144 ou 192 - em bits): ";

		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim();
				int intValue = new Integer(lineRead);

				// Validacao
				if (intValue == 96 || intValue == 144 || intValue == 192) {

					// Seta o valor com o tamanho da senha
					keyLength = intValue;

					validValue = true;
				} else {
					System.out.print("Valor invalido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor invalido. " + instructions);
			}
		}
	}
	
	/**
	 * O usuário escolhe entre digitar uma senha em valor hexadecimal ou ASCII
	 * @return True if user chose hexadecimal, or false for ASCII
	 */
	private static boolean useHexadecimalPassword(){
		String instructions = "Escolha (a)lfanumerica ou (h)exadecimal: ";
		
		
		boolean validValue = false;
		boolean returnValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim().toLowerCase();

				// Validacao
				if (lineRead.equals("h")){
					returnValue = true;
					validValue = true;					
				}
				else if(lineRead.equals("a")){
					returnValue = false;
					validValue = true;										
				}	
				else{
					System.out.print("Valor invalido. " + instructions);										
				}
				
			} catch (Exception e) {
				System.out.print("Valor invalido. " + instructions);
			}
		}
		return returnValue;
	}
	
	/**
	 * Escolhe o tamanho do IV
	 */
	private static void ivSizeInput() {
		// string de instrucao
		String instructions = "Por favor, digite um tamanho para IV (entre 64 e 96 - em bits): ";

		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim();
				int intValue = new Integer(lineRead);

				// Validacao
				if (64 <= intValue && intValue <= 96) {

					// Seta o valor com o tamanho do IV
					ivLength = intValue;

					System.out.println();
					
					validValue = true;
				} else {
					System.out.print("Valor invalido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor invalido. " + instructions);
			}
		}
	}

	/**
	 * Escolhe o tamanho do MAC
	 */
	private static void macLengthInput() {
		// string de instrucao
		String instructions = "Por favor, digite um tamanho para MAC (entre 64 e 96 - em bits): ";

		boolean validValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim();
				int intValue = new Integer(lineRead);

				// Validacao
				if (64 <= intValue && intValue <= 96) {

					// Seta o valor com o tamanho do MAC
					macLength = intValue;

					validValue = true;
				} else {
					System.out.print("Valor invalido. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("Valor invalido. " + instructions);
			}
		}
	}
	
	/**
	 * Le um arquivo de texto com as instruções
	 */
	private static byte[] readDocument(String instructions, String[] filePath) {
		boolean validValue = false;
		System.out.print(instructions);
		while (!validValue) {
			try {
				filePath[0] = reader.readLine().trim();
				
				// Validacao
				byte[] data = Util.readFile(filePath[0]); 
				if (null != data) {
					return data;
				} else {
					System.out.print("O arquivo \"" + filePath[0] + "\" nao foi encontrado. " + instructions);
				}
			} catch (Exception e) {
				System.out.print("O arquivo \"" + filePath[0] + "\" nao foi encontrado. " + instructions);
			}
		}
		return null;
	}
	
	/**
	 * Le um arquivo de texto.
	 */
	private static byte[] readDocument(String filePath) {
		boolean validValue = false;
		while (!validValue) {
			try {				
				// Validacao
				byte[] data = Util.readFile(filePath); 
				if (null != data) {
					return data;
				} else {
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Salva um arquivo de texto.
	 */
	private static void saveDocument(String message, String filePath, byte[] data) {
		boolean validValue = false;
		
		System.out.println(message);
		while (!validValue) {
			try {
				if (Util.saveFile(filePath, data)) {
					// Output
					validValue = true;
				} else {
					System.out.print("Houve um erro na gravacao. Entrando em loop infinito!");
				}
			} catch (Exception e) {
				System.out.print("Houve um erro na gravacao. Entrando em loop infinito!");
			}
		}
	}
	
	
	/**
	 * Verifica se as variáveis já estão preenchidas
	 * @param keyBitsVariable
	 * @param cipherKeyVariable
	 * @param aLengthOrIvVariable
	 * @return
	 */
	private static boolean variableAreFilled(boolean cipherKeyVariable, boolean aLengthOrIvVariable){
		String message = "";
		if(cipherKeyVariable)
			if(cipherKey == null)
				message += "\tVoce precisa definir uma senha antes (opcao 1).\n";

		if(aLengthOrIvVariable)
			if(macLength == 0)
				message += "\tVoce precisa escolher o tamanho de MAC e IV antes (opcao 2).\n";
		
		if (!message.isEmpty()){
			System.out.println("Os seguintes erros foram encontrados:\n" + message);
			return false;
		}
		return true;
	}
	
	/**
	 * Possibilita ao usuário optar por escolher o mesmo caminho do arquivo .ciph para os arquivos .mac e .iv
	 * @param mac if you want to show the option for .mac files
	 * @param iv if you want to show the option for .iv files
	 * @return True if user chose YES or false otherwise
	 */
	private static boolean useDefaultValues(boolean mac, boolean iv){
		String instructions = "Deseja usar os caminhos padroes para o(s) arquivo(s) ";
		if(mac)
			instructions += "\".mac\"";

		if(iv)
			instructions += " e \".iv\"";
		
		instructions += "? Escolha (s)im ou (n)ao: ";

		boolean validValue = false;
		boolean returnValue = false;

		System.out.print(instructions);
		while (!validValue) {
			try {
				lineRead = reader.readLine().trim().toLowerCase();

				// Validacao
				if (lineRead.equals("s")){
					returnValue = true;
					validValue = true;					
				}
				else if(lineRead.equals("n")){
					returnValue = false;
					validValue = true;										
				}	
				else{
					System.out.print("Valor invalido. " + instructions);
					validValue = false;										
				}
				
			} catch (Exception e) {
				System.out.print("Valor invalido. " + instructions);
			}
		}
		return returnValue;
	}
}
