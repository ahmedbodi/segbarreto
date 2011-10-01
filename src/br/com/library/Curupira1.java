/*
 * Curupira1.java
 * 
 * We present Curupira-1, a special-purpose block cipher tailored for platforms where power consumption and processing time are very constrained
 * resources, like sensor and mobile networks or systems heavily dependent on tokens or smart cards. Curupira-1 is an instance of the Wide Trail family of
 * algorithms which includes the AES cipher, and displays both involutional structure, in the sense that the encryption and decryption modes differ only in the key
 * schedule, and cyclic key schedule, whereby the round subkeys can be computed in-place in any order.
 */

package br.com.library;

import br.com.utils.Util;
import br.com.interfaces.BlockCipher;

public class Curupira1 implements BlockCipher{

	/*
	 * Local vars
	 */
	private int blockBits;
	private int keyBits;
	private byte[] cipherKey;
	private int t;
	private int numberOfRounds;
	private byte keyEvolution[][][];
	
	/*
	 * Abstract methods 
	 */
	
	/*
	 * @see br.com.interfaces.BlockCipher#blockBits()
	 */
	public int blockBits() {
		return this.blockBits;
	}
	
	/*
	 * @see br.com.interfaces.BlockCipher#keyBits()
	 */
	public int keyBits() {
		return this.keyBits;
	}
	
	/*
	 * @see br.com.interfaces.BlockCipher#makeKey(byte[], int)
	 */
	public void makeKey(byte[] cipherKey, int keyBits) {
		this.cipherKey = cipherKey;
		this.keyBits = keyBits;
		this.t = keyBits/48;
		this.numberOfRounds = 4 * t + 2;
		
		evolveKey();
	}
	
	/*
	 * @see br.com.interfaces.BlockCipher#encrypt(byte[], byte[])
	 */
	@Override
	public void encrypt(byte[] mBlock, byte[] cBlock) {
		// Plain text to matrix
		byte[][] blockMatrix = new byte[3][4];
		Util.blockToMatrix(mBlock, blockMatrix, true);
		
		//Initial key addition
		keyAdditionLayerSigma(blockMatrix, keySelectionPhi(keyEvolution[0]));
		
		for (int round = 1; round < numberOfRounds; round ++){
			roundFunctions(blockMatrix, keyEvolution[round], true);
		}
		
		//last round function
		nonLinearLayerGama(blockMatrix);
		permutationLayerPi(blockMatrix);
		keyAdditionLayerSigma(blockMatrix, keySelectionPhi(keyEvolution[numberOfRounds]));
		
		Util.matrixToBlock(cBlock, blockMatrix, true);
		
	}
	
	/*
	 * @see br.com.interfaces.BlockCipher#decrypt(byte[], byte[])
	 */
	@Override
	public void decrypt(byte[] cBlock, byte[] mBlock) {
		// Plain text to matrix
		byte[][] cipherMatrix = new byte[3][4];
		Util.blockToMatrix(cBlock, cipherMatrix, true);
		
		//Initial key addition
		keyAdditionLayerSigma(cipherMatrix, keySelectionPhi(keyEvolution[numberOfRounds]));
		
		nonLinearLayerGama(cipherMatrix);
		permutationLayerPi(cipherMatrix);
		
		for (int round = numberOfRounds - 1; round >= 1; round --){
			roundFunctions(cipherMatrix, keyEvolution[round], false);
		}
		
		//last round function
		keyAdditionLayerSigma(cipherMatrix, keySelectionPhi(keyEvolution[0]));
		
		//byte[] blockReturn = new byte[cipherMatrix[0].length * 3];
		Util.matrixToBlock(mBlock, cipherMatrix, true);		
		
	}
	
	/*
	 * @see br.com.interfaces.BlockCipher#sct(byte[], byte[])
	 */
	@Override
	public void sct(byte[] cBlock, byte[] mBlock) {
		byte[][] blockMatrix = new byte[3][4];
		Util.blockToMatrix(mBlock, blockMatrix, true);
		for (int i = 0; i < 4; i++)
		{
			nonLinearLayerGama(blockMatrix);
			permutationLayerPi(blockMatrix);
			linearDiffusionLayerTheta(blockMatrix);
		}
		
		Util.matrixToBlock(cBlock, blockMatrix, true);
	}
	
	/*
	 * 3. Description of the CURUPIRA primitive
	 */
	
	/**
	 * 3.1. The nonlinear layer gama
	 * gama(a) = b <=> b[i][j] = S[a[i][j]]
	 * @param textMatrix Matrix of byte
	 */
	private void nonLinearLayerGama (byte[][] textMatrix){
		for (int i = 0; i < textMatrix.length; i++){
			for (int j = 0; j < textMatrix[i].length; j++){
				textMatrix[i][j] = this.S(textMatrix[i][j]);
			}
		}
	}
	
