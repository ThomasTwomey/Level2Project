package com.Twomey.TheAlmightyMessenger;
import java.awt.Button;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Server {

	JFrame ServerFrame;
	JPanel ControlPanel;
 	JLabel LogTextLabel;
	JScrollPane LogTextScrollPane;
	
	public static void main(String[] args) {
		new Server();
	}
	
	Server()
	{
		ServerFrame = new JFrame("The Almighty Messenger Server");
		LogTextLabel = new JLabel("Server Created\n");
		
		ServerFrame.setVisible(true);
		ServerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		LogTextLabel.setPreferredSize(new Dimension(1000,1000));
		
		JTable table = new JTable(1, 1);
		LogTextScrollPane = new JScrollPane(table);
		LogTextScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new Button("..."));
		
		ServerFrame.add(LogTextScrollPane);
		ServerFrame.setSize(350, 300);
	}

}
