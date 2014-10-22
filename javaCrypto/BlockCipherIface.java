
public interface BlockCipherIface {
	public int blockSize();
	public int keySize();
	public void displayText();
	public void displayText(byte[] key, byte[] xtext);
}
