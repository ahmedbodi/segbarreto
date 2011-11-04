package br.com.fase2;

import java.math.BigInteger;

import br.com.interfaces.Duplex;
import br.com.interfaces.HashFunction;
import br.com.utils.Util;

public class Keccak implements HashFunction, Duplex {
	
	int r = 1024;
	int c = 576;
	byte[] state = new byte[1600/8];
	int ntotalbits = 1600;
	byte[][] M;
	int d = 0;

	@Override
	public int getBitRate() {
		return r;
	}

	@Override
	public int getCapacity() {
		return c;
	}
	
	public void setBitRate(int bitrate)
	{
		r = bitrate;
		c = 1600 - r;
	}
	
	public void setDiversifier(int diversifier)
	{
		d = diversifier;
	}

	@Override
	public byte[] duplexing(byte[] sigma, int sigmaLength, byte[] z, int zLength) {
		
		state = Util.xor(state, this.pad(sigma, r/8));
		
		state = keccakf(state);

		for (int i = 0; i < zLength; i++)
			z[i] = state[i];
		
		return z;
	}

	@Override
	public void init(int hashBits) {
		state = new byte[1600/8];
	}

	@Override
	public void update(byte[] aData, int aLength) {
		M = new byte[(aLength + 3)*8/r + 1][r/8];
		
		for (int i = 0; i < aLength; i++)
		{
			M[i*8/r][i%(r/8)] = aData[i];
		}
		
		int pos = aLength;
		
		M[pos*8/r][pos%(r/8)] = 0x01;
		pos++;
		M[pos*8/r][pos%(r/8)] = (byte)(d);
		pos++;
		M[pos*8/r][pos%(r/8)] = (byte)(r/8);
		pos++;
		M[pos*8/r][pos%(r/8)] = 0x01;
	}

	@Override
	public byte[] getHash(byte[] val) {

		byte[] s = new byte[200];
		
		for (int i = 0; i < M.length; i++)
		{
			s = Util.xor(s, M[i]);
			s = keccakf(s);
		}
		
		val = new byte[c/16];
		byte[] z = new byte[0];
		
		while (z.length < c/16)
		{
			byte[] aux = new byte[r/8];
			for (int i = 0; i < r/8; i++)
				aux[i] = s[i];
			
			z = Util.concat(z, aux);
			s = keccakf(s);
		}
		
		for (int i = 0; i < c/16; i++)
			val[i] = z[i];
		
		return val;
	}
	
	byte[] keccakf (byte[] input)
	{
		byte[][][] matrix = new byte[5][5][8];
		for (int x = 0; x < 5; x ++)
			for (int y = 0; y < 5; y++)
				for (int z = 0; z < 8; z ++)
					matrix[x][y][z] = input[(7 - z) + 8*(x + 5*y)];
		
		for (int i = 0; i < 24; i++)
		{
			matrix = theta(matrix);
			matrix = rho(matrix);
			matrix = pi(matrix);
			matrix = chi(matrix);
			matrix = iota(matrix, i);
		}
		
		byte[] block = new byte[ntotalbits/8];
		for (int x = 0; x < 5; x ++)
			for (int y = 0; y < 5; y++)
				for (int z = 0; z < 8; z ++)
					block[z + 8*(x + 5*y)] = matrix[x][y][7 - z];
		return block;
	}
	
	
	byte[][][] theta(byte[][][] a)
	{
		byte[][][] A = new byte[5][5][8];
		
		for (int x = 0; x < 5; x++)
			for (int y = 0; y < 5; y++)
			{
				A[x][y] = a[x][y].clone();
				int x_aux = x == 0 ? 4 : x - 1;
				for (int y_aux = 0; y_aux < 5; y_aux++)
					A[x][y] = Util.xor(A[x][y], a[x_aux][y_aux]);
				
				x_aux = x == 4 ? 0 : x + 1;
				for (int y_aux = 0; y_aux < 5; y_aux++)
					A[x][y] = Util.xor(A[x][y], rotate(a[x_aux][y_aux], 1, 64));
			}
		
		return A;
	}
	
	byte[][][] rho(byte[][][] a)
	{
		byte[][][] A = new byte[5][5][8];
		
		A[0][0] = a[0][0];
		
		int x = 1;
		int y = 0;
		
		for (int t = 0; t < 24; t++)
		{
			int bits = ((t + 1) * (t + 2)/2) % 64;
			A[x][y] = rotate(a[x][y], bits, 64);
			int aux = y;
			y = (2*x + 3*y) % 5;
			x = aux;
		}
		return A;
	}
	
