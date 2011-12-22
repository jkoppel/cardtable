/**
 *For Mao online; stores and accesses preferences about connection
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 3/18/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.online;
 
 import mao.util.*;
 import java.io.*;
 import java.net.*;
 import javax.swing.JOptionPane;
 
 //Mastery aspects:
 //HL: Adding data to a RandomAccessFile, finding data from a RandomAccessFile,
 //    parsing a file
 //SL: 1,3,4,8,9,10,13
 
 public class Preferences
 {
 	//Maximum length of the name a player may have
 	public final static int MAX_NAME_LENGTH = 30;
 	
 	//The default IP is stored in the file first
 	private final static int DEFAULT_IP = 0;
 	//The default name is stored in the file second
 	private final static int DEFAULT_NAME = 1;
 	
 	//At which byte in the file the properties should begin
 	//The final number is the index of the end of the file
 	private final static int[] offsets = {0, 4, 34};
 	
 	//The file in which prefences are stored
 	private static RandomAccessFile prefFile;
 	
 	/*
 	 *Loads the preferences file. If it must be created or if it
 	 *was created with an older version that had less preferences,
 	 *then it initializesthe rest of the file to blank values
 	 */
 	static
 	{
 		try
 		{
 			prefFile = new RandomAccessFile("pref.dat","rwd");
 			/*
 			 *If file is too small
 			 */
 			if(prefFile.length() < offsets[offsets.length-1]) 
 			{
 				//Then initialize the rest of the file by zeroing it out.
 				prefFile.seek(prefFile.length());
 				for(int i =(int) prefFile.length(); i <
 										offsets[offsets.length-1]; i++)
 					prefFile.writeByte(0);
 			}
 		}
 		catch(IOException e)
 		{
 			fileError();
 		}
 	}
 	
 	//Called signify there was an error with the file
 	private static void fileError()
 	{
 		JOptionPane.showMessageDialog(null,
 					"Error reading from or writing to preferences file.");
 		System.exit(0);
 	}
 	
 	//In a general fashion, gets the property from the file
 	//with index prop
 	//Uses the offets field to know where and how long the
 	//property is stored
 	public static byte[] getProperty(int prop)
 	{
 		try
 		{
 			prefFile.seek(offsets[prop]);
 			int len = offsets[prop+1]-offsets[prop];
 			byte[] b = new byte[len];
 			prefFile.readFully(b, 0, len);
 			return b;
 		}
 		catch(IOException e)
 		{
 			fileError();
 			return null;
 		}
 	}
 	
 	//In a general fashion, sets the property in the file
 	//with index prop
 	//Uses the offets field to know where and how long the
 	//property is stored
 	public static void setProperty(int prop, byte[] val)
 	{
 		try
 		{
 			prefFile.seek(offsets[prop]);
 			int len = offsets[prop+1]-offsets[prop];
 			if(val.length > len)
 				throw new IllegalArgumentException();
 			else if(val.length < len)
 			{
 				byte[] oldVal = val;
 				val = new byte[len];
 				System.arraycopy(oldVal,0,val,0,oldVal.length);
 			}
 			prefFile.write(val);
 		}
 		catch(IOException e)
 		{
 			fileError();
 		}
 	}
 	
 	//Convenience method for getting the default IP from the file
 	public static InetAddress getDefaultIP()
 	{
 		byte[] b = getProperty(DEFAULT_IP);
 		try
 		{
 			return InetAddress.getByAddress(b);
 		}
 		catch(UnknownHostException e)
 		{
 			return null;
 		}
 	}
 	
 	//Convenience method for setting the default IP in the file
 	public static void setDefaultIP(InetAddress ip)
 	{
 		setProperty(DEFAULT_IP, ip.getAddress());
 	}
 	
 	//Convenience method for getting the default name from the file
 	//Name is stored as a C-style (null-terminated) string
 	public static String getDefaultName()
 	{
 		return new String(getProperty(DEFAULT_NAME)
 								).replace("\000","");
 	}
 	
 	//Convenience method for setting the default name in the file
 	//Name is stored as a C-style (null-terminated) string
 	public static void setDefaultName(String name)
 	{
 		setProperty(DEFAULT_NAME, name.getBytes());
 	}
 }