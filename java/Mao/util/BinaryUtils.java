/**
 *For Mao online; contains some helper methods for manipulating bytes
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/24/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */

package mao.util;

import java.io.*;

//Mastery aspects:
//HL: Parsing a data stream
//SL: 1,4,6,8,9,10,12,14

public class BinaryUtils
{
	//Returns an int containing the first aLen bytes
	//of a followed by the binary value of b
	public static int mergeInts(int a,  int b, int aLen)
	{
		int c = 0;
		c |= a;
		c |= b<<aLen;
		return c;
	}
	
	//Returns an int consisting of len 1s
	public static int onBitMask(int len)
	{
		return (1<<len)-1;
	}
	
	//Returns an int consisting of len 1s starting at the start'th bit
	public static int onBitMask(int start, int len)
	{
		return onBitMask(len)<<start;
	}
	
	//Returns the byte in a starting at start
	public static byte extractByte(int a, int start)
	{
		return (byte)(0xFF&(a>>start));
	}
	
	//Returns the byte in a starting at start
	public static byte extractByteFromLong(long a, int start)
	{
		return (byte)(((long)0xFF)&(a>>start));
	}
	
	//Converts the boolean into a byte
	public static byte booleanToBit(boolean b)
	{
		if(b)
			return 1;
		else
			return 0;
	}
	
	//Converts the byte into a boolean
	public static boolean bitToBoolean(byte b)
	{
		if(0 == b)
			return false;
		else
			return true;
	}
	
	//Reads and returns an ASCII string from bytes starting at off
	//of length len
	public static String asASCIIString(byte[] bytes, int off, int len)
	{
		char[] chars = new char[len];
		for(int i = 0; i < len; i++)
			chars[i] = (char)bytes[off+i];
		
		return new String(chars);
	}
	
	//From the InputStream, reads ASCII characters until
	//it reaches a null,
	//then returns the characters read as a String
	public static String readNullTerminatedString(InputStream str)
													throws IOException
	{
		StringBuffer buff = new StringBuffer();
		char c;
		
		while((c=(char)str.read())!='\000')
			buff.append(c);
		
		return buff.toString();
	}
	
	/*
	 *Interprets bytes[off],...,bytes[off+len] as an integer,
	 *with more significant bytes stored first (i.e.: big endian).
	 */
	public static int asBigEndianInt(byte[] bytes, int off, int len)
	{
		int n = 0;
		for(int i = 0; i < len; i++)
			//Java bytes are signed; the & is to convert the byte
			//into an int with the same binary value,
			//rather than the same numerical value
			n |= (0xFF&bytes[off+i]) << ((len-i-1)*8);
		return n;
	}
	
	/*
	 *Converse of the previous method: places the lowest len bytes of n in big
	 *endian format (most significant bytes first) in dest starting at off
	 */
	public static void storeAsBytes(int n, byte[] dest, int off, int len)
	{
		for(int i = 0; i < len; i++)
		{
			dest[off+i] = extractByte(n, (len-i-1)*8);
		}
	}
	
	/*
	 *Interprets bytes[off],bytes[off+len] as a long,
	 *with more significant bytes stored first (i.e.: big endian).
	 */
	public static long asBigEndianLong(byte[] bytes, int off, int len)
	{
		long n = 0;
		for(int i = 0; i < len; i++)
		{
			//Java bytes are signed; the & is to convert the byte into an int
			//with the same binary value,
			//rather than the same numerical value
			n |= (((long)0xFF)&bytes[off+i]) << ((len-i-1)*8);
		}
		return n;
	}
	
	/*
	 *Converse of the previous method: places the lowest len
	 *bytes of n in big endian format (most significant bytes first)
	 *in dest starting at off
	 */
	public static void storeLongAsBytes(long n, byte[] dest, int off, int len)
	{
		for(int i = 0; i < len; i++)
		{
			dest[off+i] = extractByteFromLong(n, (len-i-1)*8);
		}
	}
	
	/*
	 *Stores up to 8 booleans in a single byte
	 *
	 *First boolean stored in rightmost bit, second in second-to-right, etc
	 */
	public static byte flagsToByte(boolean... flags)
	{
		if(flags.length > 8)
			throw new IllegalArgumentException(
						"Can only fit 8 flags in a bit.");
		
		byte b = 0;
		for(int i = 0; i < flags.length; i++)
			b |= booleanToBit(flags[i])<<i;
		
		return b;
	}
}