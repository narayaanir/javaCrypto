import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Attack extends AttackData {

	public Attack() {
		super();
	}
	static byte[] data=new byte[4];
	
	public static void main(String[] args) {
		if (args.length!=1) 
			usage();
		Attack x=new Attack();
		try {
			BufferedReader in=new BufferedReader(new FileReader(new File(args[0])));
			String line=null;
			while ((line=in.readLine())!=null) {
				String[] var=line.split("\\s+");
				pcPair.put(var[0], var[1]);
			}
			in.close();
			x.solver();				
			x.displayKey();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void usage() {
		// TODO Auto-generated method stub
		System.out.println("Usage: java Attack <FileName>");
		System.exit(1);
	}

}