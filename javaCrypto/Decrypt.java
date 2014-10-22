import edu.rit.util.Hex;

public class Decrypt extends DecryptData {
    
    public static void main(String[] args) throws Exception  {
    	if (args.length!=2 || args[0].length()!=16 || args[1].length()!=8)
    		usage();
        byte[] key=Hex.toByteArray(args[0]);
        byte[] ctext=Hex.toByteArray(args[1]);
        Decrypt newOb=new Decrypt();
        newOb.decryptText(key, ctext);
        newOb.displayText();
    }


	private static void usage() {
		// TODO Auto-generated method stub
		System.out.println("Usage Issue:");
		System.out.println("Use: java Decrypt <key> <plaintext>");
		System.out.println("Key: 16 hex and Text: 8 hex (No Spaces.)");
		System.exit(1);
	}
	
}