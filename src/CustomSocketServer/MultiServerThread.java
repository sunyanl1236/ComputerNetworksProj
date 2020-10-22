package CustomSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MultiServerThread extends Thread{
	private Socket clientSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	public MultiServerThread(Socket cSocket) {
		this.clientSocket = cSocket;
	}
	
	public void run() {
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			String reqIn="", resOut="", readIn="";
			while((readIn=in.readLine()) != null) {
				//print request
				System.out.println(readIn);
				//print response
			}
			
			if(clientSocket != null) {
				clientSocket.close();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
