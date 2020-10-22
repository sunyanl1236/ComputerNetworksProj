package CustomSocketServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import CustomSocketClient.HttpRequestGenerator;

public class HttpResponseGenerator {
	/* Using singleton pattern to create one and only one request for each command line
	 * */
		private static HttpResponseGenerator res = new HttpResponseGenerator();
		private int statusCode;
		private HashMap<String, String> resHeader = new HashMap<>();
		private HashMap<Integer, String> statusCodePhrase = new HashMap<>();
		private String resBody="";
		
		//constructor
		private HttpResponseGenerator() {
			statusCodePhrase.put(200, "200 OK");
			statusCodePhrase.put(204, "204 No Content");
			statusCodePhrase.put(404, "404 Not Found");
			statusCodePhrase.put(201, "201 Created");
			statusCodePhrase.put(400, "400 Bad Request");
			statusCodePhrase.put(403, "403 Forbidden ");
			
			//check content in statusCodePhrase
			for(Map.Entry<Integer, String> kv : statusCodePhrase.entrySet()) {
				System.out.println("key: "+ kv.getKey() + " value: "+ kv.getValue());
			}
		}
		
		public static HttpResponseGenerator getResponseObj() {
			return res;
		}
		
		public void setReqHeader(HashMap<String, String> resHeaderKeyVal) {
			this.resHeader = resHeaderKeyVal;
		}
		
		public void setResBody(String body) {
			this.resBody = body;
		}
		
		public void setStatusCode(int codeNum) {
			this.statusCode = codeNum;
		}
		
		public int getStatusCode() {
			return this.statusCode;
		}
		
		//return the status code
		public void processRequest(String requestMethod,String queryDir, String rootDir, boolean hasOverwrite) {
			if(requestMethod.toUpperCase().equals("GET")) {
				processGetReq(queryDir, rootDir);
			}
			else if(requestMethod.toUpperCase().equals("POST")) {
				
			}
			else {
				this.statusCode = 400;
			}
		}
		
		//return the status code
		public void processGetReq(String queryDir, String rootDir) {
			Path path = Paths.get(rootDir+queryDir);
			System.out.println("Full path is "+rootDir+queryDir);
			
			//check secure access, if go outside of the root directory, return directly
			if(!checkSecureAccess(queryDir)) {
				return;
			}
			
			try {
				if(Files.isDirectory(path)) { //check if the path is a dir
//				System.out.println("It's a directory.");
					if(Files.isReadable(path)) { //if readable
						//print all the dir and files in this path
						DirectoryStream<Path> dirStream = Files.newDirectoryStream(path);
						for (Path entry : dirStream) {
							resBody += "The request path is a directory, print all files and directories in the path: ";
							resBody += (entry.getFileName().toString()+"/n");  
						}
						
						if(!this.resBody.isEmpty()) {
							this.statusCode = 200;
							
							//add Content-Length header
							resHeader.put("Content-Length", Integer.toString(this.resBody.length()));
							
							//add Content-Type header
							resHeader.put("Content-Type", "text/plain");
							
							System.out.println(resBody);
						}
						else {
							this.statusCode = 204;
							System.out.println("empty file");
						}
					}
					else { //if not readable
						this.statusCode = 403;
						System.out.println("Dir not readable.");
					}
				}// end check dir
				else if(Files.isRegularFile(path)) { //check if the path is a file
					if(Files.isReadable(path)) { //check permission of a file
						System.out.println("Is a file.");
						String mimeType = Files.probeContentType(path);
						
//						BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
//						FileTime lastModified = attr.lastModifiedTime();
//						DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
//						String dataString = formatter.withZone(ZoneId.systemDefault()).format(FileTime.from(0, TimeUnit.MILLISECONDS).toInstant());
						
						//read the file content
						File f = path.toFile();
						BufferedReader br = new BufferedReader(new FileReader(f));
						StringBuilder sb = new StringBuilder();
						String line;
						
						while((line = br.readLine()) != null) {
							sb.append(line);
							sb.append(System.lineSeparator());
				        }
						
						this.resBody = sb.toString();
						
						if(!this.resBody.isEmpty()) {
							this.statusCode = 200;
							
							//add Content-Length header
							resHeader.put("Content-Length", Integer.toString(this.resBody.length()));
							//add Content-Type header
							resHeader.put("Content-Type", mimeType);
							
							System.out.println(resBody);
						} else {
							this.statusCode = 204;
							System.out.println("empty file");
						}
							
						
					}
					else {
						this.statusCode = 403;
						System.out.println("File not readable.");
					}
				} //end check file
				else { //cannot open the path
					this.statusCode = 404;
					System.out.println("Cannot open the path");
				}
			}
			catch(NotDirectoryException e) {
				System.out.println("Dir E in processGetReq");
				e.printStackTrace();
			}
			catch(IOException e) {
				System.out.println("IO E in processGetReq");
				e.printStackTrace();
			}
		}
		
