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
	private boolean hasOverwrite;
	
	
	public MultiServerThread(Socket cSocket, String rootDir) {
		this.clientSocket = cSocket;
		this.resGenetator = HttpResponseGenerator.getResponseObj();
		this.rootDir = rootDir;
	}
	
	public void run() {
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			String reqIn="", readIn="";
			while((readIn=in.readLine()) != null) {
				//print request
				System.out.println(readIn);
				reqIn += readIn;
			}
			
			//parse requestMethod, queryDir, reqBody from request
			parseRequest(reqIn);
			
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
	
	public void parseRequest(String req) {
		String[] reqArr = req.split("\r\n");
		
		//test
		for(String ss : reqArr) {
			System.out.println("check req array");
			System.out.println(ss);
			
			if(ss.contains("Has-Overwrite")) {
				String[] headerArr = ss.split(" :");
				this.hasOverwrite = Boolean.parseBoolean(headerArr[1]);
			}
		}
		
		//split the request line by " "
		String[] reqLineArr = reqArr[0].split(" ");
		this.requestMethod = reqLineArr[0];
		System.out.println("requestMethod: "+this.requestMethod);
		this.queryDir = reqLineArr[1];
		System.out.println("queryDir: "+this.queryDir);
		
		
		if(this.requestMethod.toUpperCase().equals("POST")) {
			this.reqBody = reqArr[reqArr.length-1];
		}
	}
}
