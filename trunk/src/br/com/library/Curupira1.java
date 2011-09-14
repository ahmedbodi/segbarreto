package br.com.library;

import br.com.interfaces.AEAD;
import br.com.interfaces.BlockCipher;
import br.com.interfaces.MAC;

public class Curupira1 implements BlockCipher, MAC, AEAD {

	@Override
	public void setMAC(MAC mac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIV(byte[] iv, int ivLength) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] encrypt(byte[] mData, int mLength, byte[] cData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] decrypt(byte[] cData, int cLength, byte[] mData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCipher(BlockCipher cipher) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setKey(byte[] cipherKey, int keyBits) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(byte[] R) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(byte[] aData, int aLength) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] getTag(byte[] tag, int tagBits) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int blockBits() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int keyBits() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void makeKey(byte[] cipherKey, int keyBits) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void encrypt(byte[] mBlock, byte[] cBlock) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decrypt(byte[] cBlock, byte[] mBlock) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sct(byte[] cBlock, byte[] mBlock) {
		// TODO Auto-generated method stub
		
	}

}
