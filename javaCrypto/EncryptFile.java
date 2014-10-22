import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.rit.util.Hex;


public class EncryptFile extends EncryptData {

	private static byte[] ctext=new byte[4];
	
    public static void main(String[] args) throws IOException {
    	if (args.length!=3 || args[0].length()!=16)
    		usage();
        byte[] key=Hex.toByteArray(args[0]);
        EncryptFile newOb=new EncryptFile();
        InputStream pt=null;
        OutputStream ct = null;
        long totSize=new File(args[1]).length();
//		System.out.println(totSize);
        long currSize=0;
    	try {
    	    pt = new BufferedInputStream(new FileInputStream (new File(args[1])));
    	    ct = new BufferedOutputStream(new FileOutputStream (new File(args[2])));
    	    int content=0;
			while (content!=-1) {
				content=pt.read(ctext, 0, 4);
				currSize+=content;

				if (totSize-currSize>=4) {
				    newOb.encryptText(key, ctext);
				    ct.write(newOb.getText());
//				    newOb.displayText(key, ctext);
				    ct.flush();	
				} else {

					if (totSize==currSize && content==0) {

					    newOb.encryptText(key, ctext);
					    ct.write(newOb.getText());
//					    newOb.displayText(key, ctext);
					    ct.flush();

					    ctext[0]=(byte)0x80;
						for (int i=1; i<4; ++i)
					    	ctext[i]&=0x00000000;
						
					    newOb.encryptText(key, ctext);
					    ct.write(newOb.getText());
//					    newOb.displayText(key, ctext);
					    ct.flush();
					} else if (content<4 && totSize-currSize<4) {
						ctext[content]=(byte) 0x80;
						int i=content+1;
						while (i<4) {
					    	ctext[i]&=0x00000000;
							++i;
						}
					    newOb.encryptText(key, ctext);
					    ct.write(newOb.getText());
//					    newOb.displayText(key, ctext);
					    ct.flush();
					} else {
					    newOb.encryptText(key, ctext);
					    ct.write(newOb.getText());
//					    newOb.displayText(key, ctext);
					    ct.flush();
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File not found.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to open file.");
			e.printStackTrace();
		} finally {
			if (pt!=null) {
		    	ct.close();
		    	pt.close();
			}
			System.exit(1);
		}
    	ct.close();
    	pt.close();
    }


	private static void usage() {
		// TODO Auto-generated method stub
		System.out.println("Usage Issue:");
		System.out.println("Use: java EncryptFile <key> <ptfile> <ctfile>");
		System.out.println("Key Length: 16 hex (no spaces.)");
		System.exit(1);
	}
}
