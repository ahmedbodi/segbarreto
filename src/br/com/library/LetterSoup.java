/*
 * LetterSoup.java
 * 
 * Introdução do artigo:
 * A new Authenticated Encryption with Associated Data (AEAD) scheme based onMARVIN, called LETTERSOUP, which is based on the LFRSC mode
 * of operation and shows a high performance.
 */

package br.com.library;

import br.com.interfaces.AEAD;
import br.com.interfaces.BlockCipher;
import br.com.interfaces.MAC;
import br.com.utils.Util;

public class LetterSoup implements AEAD {
	
	byte[] R;
	BlockCipher cipher;
	MAC mac;
	byte[][] M = new byte[0][];
	byte[][] C = new byte[0][];
	byte[] H = new byte[0];
	final int n = 12;
	int aLength;
	int mLength;
	int cLength;
	
	/**
	 * @see br.com.interfaces.AEAD#setMAC(br.com.interfaces.MAC)
	 */
	@Override
	public void setMAC(MAC mac) {
		this.mac = mac;
	}
	
	/**
	 * @see br.com.interfaces.AEAD#setCipher(br.com.interfaces.BlockCipher)
	 */
	@Override
	public void setCipher(BlockCipher cipher) {
		this.cipher = cipher;
	}

	/**
	 * @see br.com.interfaces.AEAD#setKey(byte[], int)
	 */
	@Override
	public void setKey(byte[] cipherKey, int keyBits) {
		cipher.makeKey(cipherKey, keyBits);
	}

	/**
	 * @see br.com.interfaces.AEAD#setIV(byte[], int)
	 */
	@Override
	public void setIV(byte[] iv, int ivLength) {
		iv = Util.lpad(iv, n);
		this.R = new byte[12];
		cipher.encrypt(iv, this.R);
		R = Util.xor(R, iv);
	}
	
	/**
	 * @see br.com.interfaces.AEAD#update(byte[], int)
	 */
	@Override
	public void update(byte[] aData, int aLength) {
		this.H = aData;
	}

	/**
	 * @see br.com.interfaces.AEAD#encrypt(byte[], int, byte[])
	 */
	@Override
	public byte[] encrypt(byte[] mData, int mLength, byte[] cData) {
this.mLength += mLength;
		
		byte[] vectorM = new byte[this.mLength];
		int position = -1;
		
		for (int i = 0; i < this.M.length; i++)
		{
			for (int j = 0; j < this.M[i].length; j++)
			{
				position = i * n + j;
				vectorM[i*n + j] = this.M[i][j];
			}
		}
		
		for (int i = 0; i < mLength; i++)
		{
			position++;
			vectorM[position] = mData[i];
		}
		
		byte[][] newM = new byte[(this.mLength - 1)/n + 1][];
		// implementacao do Algoritmo 2 na página 6 do artigo
		for (int i = 0; i <= (this.mLength - 1)/n; i++)
		{
			int size = (this.mLength - i*n) >= n ? n : this.mLength - i*n;
			
			newM[i] = new byte[size];
			for (int j = 0; j < size; j++)
			{
				newM[i][j] = vectorM[i * n + j];
			}
		}
		
		this.M = newM;
		
		byte[] C = lfsrc(this.R, true);

		mac.init(this.R);
		mac.update(C, C.length);
		
		return C;
	}

	/**
	 * @see br.com.interfaces.AEAD#decrypt(byte[], int, byte[])
	 */
	@Override
	public byte[] decrypt(byte[] cData, int cLength, byte[] mData) {
this.cLength += cLength;
		
		byte[] vectorC = new byte[this.cLength];
		int position = -1;
		
		for (int i = 0; i < this.C.length; i++)
		{
			for (int j = 0; j < this.C[i].length; j++)
			{
				position = i * n + j;
				vectorC[i*n + j] = this.C[i][j];
			}
		}
		
		for (int i = 0; i < cLength; i++)
		{
			position++;
			vectorC[position] = cData[i];
		}
		
		byte[][] newC = new byte[(this.cLength - 1)/n + 1][];
		
		for (int i = 0; i <= (this.cLength - 1)/n; i++)
		{
			int size = (this.cLength - i*n) >= n ? n : this.cLength - i*n;
			
			newC[i] = new byte[size];
			for (int j = 0; j < size; j++)
			{
				newC[i][j] = vectorC[i * n + j];
			}
		}
		
		this.C = newC;
		
		byte[] M = lfsrc(this.R, false);

		mac.init(this.R);
		mac.update(cData, cData.length);
		
		return M;
	}

	/**
	 * @see br.com.interfaces.AEAD#getTag(byte[], int)
	 */
	@Override
	public byte[] getTag(byte[] tag, int tagBits) {
int tagBytes = tagBits/8;
		
		byte[] A = mac.getTag(tag, tagBits, false);
		
		if (H.length != 0)
		{
			byte[] L = new byte[12];
			cipher.encrypt(new byte[12], L);
			mac.init(L);
			mac.update(this.H, this.H.length);
			byte[] D = new byte[12];
			D = mac.getTag(D, tagBits, false);
			cipher.sct(D, D);
			A = Util.xor(A, D);
		}
		
		byte[] T = new byte[n];
		
		cipher.encrypt(A, T);
		
		for (int i = 0; i < tagBytes; i++)
			tag[i] = T[i];
		
		return tag;
	}
	
	/**
	 * Linear Feedback Shift Register Counter (LFSRC), páginas 5 e 6 do artigo.
	 * @param nonce
	 * @param encrypt
	 * @return vectorC
	 */
	public byte[] lfsrc (byte[] nonce, boolean encrypt)
	{
		byte[][] A = encrypt ? M : C;

		byte[] O = Util.multiplyByPx(nonce);
		byte[][] C = new byte[A.length][];
		for (int i = 0; i < A.length; i++)
		{
			byte[] encrypted = new byte[12];
			cipher.encrypt(O, encrypted);
			
			C[i] = Util.xor(A[i], encrypted);
			O = Util.multiplyByPx(O);
		}
		
		byte[] vectorC = new byte[(C.length - 1) * n + C[C.length - 1].length];
		
		for (int i = 0; i < C.length; i++)
		{
			for (int j = 0; j < C[i].length; j++)
				vectorC[i*n + j] = C[i][j];
		}
		
		return vectorC;
	}

}
