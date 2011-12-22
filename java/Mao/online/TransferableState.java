/**
 *For Mao online; interface  denoting a serializable class that can
 *				be sent over the Internet and recreates the table
 *                state on the other end
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 2/02/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.online;
 
 import java.io.Serializable;
 
 //Mastery aspects:
 //HL: inheritance
 //SL: 8,14
 
 public interface TransferableState extends Serializable
 {
 	//Does everything needed to transform the local state
 	//to match the stored state
 	public void loadState();
 }