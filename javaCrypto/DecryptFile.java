import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.io.ObjectInputStream.GetField;
import java.io.OutputStream;

import edu.rit.util.Hex;
//import edu.rit.util.Packing;


public class DecryptFile extends DecryptData {
	
	static long totSize=0;
	
    public static void main(String[] args) throws IOException {
    	if (args.length!=3 || args[0].length()!=16)
    		usage();	
    	checkFileSize(args[1]);
        byte[] key=Hex.toByteArray(args[0]);
    	byte[] ctext=new byte[8];
        DecryptFile newOb=new DecryptFile();
        InputStream ct=null;
        OutputStream pt = null;
    	try {
    	    ct = new BufferedInputStream(new FileInputStream (new File(args[1])));
    	    pt = new BufferedOutputStream(new FileOutputStream (new File(args[2])));
    	    int content=ct.read(ctext, 0, 4);
    	    long current=0;
			while (content!=-1) {
				current+=content;
		        newOb.decryptText(key, ctext);
		        if ((totSize-current)<4) {
			    	if (!newOb.replaceText()) {
			    		System.out.println("Invalid Padding.");
			    		System.exit(1);
			    	} else {
				        pt.write(newOb.x);
//				        newOb.displayText(key, newOb.x);
						content=ct.read(ctext, 0, 4);
			    	}
		        } else {
			        pt.write(newOb.getText());
//			        newOb.displayText(key, ctext);
					content=ct.read(ctext, 0, 4);	
		        }
			}
//			newOb.replaceText();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File not found.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to open file.");
			e.printStackTrace();
		} finally {
			if (ct!=null) {
				pt.close();
				ct.close();
			}
			System.exit(1);
		}
    	pt.close();
    	ct.close();
    }
    
    

	private static void checkFileSize(String fileName) {
		// TODO Auto-generated method stub
		File f=new File(fileName);
		totSize=f.length();
		if ((totSize % 4) != 0) {
			System.out.println("The file size is not valid.");
			System.exit(1);
		}
	}

	private static void usage() {
		// TODO Auto-generated method stub
		System.out.println("Usage Issue:");
		System.out.println("Use: java EncryptFile <key> <ptfile> <ctfile>");
		System.out.println("Key Length: 16 hex (no spaces.)");
		System.exit(1);
	}
}