	/**
	 * 3.2. The permutation layer pi
	 * pi(a) = b <=> b[i][j] = a[i][i ^ j]
	 * @param textMatrix Matrix of byte
	 */
	public void permutationLayerPi(byte[][] textMatrix) {
		byte[][] matrixCopy = new byte[textMatrix.length][textMatrix[0].length];
		Util.copyMatrix(textMatrix, matrixCopy);
		for (int i = 1; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
					textMatrix[i][j] = matrixCopy[i][(i ^ j)];
			}
		}
	}
	
	/**
	 * 3.3. The linear diffusion layer theta
	 * theta(a) = b <=> b = D * a
	 * D * a - calculated using Algorithm 2
	 * @param textMatrix Matrix of byte
	 */
	private void linearDiffusionLayerTheta(byte[][] textMatrix) {
		int v;
		int w;
		for(int j = 0; j < textMatrix[0].length; j++){
			v = xtimes(0xFF & (textMatrix[0][j] ^ textMatrix[1][j] ^ textMatrix[2][j]));
			w = xtimes(v);
			
			textMatrix[0][j] = (byte)(textMatrix[0][j] ^ v); 
			textMatrix[1][j] = (byte)(textMatrix[1][j] ^ w);
			textMatrix[2][j] = (byte)(textMatrix[2][j] ^ v ^ w);
		}	
	}
	
	/**
	 * 3.4. The key addition sigma[k]
	 * sigma[k](a) = b <=> b[i][j] = a[i][j] ^ k[i][j]
	 * @param textMatrix Matrix of byte
	 * @param key Matrix of byte
	 */
	private void keyAdditionLayerSigma(byte[][] textMatrix, byte[][] key) {
		for (int i = 0; i < textMatrix.length; i++) {
			for (int j = 0; j < textMatrix[i].length; j++) {
				textMatrix[i][j] = (byte) (textMatrix[i][j] ^ key[i][j]);
			}
		}
	}
	
	/**
	 * Figure 2. The Curupira-1 round structure - The round functions
	 * Executes 'gama', 'pi', 'theta' and 'sigma' in sequence (encrypt = true)
	 * or
	 * Executes 'sigma', 'theta', 'pi' and 'gama' in sequence (encrypt = false)
	 * @param textMatrix Matrix of byte
	 * @param key Matrix of byte 
	 */
	private void roundFunctions(byte[][] textMatrix, byte[][] key, boolean encrypt){
		if(encrypt){
			nonLinearLayerGama(textMatrix);
			permutationLayerPi(textMatrix);
			linearDiffusionLayerTheta(textMatrix);
			keyAdditionLayerSigma(textMatrix, keySelectionPhi(key));
		}
		else{
			keyAdditionLayerSigma(textMatrix, keySelectionPhi(key));
			linearDiffusionLayerTheta(textMatrix);
			permutationLayerPi(textMatrix);
			nonLinearLayerGama(textMatrix);
		}
	}
	
	/*
	 * Schedule methods
	**/
	
	/**
	 * 3.7. The key evolution 'psi' - Figure 3. The Curupira-1 key schedule
	 * Executes 'sigma', 'csi' and 'mi' in sequence
	 * @param key Matrix of byte
	 * @param round int
	 * @param invert boolean
	 * 
	 */
	private void keyEvolutionPsi(byte[][] key, int round, boolean invert){
		constantAdditionLayerSigma(key, round);
		cyclicShiftLayerCsi(key);
		linearDiffusionLayerMi(key, invert);
	}
	
	/**
	 * Applies the keyEvolutionPsi method "numberOfRounds" times 
	 * and save each result in a matrix. 
	 */
	private void evolveKey(){
		keyEvolution = new byte[numberOfRounds + 1][3][2 * t];
		Util.blockToMatrix(cipherKey, keyEvolution[0], true);
	
		Util.copyMatrix(keyEvolution[0], keyEvolution[1]);
		for (int i = 1; i <= numberOfRounds; i++)
		{
			keyEvolutionPsi(keyEvolution[i], i, false);
			
			if (i != numberOfRounds){
				Util.copyMatrix(keyEvolution[i], keyEvolution[i+1]);
			}
		}	
	}
	
	/**
	 * 3.7. The constant addition layer 'sigma'
	 * @param key Matrix of byte[][]
	 * @param round int
	 */
	private void constantAdditionLayerSigma(byte[][] key, int round){
		byte q[][] = q(round);
		
		for (int i = 0; i < key.length; i++)
			for (int j = 0; j < key[i].length; j++)
				key[i][j] = (byte)(key[i][j] ^ q[i][j]);
	}
	
	/**
	 * 3.7. The cyclic shift layer 'csi'
	 * Csi(a) = b <=> 	b[0][j] = a[0][j]
	 * 					b[1][j] = a[1][(j + 1) mod 2t]
	 * 					b[2][j] = a[2][(j - 1) mod 2t]
	 * @param Matrix of byte[][] a
	 * @return Matrix of byte[][] b
	 */
	private void cyclicShiftLayerCsi(byte[][] key){
		int size = key[0].length;
		//i = 0
		//do nothing
		
		//i = 1 -> shift left
		byte auxKey = key[1][0];
		for(int j = 0; j < size - 1; j++){
			key[1][j] = key[1][(j + 1)];
		}
		key[1][size - 1] = auxKey;
		
		//i = 2 -> shift right
		auxKey = key[2][size - 1];
		for(int j = size - 1; j > 0; j--){
			key[2][j] = key[2][j - 1];
		}
		key[2][0] = auxKey;
	}
	
	/**
	 * 3.7. The linear diffusion layer 'mi'
	 * mi(a) = E * a, where E = I + c * C
	 * E * a - calculated using Algorithm 3
	 * @param Matrix of byte[][]
	 * @param Boolean invert [select E (false) or E^-1 (true)]
	 * @return Matrix of byte[][] b = E * a 
	 */
	private void linearDiffusionLayerMi(byte[][] key, boolean invert){
		int v;
		for(int j = 0; j < key[0].length; j++){
			v = (key[0][j] ^ key[1][j] ^ key[2][j]) & 0xFF;
			
			if (invert) {
				v = (byte)ctimes(v) ^ v;
			}
			else{
				v = ctimes(v);
			}
			
			key[0][j] = (byte)(key[0][j] ^ v); 
			key[1][j] = (byte)(key[1][j] ^ v);
			key[2][j] = (byte)(key[2][j] ^ v);
		}
	}
	
	/**
	 * 3.8. The key selection 'phi'<br/>
	 * k(r) = phi[r](K) <=> k[0][j](r) = S[K[0][j](r)] and k[i][j](r) = K[i][j](r) for i > 0 and 0 <= j < 4.
	 * @param key Matrix of byte
	 * @return k the truncated cipher key matrix
	 */
	private byte[][] keySelectionPhi(byte[][] key){ 
		byte[][] returnKey = new byte[3][4];
		for(int i = 0; i < returnKey.length; i ++){
			for (int j = 0; j < returnKey[i].length; j++){
				if(i == 0){
					returnKey[i][j] = S(key[0][j]);
				}
				else{
					returnKey[i][j] = key[i][j];
				}
			}
		}
		return returnKey;
	}
	
	/*
	 * General
	*/
	
	/**
	 * xtimes
	 * @param x int
	 * @return xtimes(x) int
	 */
	private int xtimes(int x){
		x = x << 1;
		if(x >= 0x100)
			x = x ^ 0x14d;
		return x;
	}
	
	/**
	 * ctimes
	 * @param x int
	 * @return ctimes(x) int
	 */
	private int ctimes(int x){
		return xtimes(xtimes((xtimes((xtimes(x) ^ x)) ^ x)));
	}
	
	/**
	 * Algorithm 1
	 * Computing S [u] from the mini-boxes P and Q
	 * @param u byte
	 * @return S(u) byte
	 */
	private byte S(byte u) {
		// Although it's not a good Java convention, we used uppercase names to match the paper names
		final byte[] P = {0x03, 0x0F, 0x0E, 0x00, 0x05, 0x04, 0x0B, 0x0C, 0x0D, 0x0A, 0x09, 0x06, 0x07, 0x08, 0x02, 0x01};
		final byte[] Q = {0x09, 0x0E, 0x05, 0x06, 0x0A, 0x02, 0x03, 0x0C, 0x0F, 0x00, 0x04, 0x0D, 0x07, 0x0B, 0x01, 0x08};

		byte uh1 = P[(u >> 4) & 0x0F];
		byte ul1 = Q[u & 0x0F];

		byte uh2 = Q[(uh1 & 0x0C) ^ ((ul1 >> 0x02) & 0x03)];
		byte ul2 = P[((uh1 << 0x02) & 0x0C) ^ (ul1 & 0x03)];

		uh1 = P[(uh2 & 0x0C) ^ ((ul2 >> 0x02) & 0x03)];
		ul1 = Q[((uh2 << 0x02) & 0x0C) ^ (ul2 & 0x03)];

		return (byte) ((uh1 << 0x04) ^ ul1);
	}
	
	/**
	 * The schedule constants q
	 * q(0) = 0
	 * q[i][j](s) = S[2t(s-1) + j]		if i=0
	 * q[i][j](s) = 0 					otherwise
	 * @param s Matrix of byte
	 * @return q(s) Matrix of byte
	 */
	private byte[][] q(int s){
		int t = keyBits / 48;
		byte[][] q = new byte[3][2*t];
		
		//q(0) = 0
		if(s == 0x00){
			for (int i = 0; i < 3; i++){
				for (int j = 0; j < (2 * t); j++){
					q[i][j] = (byte)0x00;
				}
			}
		}
		else
		{
			for (int i = 0; i < 3; i++){
				for (int j = 0; j < (2 * t); j++){
					//i = 0
					if(i == 0){
						q[i][j] = S((byte)(0x02 * t * (s - 0x01) + j));
					}
					//otherwise
					else{
						q[i][j] = (byte)0x00;
					}
				}
			}
		}
		return q;
	}
}
