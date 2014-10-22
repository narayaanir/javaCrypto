


import edu.rit.util.Hex;
import edu.rit.util.Packing;


public class DecryptData implements BlockCipherIface {
	private byte[] text=new byte[4];
	private short[] subkey=new short[22];
    
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

    private void setKeys(byte[] key) {
    	short[] lkey=new short[24];
    	subkey[0]=Packing.packShortBigEndian(key, 6);
    	lkey[0]=Packing.packShortBigEndian(key, 4);
    	lkey[1]=Packing.packShortBigEndian(key, 2);
    	lkey[2]=Packing.packShortBigEndian(key, 0);
    	for (int i=0; i<21; ++i) {
    		lkey[i+3]|=(subkey[i]+((lkey[i]&0xFFFF)>>>7|((lkey[i]&0xFFFF)<<9)))^i; // right
    		subkey[i+1]|=(((subkey[i]&0xFFFF)<<2)|((subkey[i]&0xFFFF)>>>14))^lkey[i+3]; //left
    	}
    }
    
	private void generateDecrypt(byte[] ctext) {
		// TODO Auto-generated method stub
    	short[] word=new short[2];
    	word[0]=Packing.packShortBigEndian(ctext, 0);
    	word[1]=Packing.packShortBigEndian(ctext, 2);
    	for (short i=21; i>=0; --i) {
    		short tmpVar=0;
    		tmpVar|=(((word[0]^word[1])&0xFFFF)>>>2)|(((word[0]^word[1])&0xFFFF)<<14);
    		short a=0, b=0;
    		a|=((word[0]^word[1])&0xFFFF)>>>2|((word[0]^word[1])&0xFFFF)<<14;
    		word[1]=a;
    		b|=(((word[0]^subkey[i])-tmpVar)&0xFFFF)<<7|(((word[0]^subkey[i])-tmpVar)&0xFFFF)>>>9;
    		word[0]=b;
    	}
		int tmp=(word[0]<<16)|(word[1]&0xFFFF);
    	Packing.unpackIntBigEndian(tmp, text, 0);
	}
	
	protected byte[] getText() {
		return text;
	}

	
	protected byte[] x;
	public Boolean replaceText() {
		for (int i=3; i>=0; --i) {
			if (i==3 && (text[i]^0xffffff80)==0) {
				x=new byte[3];
				x[0]=text[0];
				x[1]=text[1];
				x[2]=text[2];
				text=new byte[4];
				text[0]=x[0];
				text[1]=x[1];
				text[2]=x[2];
				return true;	
			} else if (i==2 && (text[i]^0xffffff80)==0 && (text[i+1]^0x00000000)==0) {
				x=new byte[2];
				x[0]=text[0];
				x[1]=text[1];
				text=new byte[4];
				text[0]=x[0];
				text[1]=x[1];
				return true;	
			} else if (i==1 && (text[i]^0xffffff80)==0 && (text[i+1]^0x00000000)==0 && (text[i+2]^0x00000000)==0) {
				x=new byte[1];
				x[0]=text[0];
				text=new byte[4];
				text[0]=x[0];
				return true;	
			} else if (i==0 && (text[i]^0xffffff80)==0 && (text[i+1]^0x00000000)==0 && (text[i+2]^0x00000000)==0 && (text[i+3]^0x00000000)==0) {
				return true;	
			}
		}
		return false;
	}
	
	public void decryptText(byte[] key, byte[] ctext) {
		// TODO Auto-generated method stub
//		byte[] cyphertext=ctext.clone();
		setKeys(key);
		generateDecrypt(ctext);
	}
}