		//*** 
		public void processPostReq(String queryDir, String rootDir, boolean hasOverwrite, String reqBody) {
			//check secure access, if go outside of the root directory, return directly
			if(!checkSecureAccess(queryDir)) {
				return;
			}
			
			String fullPath = rootDir;
			String[] splittedQueryDir = queryDir.split("/");
			for(int i=0; i< splittedQueryDir.length; i++) {
				fullPath = rootDir+File.separator+splittedQueryDir[i];
				Path p = Paths.get(fullPath);
				
				if(i != splittedQueryDir.length-1) { //if not the last elt --> dir
					//check if it's Dir (everthing in between the root folder and last element should be directory)
					if(	Files.isDirectory(p)) { //dir exists
						//if the dir is readable, go to the dir
						//if the dir is not readable, end function
						if(!Files.isReadable(p)) { 
							this.statusCode = 403;
							System.out.println("Cannot read the dir.");
							return;
						}
					}
					else {//if dir not exists, create the folder
						try {
							System.out.println(p.toString()+ "doesn't exist, create the dir.");
							Files.createDirectory(p);
						} catch (IOException e) {
							System.out.println("e createDirectory");
							e.printStackTrace();
						}
					}
				}
				else { //if is last elt --> file
					try {
						if(	Files.isRegularFile(p)) { //file exists
							//check permission
							if(!Files.isWritable(p)) {
								this.statusCode = 403;
								System.out.println("Cannot write the file.");
								return;
							}
						}
						else { //file doesn't exist, create file
							Files.createFile(p);
						}
						
						FileWriter fw;
						File f = p.toFile();
						fw = new FileWriter(f);
						fw.write(reqBody);
						fw.close();
						System.out.println("Successfully wrote to the file.");
						
						//set if anyone can overwrite it
						f.setWritable(hasOverwrite);
					} 
					catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
			
		}
		
		/* if the queryDir starts with /.., return false
		 * if the queryDir is /dir1/../../.. return false
		 * otherwise, return true
		 * */
		public boolean checkSecureAccess(String queryDir) {
			int countDirFile = 0;
			int countUpOneFolder = 0;
			String[] splittedDir = queryDir.split("/");
			
			if(splittedDir.length > 0) {
				//case 1: rootDir/..
				if(splittedDir[0].equals("..")) {
					this.statusCode = 403;
					return false;
				}
				
				//loop through the string array and check secure access
				for(String s : splittedDir) {
					//first string s is dir or file
					if(s.equals("..")) {
						countUpOneFolder++;
					} else {
						countDirFile++;
					}
					
					/* case 2:
					 * rootDir/dir/../.. --> outside of rootDir
					 * rootDir/dir/../dir/../.. --> outside of rootDir
					 * */
					if(countUpOneFolder>countDirFile) {
						this.statusCode = 403;
						return false;
					}
				}
			}
			
			return true;
		}
		
		public String printResponse() {
			StringBuilder sb = new StringBuilder();
			//status line
			sb.append("HTTP/1.0 ").append(this.statusCodePhrase.get(this.statusCode)).append("\r\n");
			
			//response header line
			//traverse the HashMap and append the key-val pair to sb
			for(Map.Entry<String, String> entry : resHeader.entrySet()) {
				sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
			}
			
			//Date header
			LocalDate date = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
			String dataString = date.format(formatter);
			sb.append("Date: ").append(dataString).append("\r\n");
     
			sb.append("Server: Concordia Server-HTTP/1.0\r\n");
			sb.append("MIME-version: 1.0\r\n");
			
			//blank line
			sb.append("\r\n");
			
			//entity
			if(!this.resBody.isEmpty()) {
				sb.append(this.resBody);
			}
			return sb.toString();
		}
}
