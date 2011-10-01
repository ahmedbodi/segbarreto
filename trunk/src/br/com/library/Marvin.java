/*
 * Marvin.java
 * 
 * a new, fully parallelizable MAC called MARVIN, based on the ALRED family and designed with resource-constrained platforms in mind.
 */

package br.com.library;

import br.com.interfaces.BlockCipher;
import br.com.interfaces.MAC;

public class Marvin implements MAC{

	/*
	 * @see br.com.interfaces.MAC#setCipher(br.com.interfaces.BlockCipher)
	 */
	@Override
	public void setCipher(BlockCipher cipher) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * @see br.com.interfaces.MAC#setKey(byte[], int)
	 */
	@Override
	public void setKey(byte[] cipherKey, int keyBits) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * @see br.com.interfaces.MAC#init()
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * @see br.com.interfaces.MAC#init(byte[])
	 */
	@Override
	public void init(byte[] R) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * @see br.com.interfaces.MAC#update(byte[], int)
	 */
	@Override
	public void update(byte[] aData, int aLength) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * @see br.com.interfaces.MAC#getTag(byte[], int)
	 */
	@Override
	public byte[] getTag(byte[] tag, int tagBits) {
		// TODO Auto-generated method stub
		return null;
	}

}
