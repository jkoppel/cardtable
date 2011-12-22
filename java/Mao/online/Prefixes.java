/**
 *For Mao online; a class that stores the message prefixes of
 *					various message
 *					types as static variables
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/2/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */

package mao.online;

import mao.game.*;

//Mastery aspects: none

/*
 *
 *Due to strange errors, JOIN_REQUEST handling is currently
 *partially disabled;
 *GameServer currently automatically accepts all join requests
 *
 *Also, handling players leaving is not implemented
 */

public class Prefixes
{
	/*
 	*Prefixes of the message types
 	*/
	public final static byte CHAT = 0;
	public final static byte ACTION_PERFORMED = 1;
	public final static byte GAME_STATE_TRANSFER = 2;
//	public final static byte JOIN_REQUEST = 3;
	public final static byte JOIN_REQUEST_ACCEPTED = 4;
//	public final static byte JOIN_REQUEST_REFUSED = 5;
	public final static byte PLAYER_BEING_ADDED = 6;
	public final static byte PLAYER_ADDED = 7;
	//public final static byte PLAYER_LEFT = 8;

}