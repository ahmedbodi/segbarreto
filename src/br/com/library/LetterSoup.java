/*
 * LetterSoup.java
 * 
 * A new Authenticated Encryption with Associated Data (AEAD) scheme based onMARVIN, called LETTERSOUP, which is based on the LFRSC mode
 * of operation and shows a high performance.
 */

package br.com.library;

import br.com.interfaces.AEAD;
import br.com.interfaces.BlockCipher;
import br.com.interfaces.MAC;

public class LetterSoup implements AEAD {
	
	/*
	 * @see br.com.interfaces.AEAD#setMAC(br.com.interfaces.MAC)
	 */
	@Override
	public void setMAC(MAC mac) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * @see br.com.interfaces.AEAD#setCipher(br.com.interfaces.BlockCipher)
	 */
	@Override
	public void setCipher(BlockCipher cipher) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * @see br.com.interfaces.AEAD#setKey(byte[], int)
	 */
	@Override
	public void setKey(byte[] cipherKey, int keyBits) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * @see br.com.interfaces.AEAD#setIV(byte[], int)
	 */
	@Override
	public void setIV(byte[] iv, int ivLength) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * @see br.com.interfaces.AEAD#update(byte[], int)
	 */
	@Override
	public void update(byte[] aData, int aLength) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * @see br.com.interfaces.AEAD#encrypt(byte[], int, byte[])
	 */
	@Override
	public byte[] encrypt(byte[] mData, int mLength, byte[] cData) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @see br.com.interfaces.AEAD#decrypt(byte[], int, byte[])
	 */
	@Override
	public byte[] decrypt(byte[] cData, int cLength, byte[] mData) {
		// TODO Auto-generated method stub
		return null;
	}

}
