/*Key: 1918 1110 0908 0100
Plaintext: 6574 694c
Ciphertext: a868 42f2*/

import edu.rit.util.Hex;
import edu.rit.util.Packing;


public class EncryptData implements BlockCipherIface {
	private byte[] text=new byte[4];
    
    // Returns keysize in bytes
    public int keySize() {
        return 8;
    }
    
    // Returns Blocksize in bytes
    public int blockSize() {
        return 4;
    }
    
    public void displayText() {
    	System.out.println(Hex.toString(text));
    }
    
    public void displayText(byte[] key, byte[] xtext) {
    	System.out.println(Hex.toString(text)+" "+Hex.toString(xtext)+" "+Hex.toString(key));
    }
    
    public void generateEncrypt(byte[] plaintext) {
    	short[] word=new short[2];
    	word[0]=Packing.packShortBigEndian(plaintext, 0);
    	word[1]=Packing.packShortBigEndian(plaintext, 2);
    	
//		int tmp=(word[0]<<16)|(word[1]&0xFFFF);

    	
    	for (short i=0; i<22; ++i) {
    		int tmp=(word[0]<<16)|(word[1]&0xFFFF);
        	Packing.unpackIntBigEndian(tmp, text, 0);
    		System.out.println(i+" "+Hex.toString(text)+" "+Hex.toString(subkey[i]));
  		 short a=0, b=0;
  		 	a|=(((word[0]&0xFFFF)>>>7|(word[0]&0xFFFF)<<9)+word[1])^subkey[i];
    		word[0]= a;
    		b|=(((word[1]&0xFFFF)<<2)|((word[1]&0xFFFF)>>>14))^word[0];
    		word[1]= b;
    		
    	}
 
		int tmp=(word[0]<<16)|(word[1]&0xFFFF);
    	Packing.unpackIntBigEndian(tmp, text, 0);
    	
    }

    

    short[] subkey=new short[22];
    
	private void setKeys(byte[] key) {
		short[] lkey = new short[24];
		subkey[0] = Packing.packShortBigEndian(key, 6);
		lkey[0] = Packing.packShortBigEndian(key, 4);
		lkey[1] = Packing.packShortBigEndian(key, 2);
		lkey[2] = Packing.packShortBigEndian(key, 0);
		for (short i = 0; i < 21; ++i) {
			lkey[i + 3]|=((subkey[i] + (((lkey[i] & 0xFFFF) >>> 7) | ((lkey[i] & 0xFFFF) << 9))) ^ i); // right
			subkey[i + 1]|=((((subkey[i] & 0xFFFF) << 2) | ((subkey[i] & 0xFFFF) >>> 14)) ^ lkey[i + 3]);// left
		}
	}
    
	protected byte[] getText() {
		return text;
	}
    
	public void encryptText(byte[] key, byte[] ptext) {
		// TODO Auto-generated method stub
		setKeys(key);
		generateEncrypt(ptext);
	}

}
