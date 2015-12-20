package com.Twomey.TheAlmightyMessenger;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client implements ActionListener{

	private JFrame ClientFrame;
	private JPanel ControlPanel;
	private JPanel ChatPanel;
	private JTextField InputText;
	private JTextArea ChatText;
	private JButton Connect;
	private Socket Connection;
	private ObjectInputStream Input;
	private ObjectOutputStream Output;
	
 	private JScrollBar ChatTextVerticleScrollBar;
	
	
	public static void main(String[] args) {
		new Client();
	}
	
	public Client()
	{
		ClientFrame = new JFrame("The Almighty Messenger");
		ControlPanel = new JPanel();
		ChatPanel = new JPanel();
		
		InputText = new JTextField();
		InputText.setSize(350, 25);
		InputText.setEditable(false);
		InputText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						sendMessage(event.getActionCommand());
						InputText.setText("");
					}
				}
			);
		
		ChatText = new JTextArea(7,30);
		ChatText.insert("\n\nChat Area:\n", 0);
		ChatText.setEditable(false);
		ChatText.setLineWrap(true);
		
		ChatText.add(InputText, BorderLayout.SOUTH);
		
		JScrollPane ChatTextScrollPane = new JScrollPane(ChatText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ChatPanel.add(ChatTextScrollPane, BorderLayout.SOUTH);
		
		ChatTextVerticleScrollBar = ChatTextScrollPane.getVerticalScrollBar();
		
		Connect = new JButton("Connect");
		Connect.addActionListener(this);
		
		ControlPanel.add(Connect);
		
		ClientFrame.setLayout(new BorderLayout());
		ClientFrame.add(ControlPanel, BorderLayout.NORTH);
		ClientFrame.add(ChatPanel, BorderLayout.SOUTH);
		ClientFrame.setSize(400, 300);
		ClientFrame.setVisible(true);
		ClientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void RunConnection() {
		new Thread(){
			public void run(){
				try{
					connectToServer();
					whileChatting();
				}		
				catch(IOException ioException){
					ioException.printStackTrace();
				}finally{
					closeConnection();
				}
			}
		}.start();
	}
	
	private void connectToServer() {
		try{
			Connection = new Socket("localhost", 1738);
			Output = new ObjectOutputStream(Connection.getOutputStream());
			Output.flush();
			Input = new ObjectInputStream(Connection.getInputStream());
			System.out.println("Connected");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	private void displayMessage(String message) {
		ChatText.append("\n" + message);
	}
	
	private void sendMessage(String message) {
		try{
			Output.writeObject("CLIENT - " + message);
			Output.flush();
			ChatText.append("\nCLIENT - " + message);
		}catch(IOException ioException){
			ChatText.append("\nERROR COULD NOT SEND MESSAGE");
		}
		ChatTextVerticleScrollBar.setValue(ChatTextVerticleScrollBar.getMaximum());
	}
	
	private void closeConnection() {
		try{
			Output.close();
			Input.close();
			Connection.close();
			InputText.setEditable(false);
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	private void whileChatting() throws IOException {
		String message = "You are now connected!";
		displayMessage(message);
		InputText.setEditable(true);
		do{
			try{
				message = (String) Input.readObject();
				sendMessage(message);
			}catch(ClassNotFoundException classNotFoundException) {
				displayMessage("Could not read message");
			}
		}while(!message.equals("SERVER - END"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == Connect)
		{
			RunConnection();
		}
		
	}

}
