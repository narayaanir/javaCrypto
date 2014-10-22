import edu.rit.util.Hex;
import edu.rit.util.Packing;
import java.util.Arrays;

public class Threefish
   implements BlockCipher
   {

   private long[] subkey = new long [33];

   /**
    * Returns this block cipher's block size in bytes.
    *
    * @return  Block size.
    */
   public int blockSize()
      {
      return 8;
      }

   /**
    * Returns this block cipher's key size in bytes.
    *
    * @return  Key size.
    */
   public int keySize()
      {
      return 10;
      }

   /**
    * Set the key for this block cipher. <TT>key</TT> must be an array of bytes
    * whose length is equal to <TT>keySize()</TT>.
    * <P>
    * For PRESENT, bytes <TT>key[0]</TT> through <TT>key[9]</TT> are used.
    *
    * @param  key  Key.
    */
   public void setKey
      (byte[] key)
      {
      long ku = Packing.packLongBigEndian (key, 0);
      long kl = Packing.packLongBigEndian (key, 2) << 48;

      for (int round = 1; round <= 31; ++ round)
         {
         // Store subkey.
         subkey[round] = ku;

         // Key update.
            // Rotate right 19 bits (= rotate left 61 bits).
         kl >>>= 19;
         kl |= ku << 45;
         ku >>>= 19;
         ku |= kl << 16;
         kl &= 0xFFFF000000000000L;
            // Apply S-box to bits 79..76.
         ku = (Sbox[(int)(ku >>> 60)] << 60) | (ku & 0x0FFFFFFFFFFFFFFFL);
            // Exclusive-or round counter into bits 19..15.
         ku ^= (long)round >>> 1;
         kl ^= (long)round << 63;
         }

      subkey[32] = ku;
      }

   /**
    * Encrypt the given plaintext. <TT>text</TT> must be an array of bytes
    * whose length is equal to <TT>blockSize()</TT>. On input, <TT>text</TT>
    * contains the plaintext block. The plaintext block is encrypted using the
    * key specified in the most recent call to <TT>setKey()</TT>. On output,
    * <TT>text</TT> contains the ciphertext block.
    * <P>
    * For PRESENT, bytes <TT>text[0]</TT> through <TT>text[7]</TT> are used.
    *
    * @param  text  Plaintext (on input), ciphertext (on output).
    */
   public void encrypt
      (byte[] text)
      {
      long data = Packing.packLongBigEndian (text, 0);
      long t;

      // Do 31 rounds.
      for (int round = 1; round <= 31; ++ round)
         {
         // Subkey addition.
         data ^= subkey[round];

         // S-box layer and bit permutation layer via T-box lookup.
         t  = Tbox[ 0][(int)(data       ) & 15];
         t |= Tbox[ 1][(int)(data >>>  4) & 15];
         t |= Tbox[ 2][(int)(data >>>  8) & 15];
         t |= Tbox[ 3][(int)(data >>> 12) & 15];
         t |= Tbox[ 4][(int)(data >>> 16) & 15];
         t |= Tbox[ 5][(int)(data >>> 20) & 15];
         t |= Tbox[ 6][(int)(data >>> 24) & 15];
         t |= Tbox[ 7][(int)(data >>> 28) & 15];
         t |= Tbox[ 8][(int)(data >>> 32) & 15];
         t |= Tbox[ 9][(int)(data >>> 36) & 15];
         t |= Tbox[10][(int)(data >>> 40) & 15];
         t |= Tbox[11][(int)(data >>> 44) & 15];
         t |= Tbox[12][(int)(data >>> 48) & 15];
         t |= Tbox[13][(int)(data >>> 52) & 15];
         t |= Tbox[14][(int)(data >>> 56) & 15];
         t |= Tbox[15][(int)(data >>> 60) & 15];
         data = t;
         }

      // Final subkey addition.
      data ^= subkey[32];

      Packing.unpackLongBigEndian (data, text, 0);
      }

   /**
    * S-box table.
    */
   private static long[] Sbox = new long[]
      { 12, 5, 6, 11, 9, 0, 10, 13, 3, 14, 15, 8, 4, 7, 1, 2 };

   /**
    * Bit permutation table.
    */
   private static int[] P = new int [64];
   static
      {
      for (int i = 0; i <= 62; ++ i) P[i] = (i << 4) % 63;
      P[63] = 63;
      }

   /**
    * T-box tables.
    */
   private static long[][] Tbox = new long [16] [16];
   static
      {
      for (int posn = 0; posn <= 15; ++ posn)
         {
         for (int data = 0; data <= 15; ++ data)
            {
            long s = Sbox[data];
            long t = 0;
            for (int i = 0; i <= 3; ++ i)
               {
               t |= ((s >> i) & 1) << P[posn*4 + i];
               }
            Tbox[posn][data] = t;
            }
         }
      }

   /**
    * Unit test main program. Prints the test vectors from the PRESENT
    * specification.
    */
   public static void main
      (String[] args)
      {
      BlockCipher cipher = new Threefish();

      byte[] plaintextZero = new byte [8];
      byte[] plaintextOne = new byte [8];
      byte[] keyZero = new byte [10];
      byte[] keyOne = new byte [10];
      Arrays.fill (plaintextOne, (byte)0xFF);
      Arrays.fill (keyOne, (byte)0xFF);

      test (cipher, plaintextZero, keyZero);
      test (cipher, plaintextZero, keyOne);
      test (cipher, plaintextOne, keyZero);
      test (cipher, plaintextOne, keyOne);
      }

   private static void test
      (BlockCipher cipher,
       byte[] plaintext,
       byte[] key)
      {
      byte[] ciphertext = (byte[]) plaintext.clone();
      cipher.setKey (key);
      cipher.encrypt (ciphertext);
      System.out.printf ("%s  %s  %s%n",
         Hex.toString (plaintext),
         Hex.toString (key),
         Hex.toString (ciphertext));
      }

   }