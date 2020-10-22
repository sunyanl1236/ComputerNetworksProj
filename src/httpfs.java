import java.io.IOException;
import java.net.ServerSocket;

import CustomSocketServer.MultiServerThread;
import CustomSocketServer.ServerCLIImplement;

public class httpfs {

	public static void main(String[] args) {
//		System.out.println("Hello wORLD.");
		
		ServerSocket serverSocket = null;
		
		ServerCLIImplement parseCommand = new ServerCLIImplement(args);
		int portNumber = parseCommand.getFinalPortNum();
		boolean listening = true;
		
		try {
			serverSocket = new ServerSocket(portNumber);
			while(listening) {
				new MultiServerThread(serverSocket.accept(), parseCommand.getRootDirPath()).start();
			}
			
			if(serverSocket != null) {
				serverSocket.close();
			}
		}
		catch(IOException e) {
			System.err.println("Could not listen on port "+ portNumber);
			System.exit(-1);
		}
	}

}
