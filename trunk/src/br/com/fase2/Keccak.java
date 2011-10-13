package br.com.fase2;

import br.com.interfaces.Duplex;
import br.com.interfaces.HashFunction;

public class Keccak implements HashFunction, Duplex {

	@Override
	public int getBitRate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] duplexing(byte[] sigma, int sigmaLength, byte[] z, int zLength) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(int hashBits) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(byte[] aData, int aLength) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] getHash(byte[] val) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