	byte[][][] pi(byte[][][] a)
	{
		byte[][][] A = new byte[5][5][8];
		
		for (int x = 0; x < 5; x++)
			for (int y = 0; y < 5; y++)
			{
				int xlinha = y;
				int ylinha = (2*x + 3*y)%5;
				A[xlinha][ylinha] = a[x][y];
			}
		
		return A;
	}
	
	byte[][][] chi(byte[][][]a)
	{
		byte[][][] A = new byte[5][5][8];
		
		for (int x = 0; x < 5; x++)
			for (int y = 0; y < 5; y++)
			{
				int x_aux1 = (x + 1) % 5;
				int x_aux2 = (x + 2) % 5;
				A[x][y] = Util.xor(a[x][y], Util.and(a[x_aux2][y], Util.not(a[x_aux1][y])));
			}
		
		return A;
	}
	
	byte[][][] iota(byte[][][] a, int roundNumber)
	{
		byte[] vectorBlock = new byte[8];
		switch (roundNumber)
		{
			case 0:
				vectorBlock = Util.convertStringToVector("0000000000000001");
			case 1:
				vectorBlock = Util.convertStringToVector("0000000000008082");
			case 2:
				vectorBlock = Util.convertStringToVector("800000000000808A");
			case 3:
				vectorBlock = Util.convertStringToVector("8000000080008000");
			case 4:
				vectorBlock = Util.convertStringToVector("000000000000808B");
			case 5:
				vectorBlock = Util.convertStringToVector("0000000080000001");
			case 6:
				vectorBlock = Util.convertStringToVector("8000000080008081");
			case 7:
				vectorBlock = Util.convertStringToVector("8000000000008009");
			case 8:
				vectorBlock = Util.convertStringToVector("000000000000008A");
			case 9:
				vectorBlock = Util.convertStringToVector("0000000000000088");
			case 10:
				vectorBlock = Util.convertStringToVector("0000000080008009");
			case 11:
				vectorBlock = Util.convertStringToVector("000000008000000A");
			case 12:
				vectorBlock = Util.convertStringToVector("000000008000808B");
			case 13:
				vectorBlock = Util.convertStringToVector("800000000000008B");
			case 14:
				vectorBlock = Util.convertStringToVector("8000000000008089");
			case 15:
				vectorBlock = Util.convertStringToVector("8000000000008003");
			case 16:
				vectorBlock = Util.convertStringToVector("8000000000008002");
			case 17:
				vectorBlock = Util.convertStringToVector("8000000000000080");
			case 18:
				vectorBlock = Util.convertStringToVector("000000000000800A");
			case 19:
				vectorBlock = Util.convertStringToVector("800000008000000A");
			case 20:
				vectorBlock = Util.convertStringToVector("8000000080008081");
			case 21:
				vectorBlock = Util.convertStringToVector("8000000000008080");
			case 22:
				vectorBlock = Util.convertStringToVector("0000000080000001");
			case 23:
				vectorBlock = Util.convertStringToVector("8000000080008008");
			default:
				vectorBlock = new byte[0];
		}
		
		a[0][0] = Util.xor(a[0][0], vectorBlock);
		return a;
	}
	
	byte[] rotate (byte[] in, int nbits, int ntotalbits)
	{
		BigInteger bg = new BigInteger(in);
		
		for (int i = 0; i < nbits; i++)
		{
			bg = bg.shiftLeft(1);
			if (bg.testBit(ntotalbits))
			{
				bg = bg.clearBit(ntotalbits);
				bg = bg.setBit(0);
			}
			else
				bg = bg.clearBit(0);
		}
		
		byte[] aux = bg.toByteArray();
		byte[] out = new byte[ntotalbits/8];
		
		
		for (int i = 1; i <= ntotalbits/8; i++)
		{
			int pos =  aux.length - i;
			
			if (pos >= 0)
				out[ntotalbits/8 - i] = aux[pos];
		}
		
		return out;
	}
	
	byte[] pad(byte[] input, int length)
	{
		byte[] output = new byte[length];
		
		int i;
		
		for (i = 0; i < input.length; i++)
		{
			output[i] = input[i];
		}
		if (i != length - 1)
		{
			output[i] = 0x01;
			for (int j = i + 1; j < length - 1; j++)
				output[j] = 0x00;
			output[length - 1] = (byte)0x80;
		}
		else
			output[i] = (byte)0x81;
		
		return output;
	}

}
