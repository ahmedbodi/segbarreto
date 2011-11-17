package br.com.fase3;

import java.math.BigInteger;

import br.com.interfaces.DigitalSignature;
import br.com.interfaces.HashFunction;
import br.com.interfaces.SpongePRG;

public class Schnorr implements DigitalSignature{
	
	BigInteger p;
	BigInteger q;
	BigInteger g;
	HashFunction H;
	SpongePRG sr;
	int hashBits = 512;
	byte[] M = new byte[0];

	@Override
	public void setup(BigInteger p, BigInteger q, BigInteger g, HashFunction H,	SpongePRG sr) {
		this.p = p;
		this.q = q;
		this.g = g;
		this.H = H;
		this.sr = sr;
	}

	@Override
	public BigInteger makeKeyPair(String passwd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(byte[] aData, int aLength) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BigInteger[] sign(String passwd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verify(BigInteger y, BigInteger[] sig) {
		// TODO Auto-generated method stub
		return false;
	}

}
