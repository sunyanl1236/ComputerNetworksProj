import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import CustomSocketServer.MultiServerThread;
import CustomSocketServer.ServerCLIImplement;

public class httpfs {

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		
		ServerCLIImplement parseCommand = new ServerCLIImplement(args);
		int portNumber = parseCommand.getFinalPortNum();
		boolean listening = true;
		
		try {
			serverSocket = new ServerSocket(portNumber);
			while(listening) {
				Socket cs = serverSocket.accept();
				new MultiServerThread(cs, parseCommand.getRootDirPath(), parseCommand.getHasDebugMsg()).start();
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
