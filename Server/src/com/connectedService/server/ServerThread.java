package com.connectedService.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread {

	private final String GET_COMMAND = "get";
	private final String PUT_COMMAND = "put";

	protected Socket socket;

	public ServerThread(Socket clientSocket) {
		this.socket = clientSocket;
	}

	public void run() {
		System.out.println("Servicing Client...");
		DataInputStream in = null;
		DataOutputStream out = null;

		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			String inputLine;
			while (true) {
				try {

					// read command from socket
					inputLine = in.readUTF();

					// if command is quit, then close the socket
					if ((inputLine == null) || inputLine.equalsIgnoreCase("QUIT")) {
						socket.close();
						System.out.println("One Client Disconnected");
						return;
					}
					else {

						int cursor_pos = inputLine.indexOf(' ');
						String ip_cmd = inputLine.substring(0,cursor_pos);

						if(cursor_pos<=0)
						{
							System.out.println("invalid command " + inputLine);
							out.writeUTF("invalid command");
							out.flush();
						}
						// if command is valid proceed
						else {
							if (ip_cmd.equalsIgnoreCase(GET_COMMAND)) {
								String f_name = inputLine.substring(cursor_pos + 1);
								String f_data = FileHandler.readFile(f_name);
								if(f_data == null || f_data.equals(""))
								{
									System.out.println("File NotFound or is Blank");
								}
								else{
									out.writeUTF(f_data);
								}
								out.flush();
								System.out.println("File Sent to Client: " + f_name);
							}
							// if put command specified, then get the file from client
							// and store it on server
							else if (ip_cmd.equalsIgnoreCase(PUT_COMMAND)) {
								String f_name = inputLine.substring(cursor_pos + 1);
								String f_data = in.readUTF();
								if(FileHandler.saveFile(f_name, f_data)){
									System.out.println("File Downloaded on Server: " + f_name);
								}
								else
								{
									System.out.println("Error Occoured while Downloading File");
								}
								out.flush();
							}
						}

					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		} catch (IOException e) {
			return;
		}
	}
}
