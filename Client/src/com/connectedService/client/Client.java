package com.connectedService.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
	private final static String GET_COMMAND = "get";
	private final static String PUT_COMMAND = "put";

	public static void main(String[] args) {
		String ip_line = null;
		String host = null;
		int port = -1;
		try {

			BufferedReader user_input = new BufferedReader(new InputStreamReader(System.in));

			// get server ip and port from user
			System.out.print("Enter Server domain or ip: ");
			host = user_input.readLine();
			System.out.print("Enter Server Port: ");
			try{
				port = Integer.parseInt(user_input.readLine());
			}
			catch(Exception e)
			{
				System.out.println("Please enter valid port...Exiting...");
				return;
			}

			Socket clientSocket = new Socket(host, port);

			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());

			// print menu for user
			printMenu();
			while (true) {
				try {
					System.out.print(">");
					// read command from user
					ip_line = user_input.readLine();

					// if input is quit, then exit socket and quit program
					if ((ip_line == null) || ip_line.equalsIgnoreCase("QUIT")) {
						out.writeUTF(ip_line);
						clientSocket.close();
						return;
					}
					// if command other then quit
					else {

						int cursor_pos = ip_line.indexOf(' ');
						String ip_cmd = ip_line.substring(0,cursor_pos);

						if(cursor_pos<=0){
							System.out.println("invalid command " + ip_line);
							out.writeUTF("invalid command");
							out.flush();
						}
						else {
							// if get command is passed by user
							// and download the file from server
							if (ip_cmd.equalsIgnoreCase(GET_COMMAND)) {
								String f_name = ip_line.substring(cursor_pos + 1);
								out.writeUTF(ip_line);

								String f_data = in.readUTF();
								if(FileHandler.saveFile(f_name, f_data)){
									System.out.println("File Downloaded on Client Machine: " + f_name);
								}
								else
								{
									System.out.println("Error in Downloading File");
								}
								out.flush();
							}
							// if put command is passed, then pass the command
							// and upload the file to server
							if (ip_cmd.equalsIgnoreCase(PUT_COMMAND)) {
								String f_name = ip_line.substring(cursor_pos + 1);
								String f_data = FileHandler.readFile(f_name);
								out.writeUTF(ip_line);

								if(f_data == null || f_data.equals(""))
								{
									System.out.println("File NotFound or is Blank");
								}
								else{
									out.writeUTF(f_data);
								}
								out.flush();
								System.out.println("File Commited on Server Machine: " + f_name);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}

	private static void printMenu() {
		System.out.println("Usage : ");
		System.out.println("1) get <f_name> ");
		System.out.println("2) put <f_name> ");
		System.out.println("3) quit ");
		System.out.println();
	}
}
