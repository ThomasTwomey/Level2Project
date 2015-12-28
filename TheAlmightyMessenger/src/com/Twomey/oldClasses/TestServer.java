package com.Twomey.oldClasses;

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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TestServer implements ActionListener{
	JFrame frame;
	JPanel controlPanel;
	JPanel chatPanel;
	JButton start;
	JButton stop;
	JTextArea chatText;
	JTextArea inputTextArea;
	JTextField inputText;
	
 	private ObjectOutputStream output;
 	private ObjectInputStream input;
 	private ServerSocket server;
 	private Socket connection;
	
	public static void main(String[] args) {
		new TestServer();
	}

	TestServer() {
		frame = new JFrame("Server");
		controlPanel = new JPanel();
		chatPanel = new JPanel();
		start = new JButton("Start");
		stop = new JButton("Stop");
		
		start.addActionListener(this);
		stop.addActionListener(this);
		
		controlPanel.add(start);
		controlPanel.add(stop);
		
		inputText = new JTextField();
		inputText.setSize(350, 25);
		inputText.setEditable(true);
		inputText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						sendMessage(event.getActionCommand());
						inputText.setText("");
					}
				}
			);
		
		inputTextArea = new JTextArea(2,22);
		inputTextArea.setEditable(false);
		inputTextArea.setLineWrap(true);
		inputTextArea.add(inputText,BorderLayout.CENTER);
		
		chatPanel.add(inputTextArea, BorderLayout.NORTH);
		
		chatText = new JTextArea(10,22);
		chatText.setEditable(false);
		chatText.setLineWrap(true);
		
		JScrollPane chatTextScrollPane = new JScrollPane(chatText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		chatPanel.add(chatTextScrollPane, BorderLayout.SOUTH);
		
		frame.setLayout(new BorderLayout());
		frame.add(controlPanel, BorderLayout.NORTH);
		frame.add(chatPanel, BorderLayout.SOUTH);
		
		frame.setSize(300, 250);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	
	boolean isConnected = false;
	
	public void startServer() {
		new Thread() {
			public void run() {
				try {
				server = new ServerSocket(1738, 100);
				showMessage("Server socket bound");
				while(!isConnected) {
					waitForConnection();
					setupStreams();
				}
				showMessage("Connection established");
				whileChatting();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}.start();
}
	
	private void waitForConnection() throws IOException{
		showMessage("Waiting for client to connect");
		connection = server.accept();
		showMessage("Client accepted connection");
	}
	
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		isConnected = true;
	}
	
	private void showMessage(String message) {
		chatText.append("\n" + message);
	}
	
	private void sendMessage(String message) {
		try{
			output.writeObject("\nSERVER - " + message);
			output.flush();
			chatText.append("\nSERVER - " + message);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	private void whileChatting() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == start) {
			startServer();
		}
		else if(e.getSource() == stop) {
			
		}
	}
	
}