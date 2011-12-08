package br.com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import br.com.fase2.Keccak;
import br.com.fase2.KeccakPRG;
import br.com.interfaces.SpongePRG;

/**
 * 
 * Classe auxiliar para conversões
 * 
 */
public class Util {
	/**
	 * Converter uma String de plaintext para um array de bytes
	 */
	public static byte[] convertStringToVector(String plainText) {
		int size = plainText.length();
		byte[] vectorBlock = new byte[size / 2];
		for (int i = 0; i < size; i += 2) {
			String sByte1 = plainText.substring(i, i + 1);
			String sByte2 = plainText.substring(i + 1, i + 2);

			int byte1 = (stringToByte(sByte1) << (byte) 0x04);
			;
			int byte2 = stringToByte(sByte2);

			vectorBlock[i / 2] = (byte) (byte1 ^ byte2);
		}
		return vectorBlock;
	}

	/**
	 * Converte um caracter simples para um byte
	 */
	public static byte stringToByte(String sByte) {
		if (sByte.equals("0"))
			return (byte) 0x00;
		else if (sByte.equals("1"))
			return (byte) 0x01;
		else if (sByte.equals("2"))
			return (byte) 0x02;
		else if (sByte.equals("3"))
			return (byte) 0x03;
		else if (sByte.equals("4"))
			return (byte) 0x04;
		else if (sByte.equals("5"))
			return (byte) 0x05;
		else if (sByte.equals("6"))
			return (byte) 0x06;
		else if (sByte.equals("7"))
			return (byte) 0x07;
		else if (sByte.equals("8"))
			return (byte) 0x08;
		else if (sByte.equals("9"))
			return (byte) 0x09;
		else if (sByte.toLowerCase().equals("a"))
			return (byte) 0x0A;
		else if (sByte.toLowerCase().equals("b"))
			return (byte) 0x0B;
		else if (sByte.toLowerCase().equals("c"))
			return (byte) 0x0C;
		else if (sByte.toLowerCase().equals("d"))
			return (byte) 0x0D;
		else if (sByte.toLowerCase().equals("e"))
			return (byte) 0x0E;
		else
			return (byte) 0x0F;
	}

	/**
	 * Converte um byte simples para o respectivo valor hexadecimal
	 */
	public static String byteToHex(byte b) {
		return Integer.toString( ( b & 0xff ) + 0x100, 16).substring( 1 ).toUpperCase();
	}

	/**
	 * Converte um array de bytes para uma string
	 */
	public static String byteToHex(byte[] b) {
		String result = "";
        for (int i = 0; i < b.length; i++) 
                result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        return result.toUpperCase();
	}

	/**
	 * Converte um bloco (array de bytes) para uma matriz
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
	 * Converte uma matriz para um bloco
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
	 * Copia uma matriz
	 */
	public static void copyMatrix(byte[][] in, byte[][] out) {
		for (int i = 0; i < in.length; i++)
			for (int j = 0; j < in[i].length; j++)
				out[i][j] = in[i][j];
	}

	/**
	 * Le arquivo
	 */
	public static byte[] readFile(String filePath) throws java.io.IOException {
		File file = new File(filePath);
		FileInputStream fis = new FileInputStream(file);
		byte[] bytes = new byte[(int) file.length()];
		fis.read(bytes, 0, (int) file.length());
		return bytes;
	}
	
	/**
	 * Salva arquivo
	 */
	public static boolean saveFile(String filePath, byte[] data) throws IOException {
		File file = new File(filePath);
		FileOutputStream fis = new FileOutputStream(file);
		fis.write(data);
		fis.close();
		return true;
	}

	/**
	 * Computa uma operação XOR entre dois arrays de bytes
	 */
	public static byte[] xor(byte[] a, byte[] b) {
		byte[] output = new byte[a.length];

		for (int i = 0; i < a.length; i++)
		{
			if (i < b.length)
				output[i] = (byte) (a[i] ^ b[i]);
			else
				output[i] = a[i];
		}

		return output;
	}
	
