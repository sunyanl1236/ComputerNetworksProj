package CustomSocketServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
			
			try {
				if(Files.isDirectory(path)) { //check if the path is a dir
//				System.out.println("It's a directory.");
					if(Files.isReadable(path)) { //if readable
						//print all the dir and files in this path
						DirectoryStream<Path> dirStream = Files.newDirectoryStream(path);
						for (Path entry : dirStream) {
							this.statusCode = 200;
							resBody += "The request path is a directory, print all files and directories in the path: ";
							resBody += (entry.getFileName().toString()+"/n");  
						}
						
						//add Content-Length header
						resHeader.put("Content-Length", Integer.toString(this.resBody.length()));
						
						//add Content-Type header
						resHeader.put("Content-Type", "text/plain");
						
						System.out.println(resBody);
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
						
						//add Content-Length header
						resHeader.put("Content-Length", Integer.toString(this.resBody.length()));
						//add Content-Type header
						resHeader.put("Content-Type", mimeType);
						
						System.out.println(resBody);
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
		
		//*** implement overwrite options in Client
		public void processPostReq(String queryDir, String rootDir, boolean hasOverwrite) {
			
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
