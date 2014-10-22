
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


import edu.rit.util.Hex;
import edu.rit.util.Packing;

public class AttackData {
	
	private byte[] ctext=new byte[4];
	private byte[] ptext=new byte[4];
	short[] values=new short[8];
	protected static HashSet<String> keyMasterList=new HashSet<String>();
	protected static HashMap<String, String> pcPair=new HashMap<>();
	private short one, two, three;
	
	private void getValues() {
    	values[0]=Packing.packShortBigEndian(ptext, 0);
    	values[1]=Packing.packShortBigEndian(ptext, 2);
    	values[6]=Packing.packShortBigEndian(ctext, 0);
    	values[7]=Packing.packShortBigEndian(ctext, 2);
    	values[5]=0;

    	short a=0;
    	a|=values[7]^values[6];
    	values[5]=rotateRightTwo(a);
//    	System.out.println(Hex.toString(values[5]));

    	for (short i=-32768; i<32767; ++i) {
    		values[4]=0; values[3]=0; values[2]=0; 
	    	one=i; 
	  		values[2]|=(rotateRightSeven(values[0])+values[1])^one;
	    	values[3]|=(rotateLeftTwo(values[1]))^values[2];
	    	values[4]|=rotateLeftTwo(values[3])^values[5];
			short c=0, d=0;
			c|=(rotateRightSeven(values[2])+values[3])^(rotateLeftTwo(values[3])^values[5]);
			d|=(rotateRightSeven(values[4])+values[5])^(rotateLeftTwo(values[5])^values[7]);
			two=c; three=d;
	    	if (generateEncrypt(ptext, ctext) && checkValues()) {
//	        	System.out.println(Hex.toString(values[5]));
	    		String key=Hex.toString(one)+"\n"+Hex.toString(two)+"\n"+Hex.toString(three);
	    		keyMasterList.add(key);	
	    	}
    	}
	}

	private Boolean generateDecrypt(byte[] pltext, byte[] cltext) {
		// TODO Auto-generated method stub
    	short[] word=new short[2];
    	word[0]=Packing.packShortBigEndian(cltext, 0);
    	word[1]=Packing.packShortBigEndian(cltext, 2);
    	for (short i=2; i>=0; --i) {
    		short tmpVar=0;
    		tmpVar|=(((word[0]^word[1])&0xFFFF)>>>2)|(((word[0]^word[1])&0xFFFF)<<14);
    		short a=0, b=0;
    		a|=((word[0]^word[1])&0xFFFF)>>>2|((word[0]^word[1])&0xFFFF)<<14;
    		word[1]=a;
    		
    		if (i==2) {
        		b|=(((word[0]^three)-tmpVar)&0xFFFF)<<7|(((word[0]^three)-tmpVar)&0xFFFF)>>>9;    			
        		word[0]=b;
    		} else if (i==1) {
        		b|=(((word[0]^two)-tmpVar)&0xFFFF)<<7|(((word[0]^two)-tmpVar)&0xFFFF)>>>9;    			
        		word[0]=b;
    		} else {
        		b|=(((word[0]^one)-tmpVar)&0xFFFF)<<7|(((word[0]^one)-tmpVar)&0xFFFF)>>>9;    			
        		word[0]=b;
    		}
    	}
		int tmpp=0, tmp=(word[0]<<16)|(word[1]&0xFFFF);
    	word[0]=Packing.packShortBigEndian(pltext, 0);
    	word[1]=Packing.packShortBigEndian(pltext, 2);
    	tmpp=(word[0]<<16)|(word[1]&0xFFFF);
    	if (tmp==tmpp) {
        	return true;
    	}
    	return false;
	}
	
    public Boolean generateEncrypt(byte[] pltext, byte[] cltext) {
        	short[] word=new short[2];
        	word[0]=Packing.packShortBigEndian(pltext, 0);
        	word[1]=Packing.packShortBigEndian(pltext, 2);
        	
        	for (short i=0; i<3; ++i) {
      		 short a=0, b=0;
      		 if (i==0) {
      			a|=(rotateRightSeven(word[0])+word[1])^one;
        		word[0]= a;
      		 } else if (i==1) {
        		a|=(rotateRightSeven(word[0])+word[1])^two; 
         		word[0]= a;
      		 } else {
        		 a|=(rotateRightSeven(word[0])+word[1])^three;
        		 word[0]= a;
      		 }
        		b|=rotateLeftTwo(word[1])^word[0];
        		word[1]= b;
        	}
    		int tmpc=0, tmp=(word[0]<<16)|(word[1]&0xFFFF);
        	word[0]=Packing.packShortBigEndian(cltext, 0);
        	word[1]=Packing.packShortBigEndian(cltext, 2);
        	tmpc=(word[0]<<16)|(word[1]&0xFFFF);
        	if (tmp==tmpc) {
            	return true;
        	}
        	return false;
    }
	

	private short rotateRightSeven(short i) {
		short a=0;
		a|=((i&0xFFFF)>>>7|(i&0xFFFF)<<9);
		return a;
	}
	
	private short rotateLeftTwo(short i) {
		short a=0;
		a|=((i&0xFFFF)<<2)|((i&0xFFFF)>>>14);
		return a;
	}
	
	private short rotateRightTwo(short i) {
		short a=0;
		a|=((i&0xFFFF)>>>2|(i&0xFFFF)<<14);
		return a;
	}
	

	protected void solver() {
		// TODO Auto-generated method stub
		for (Map.Entry<String, String> s : pcPair.entrySet()) {
			seText(Hex.toByteArray(s.getKey()), Hex.toByteArray(s.getValue()));
			getValues();	
		}
	}
	
	
	private Boolean checkValues() {
		// TODO Auto-generated method stub
		int count=0;
		for (Map.Entry<String, String> e : pcPair.entrySet()) {
			byte[] x=Hex.toByteArray(e.getKey());
			byte[] y=Hex.toByteArray(e.getValue());
			if (generateEncrypt(x, y) && generateDecrypt(x, y)) {
				++count;
				if (count==pcPair.size())
					return true;
			}
		}
		return false;
	}

	public void displayKey() {
		for (String s : keyMasterList)
			System.out.println(s);
	}

	public void seText(byte[] ptext, byte[] ctext) {
		this.ptext=ptext;
		this.ctext = ctext;
	}
	
	public void seText(int ptext, int ctext) {
		Packing.unpackIntBigEndian(ctext, this.ctext, 0);
		Packing.unpackIntBigEndian(ptext, this.ptext, 0);
	}

	public byte[] getPtext() {
		return ptext;
	}
	
	public byte[] getCtext() {
		return ctext;
	}

}
