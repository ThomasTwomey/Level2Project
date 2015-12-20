package com.Twomey.TheAlmightyMessenger;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class Server implements ActionListener {
	private JFrame ServerFrame;
	private JPanel ControlPanel;
	private JPanel LogPanel;
	private JPanel ChatPanel;
 	private JTextArea LogText;
 	private JTextArea ChatText;
 	private JTextField InputText;
 	private JButton StartButton;
 	private JButton StopButton;
 	private JScrollBar ChatTextVerticleScrollBar;
 	
 	private ObjectOutputStream Output;
 	private ObjectInputStream Input;
 	private ServerSocket Server;
 	private Socket Connection;
	
	public static void main(String[] args) {
		new Server();
	}
	
	public Server() {
		ServerFrame = new JFrame("The Almighty Messenger Server");
		LogPanel = new JPanel();
		ChatPanel = new JPanel();
		ControlPanel = new JPanel();
		
		StartButton = new JButton("Start");
		StopButton = new JButton("Stop");
		StartButton.addActionListener(this);
		StopButton.addActionListener(this);
		
		ControlPanel.add(StartButton);
		ControlPanel.add(StopButton);
		
		LogText = new JTextArea(6,30);
		LogText.insert("Server Log Area:\nApplication Launched", 0);
		LogText.setEditable(false);
		LogText.setLineWrap(true);
		
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
		
		ChatText.add(InputText, BorderLayout.NORTH);
		
		JScrollPane ChatTextScrollPane = new JScrollPane(ChatText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ChatPanel.add(ChatTextScrollPane, BorderLayout.SOUTH);
		
		ChatTextVerticleScrollBar = ChatTextScrollPane.getVerticalScrollBar();
		
		JScrollPane LogTextScrollPane = new JScrollPane(LogText,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		LogPanel.add(LogTextScrollPane);
		
		ServerFrame.setLayout(new BorderLayout());
		ServerFrame.add(ControlPanel, BorderLayout.NORTH);
		ServerFrame.add(ChatPanel, BorderLayout.CENTER);
		ServerFrame.add(LogPanel, BorderLayout.SOUTH);
		ServerFrame.setSize(400, 350);
		ServerFrame.setVisible(true);
		ServerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void startServer() {
		new Thread(){
			public void run(){
				try{
				Server = new ServerSocket(1738, 100);
				displayLogMessage("Server socket bound");
					while(true){
						waitForConnection();
						setupStreams();
						whileChatting();
					}
				}		
				catch(IOException ioException){
					ioException.printStackTrace();
				}finally{
					closeConnection();
				}
		displayLogMessage("Connection Established");
			}
		}.start();
	}
	
	private void waitForConnection() throws IOException{
		displayLogMessage("Waiting for connection");
		Connection = Server.accept();
		displayLogMessage("Now Connected to " + Connection.getInetAddress().getHostName());
	}
	
	private void setupStreams() throws IOException {
		Output = new ObjectOutputStream(Connection.getOutputStream());
		Output.flush();
		Input = new ObjectInputStream(Connection.getInputStream());
		displayLogMessage("Connection Established");
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
	
	private void closeConnection() {
		displayLogMessage("Closing connections...");
		try{
			Output.close();
			Input.close();
			Connection.close();
			InputText.setEditable(false);
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
		displayLogMessage("Connections closed");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == StartButton) {
			startServer();
		}
		else if(e.getSource() == StopButton) {
			closeConnection();
		}
		
	}
	
	private void displayLogMessage(String message) {
		LogText.append("\n" + message);
	}
	
	private void displayMessage(String message) {
		ChatText.append("\n" + message);
	}
	
	private void sendMessage(String message) {
		try{
			Output.writeObject("SERVER - " + message);
			Output.flush();
			if(message.contains("CLIENT -"))
			{
				ChatText.append("\n" + message);
			}
			else {
				ChatText.append("\nSERVER - " + message);
			}
			ChatTextVerticleScrollBar.setValue(ChatTextVerticleScrollBar.getMaximum());
		}catch(IOException ioException){
			ChatText.append("\nERROR COULD NOT SEND MESSAGE");
		}
	}

}
