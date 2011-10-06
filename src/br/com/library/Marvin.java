/*
 * Marvin.java
 * 
 * a new, fully parallelizable MAC called MARVIN, based on the ALRED family and designed with resource-constrained platforms in mind.
 */

package br.com.library;

import br.com.interfaces.BlockCipher;
import br.com.interfaces.MAC;
import br.com.utils.Util;

public class Marvin implements MAC{
	
	private byte[] c;
	private byte[] R;
	private byte[] A;
	private byte[] O;
	private BlockCipher cipher;
	private int aLength;
	
	/*
	 * @see br.com.interfaces.MAC#setCipher(br.com.interfaces.BlockCipher)
	 */
	@Override
	public void setCipher(BlockCipher cipher) {
		this.cipher = cipher;
	}

	/*
	 * @see br.com.interfaces.MAC#setKey(byte[], int)
	 */
	@Override
	public void setKey(byte[] cipherKey, int keyBits) {
		this.cipher.makeKey(cipherKey, keyBits);
		
	}

	/*
	 * @see br.com.interfaces.MAC#init()
	 */
	@Override
	public void init() {
		this.A = new byte[this.cipher.blockBits()/8];
		
		c[0] = 0x2A;
		
		this.R = new byte[this.cipher.blockBits()/8];
		this.cipher.encrypt(Util.lpad(this.c, this.cipher.blockBits()), this.R);
		for (int i = 0; i < this.cipher.blockBits()/8; i++) {
			this.R[i] = (byte) (this.R[i] ^ Util.lpad(this.c, this.cipher.blockBits())[i]);
		}
		
		this.O = new byte[this.cipher.blockBits()/8];
		initO();
	}

	/*
	 * @see br.com.interfaces.MAC#init(byte[])
	 */
	@Override
	public void init(byte[] R) {
		this.R = R;
		
		this.O = new byte[this.cipher.blockBits()/8];
		initO();
		
		this.A = new byte[this.cipher.blockBits()/8];
	}

	/*
	 * @see br.com.interfaces.MAC#update(byte[], int)
	 */
	@Override
	public void update(byte[] aData, int aLength) {
		this.aLength = aLength;
		//Assume-se que aData seja a mensagem inteira a ser computada
		for (int i = 0; i < aLength/(this.cipher.blockBits()/8); i++) {
			this.wordWiseMultiplication();
			this.cipher.sct(this.A, aData);
			
		}
		
		
	}

	/*
	 * @see br.com.interfaces.MAC#getTag(byte[], int)
	 */
	@Override
	public byte[] getTag(byte[] tag, int tagBits) {
		
		for (int i = 0; i < this.A.length; i++) {
			A[i] = (byte) (A[i] ^ this.R[i] ^ (this.cipher.blockBits() - tagBits) ^ ((byte) aLength));
		}
		this.cipher.encrypt(A, tag);
		
		return tag;
	}
	
	private void wordWiseMultiplication(){
		byte U0 = this.O[0];
		this.O[0] = this.O[11];
		this.O[11] = this.O[10];
		this.O[10] = this.O[9];
		this.O[9] = this.O[8];
		this.O[8] = this.O[7];
		this.O[7] = this.O[6];
		this.O[6] = this.O[5];
		this.O[5] = this.O[4];
		this.O[4] = this.O[3];
		this.O[3] = this.O[2];
		this.O[2] = (byte) (this.O[1] ^ (this.O[0] ^ (this.O[0] >> 3) ^ (this.O[0] >> 5)));
		this.O[1] = (byte) (U0 ^ (this.O[0] ^ (this.O[0] >> 3) ^ (this.O[0] >> 5)));
	}
	
	private void initO(){
		byte U0 = this.O[0];
		
		this.O[0] = this.R[11];
		this.O[11] = this.R[10];
		this.O[10] = this.R[9];
		this.O[9] = this.R[8];
		this.O[8] = this.R[7];
		this.O[7] = this.R[6];
		this.O[6] = this.R[5];
		this.O[5] = this.R[4];
		this.O[4] = this.R[3];
		this.O[3] = this.R[2];
		this.O[2] = (byte) (this.R[1] ^ (this.O[0] ^ (this.O[0] >> 3) ^ (this.O[0] >> 5)));
		this.O[1] = (byte) (U0 ^ (this.O[0] ^ (this.O[0] >> 3) ^ (this.O[0] >> 5)));
	}
	
}
