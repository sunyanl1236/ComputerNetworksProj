package CustomSocketServer;

import java.time.LocalDate;
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
		private String resBody;
		
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
		
		public void processRequest(String queryDir) {
			
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
			
/* ****Last-Modified: 
 * Path file = Paths.get(fileName);
            BasicFileAttributes attr =
                Files.readAttributes(file, BasicFileAttributes.class);

            System.out.println("creationTime: " + attr.creationTime());
            System.out.println("lastAccessTime: " + attr.lastAccessTime());
            System.out.println("lastModifiedTime: " + attr.lastModifiedTime());
 * */
           
			sb.append("Server: Concordia Server-HTTP/1.0\r\n");
			sb.append("MIME-version: 1.0\r\n");
			//****Content-Length for response entity length
			//****Content-Type get from request client
			
			//blank line
			sb.append("\r\n");
			
			/* check GET and POST request body in main
			 * the GET method should not have entity body
			 * */
			if(!this.resBody.isEmpty()) {
				sb.append(this.resBody);
			}
			return sb.toString();
		}
}
