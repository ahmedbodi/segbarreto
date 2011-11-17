package br.com.fase3;

import java.math.BigInteger;

import br.com.interfaces.HashFunction;
import br.com.interfaces.KeyEncapsulation;
import br.com.interfaces.SpongePRG;

public class CramerShoup implements KeyEncapsulation{
	
	BigInteger p;
	BigInteger q;
	BigInteger g1;
	BigInteger g2;
	HashFunction H;
	SpongePRG sr;
	int hashBits = 512;

	@Override
	public void setup(BigInteger p, BigInteger q, BigInteger g1, BigInteger g2,	HashFunction H, SpongePRG sr) {
		this.p = p;
		this.q = q;
		this.g1 = g1;
		this.g2 = g2;
		this.H = H;
		this.sr = sr;
	}

	@Override
	public BigInteger[] makeKeyPair(String passwd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigInteger[] encrypt(BigInteger[] pk, byte[] m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] decrypt(String passwd, BigInteger[] cs) {
		// TODO Auto-generated method stub
		return null;
	}

}