	/**
	 * Multiplica por Px, um array de bytes
	 */
	public static byte[] multiplyByPx(byte[] input) {
		byte[] output = new byte[input.length];

		for (int i = 0; i < 9; i++) {
			output[i] = input[i + 1];
		}

		output[9] = (byte) (input[10] ^ (input[0] ^ ((input[0] & 0xFF) >>> 3) ^ ((input[0] & 0xFF) >>> 5)));
		output[10] = (byte) (input[11] ^ ((input[0] << 5) ^ (input[0] << 3)));
		output[11] = input[0];

		return output;
	}

	/**
	 * Adiciona quantos zeros forem necessários à esquerda para completar a string de bits
	 */
	public static byte[] lpad(byte[] message, int n) {
		byte[] leftPaddedMessage = new byte[n];

		for (int i = 0; i < n - message.length; i++)
			leftPaddedMessage[i] = 0;
		for (int i = n - message.length; i < n; i++) {
			leftPaddedMessage[i] = message[i + message.length - n];
		}

		return leftPaddedMessage;
	}

	/**
	 * Adiciona quantos zeros forem necessários à direita para completar a string de bits
	 */
	public static byte[] rpad(byte[] message, int n) {
		byte[] rightPaddedMessage = new byte[n];

		for (int i = 0; i < message.length; i++)
			rightPaddedMessage[i] = message[i];
		for (int i = message.length; i < n; i++)
			rightPaddedMessage[i] = 0;

		return rightPaddedMessage;
	}
	
	/**
	 * Concatena dois bytes 
	 */
	public static byte[] concat (byte[] a, byte[] b)
	{
		byte[] output = new byte[a.length + b.length];
		
		for (int i = 0; i < a.length; i++)
			output[i] = a[i];
		for (int i = a.length; i < a.length + b.length; i++)
			output[i] = b[i - a.length];
		
		return output;
	}
	
	public static byte[] not (byte[] in) {
		byte[] output = new byte[in.length];
		
		for (int i = 0; i < in.length; i++)
			output[i] = (byte)(in[i] ^ 0xFF);
		
		return output;
	}
	
	public static byte[] and (byte[] a, byte[] b) {
		byte[] output = new byte[a.length];
		
		for (int i = 0; i < a.length; i++)
			output[i] = (byte)(a[i] & b[i]);
		
		return output;
	}

    /**
     * Converte uma string em hexadecimal para um array de bytes.
     */
    public static byte[] hexStringToByteArray(String s) 
    {
            int len = s.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                    data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                                    .digit(s.charAt(i + 1), 16));
            }
            return data;
    }
    
    public static BigInteger byteArrayToBigInteger (byte[] M) {
            byte[] M1 = new byte[M.length + 1];
            M1[0] = 0;
            
            for (int i = 1; i < M1.length; i++)
                    M1[i] = M[i-1];
            
            return new BigInteger(M1);
    }
    
    public static byte[] bigIntegerToByteArray (BigInteger bi) {
            byte[] M1 = bi.toByteArray();
            
            if (M1[0] == 0 && M1.length > 1) {
                    byte[] M = new byte[M1.length - 1];
                    for (int i = 0; i < M.length; i++)
                            M[i] = M1[i+1];
                    
                    return M;
            }

            return M1;
    }

    
    private static final int SEED_LENGTH = 32;
    private static final int HASH_BITRATE = 576;
    private static final int HASH_DIVERSIFIER = 64;
    
    /**
     * Obtém um numero aleatório.
     */
    public static byte[] getRandomNumber(int length) {
            byte[] seed = SecureRandom.getSeed(SEED_LENGTH);
            
            Keccak k = new Keccak();
            
            k.setBitRate(HASH_BITRATE);
            k.setDiversifier(HASH_DIVERSIFIER);
            
            // Instantiate a sponge object with a Keccak instance for the calculation of r. 
            SpongePRG sponge = new KeccakPRG(k);
            sponge.init(0);
            sponge.feed(seed, seed.length);
            
            return sponge.fetch(new byte[length], length);
    }
}
