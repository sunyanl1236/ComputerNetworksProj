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
	private boolean hasDebugMsg;
	private int contentLen = 0;
	
	
	public MultiServerThread(Socket cSocket, String rootDir, boolean hasDebugMsg) {
		this.clientSocket = cSocket;
		this.resGenetator = HttpResponseGenerator.getResponseObj();
		this.rootDir = rootDir;
		this.hasDebugMsg = hasDebugMsg;
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
					
					//test
					System.out.println("print reqLineArr\n");
					for(String ss : reqLineArr) {
						System.out.println(ss);
					}
					
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
				request.append(line).append("\r\n");
			}
			while(true);
			
			//if has content length, read request body
			if(this.hasContentLength) {
				System.out.println("has request body.");
				char c;
				for(int j=0; j< this.contentLen; j++) {
					c = (char)in.read();
					this.reqBody += c;
				}
				request.append("\r\n").append(this.reqBody);
			}
			
			//print request
			if(this.hasDebugMsg) {
				System.out.println("\n\nPrint the request:");
				System.out.println(request.toString());
			}
			
			//get response
			this.resGenetator.processRequest(this.requestMethod, this.queryDir, this.rootDir, this.hasOverwrite, this.reqBody);
			this.response = this.resGenetator.printResponse();
			
			//print response //test
			if(this.hasDebugMsg) {
				System.out.println("\n\nPrint the response:");
				System.out.println(this.response);
			}
			
			//send the response
			out.print(this.response);
			out.flush();
//			out.close();
			
			if(clientSocket != null) {
//				System.out.println("Close the client Socket");
				clientSocket.close();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
