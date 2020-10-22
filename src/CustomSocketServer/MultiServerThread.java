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
	private HttpResponseGenerator resGenetator = null;
	private String requestMethod;
	private String queryDir;
	private String rootDir;
	private String reqBody;
	private String response;
	private boolean hasOverwrite = false;
	private boolean hasContentLength = false;
	private int contentLen = 0;
	
	
	public MultiServerThread(Socket cSocket, String rootDir) {
		this.clientSocket = cSocket;
		this.resGenetator = HttpResponseGenerator.getResponseObj();
		this.rootDir = rootDir;
	}
	
	public void run() {
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			StringBuilder request = new StringBuilder();
			String line="";
			
			//read request line and header
			do {
				line = in.readLine();
				if(line.contains("GET") || line.contains("POST")) {
					//split the request line by " "
					String[] reqLineArr = line.split(" ");
					this.requestMethod = reqLineArr[0];
					System.out.println("requestMethod: "+this.requestMethod);
					this.queryDir = reqLineArr[1];
					System.out.println("queryDir: "+this.queryDir);
				}
				if(line.contains("Content-Length")) {
					this.hasContentLength = true;
					String[] contentLenArr = line.split(" :");
					this.contentLen = Integer.parseInt(contentLenArr[1]);
					System.out.println("hasContentLength: "+ this.hasContentLength + "contentLen" + this.contentLen);
				}
				if(line.contains("Has-Overwrite")) {
					String[] hasOverwriterArr = line.split(" :");
					this.hasOverwrite = Boolean.parseBoolean(hasOverwriterArr[1]);
					System.out.println("hasOverwrite: "+ this.hasOverwrite);
				}
				if(line.equals("")) {
					break;
				}
				request.append(line);
			}
			while(true);
			
			//if has content length, read request body
			if(this.hasContentLength) {
				char c;
				for(int j=0; j< this.contentLen; j++) {
					c = (char)in.read();
					this.reqBody += c;
				}
			}
			
			//get response
			this.resGenetator.processRequest(this.requestMethod, this.queryDir, this.rootDir, this.hasOverwrite, this.reqBody);
			this.response = this.resGenetator.printResponse();
			
			//print response
			System.out.println(this.response);
			
			//send the response
			out.print(this.response);
			
			if(clientSocket != null) {
				clientSocket.close();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
