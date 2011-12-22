/**
 *For Mao online; used to request to join a game
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 3/18/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.online;
 
 import java.awt.*;
 import java.awt.event.*;
 import javax.swing.*;
 import java.net.*;
 import java.io.IOException;
 
 //Mastery aspects:
 //HL: encapsulation, inheritance
 //SL: 2,3,8,9,14
 
 public class JoinRequestPanel extends JDialog implements ActionListener
 {
 	private JTextField host; //asks for the IP to connect to
 	private JTextField name; //asks for the handle to connect using
 	private JButton submit; //initiates the sequence to connect to the server
 	
 	/*
 	 *Creates a dialog to connect a server
 	 */
 	public JoinRequestPanel(Frame owner)
 	{
 		super(owner, true);
 		GridBagLayout layout = new GridBagLayout();
 		setLayout(layout);
 		
 		
 		host = new JTextField(30);
 		
 		//host should start with the last-connected-to
 		//IP already filled in
 		InetAddress defIP = Preferences.getDefaultIP();
 		if(null!=defIP)
 			host.setText(defIP.getHostAddress());
 		
 		//name should start with the last-used name already filled in
 		name = new JTextField(Preferences.getDefaultName(),
 								Preferences.MAX_NAME_LENGTH);
 		submit = new JButton("Connect");
 		
 		JLabel label;
 		
 		GridBagConstraints c = new GridBagConstraints();
 		
 		c.gridwidth = GridBagConstraints.RELATIVE;
 		add((label=new JLabel("Name: ")));
 		layout.setConstraints(label, c);
 		
 		c.gridwidth = GridBagConstraints.REMAINDER;
 		add(name);
 		layout.setConstraints(name, c);
 		
 		c.gridwidth = GridBagConstraints.RELATIVE;
 		add((label=new JLabel("Host: ")));
 		layout.setConstraints(label, c);
 		
 		c.gridwidth = GridBagConstraints.REMAINDER;
 		add(host);
 		layout.setConstraints(host, c);
 		
 		add(submit);
 		layout.setConstraints(submit, c);
 		
 		submit.addActionListener(this);
 	}
 	
 	//When submit is hit, creates a GameClient to connect
 	//to the server
 	//and sets that the used IP and name should be the
 	//default next time
 	public void actionPerformed(ActionEvent e)
 	{
 		try
 		{
 			InetAddress ip = InetAddress.getByName(host.getText());
 			String handle = name.getText();
 			Preferences.setDefaultIP(ip);
 			Preferences.setDefaultName(handle);
 			
 			GameClient.destroyInstance();
 			GameClient.makeInstance(ip, GameClient.DEFAULT_PORT, handle);
 		}
 		catch(UnknownHostException f)
 		{
 			JOptionPane.showMessageDialog(this, "Unknown host or invalid format.");
 		}
 		catch(IOException f)
 		{
 			JOptionPane.showMessageDialog(this, "Trouble connecting to server.");
 		}
 		finally
 		{
 			//closes this dialog
 			dispose();
 		}
 	}
 }