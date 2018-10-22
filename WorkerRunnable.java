import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import java.util.Base64.Encoder;
import javax.xml.bind.DatatypeConverter;

/**
 * @author rjw357 Russell Waterson
 */
public class WorkerRunnable implements Runnable {

	protected Socket clientSocket = null;
	protected String serverText   = null;
	protected String dir          = null;

	public WorkerRunnable(Socket clientSocket, String serverText) {
		this.clientSocket = clientSocket;
		this.serverText   = serverText;
	}

	public void run() {
		try {
			InputStream input  = clientSocket.getInputStream();
			OutputStream output = clientSocket.getOutputStream();
            
			//Set date and time
			long time = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");    
			Date resultdate = new Date(time);
			String readableDate = (sdf.format(resultdate) + " GMT");
            
			//Get the directory requested from the client
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
			String clientRequest = (bufferedReader.readLine());
			String authRequest = (bufferedReader.readLine());			
			if (clientRequest != null) {
				dir = (clientRequest.substring((clientRequest.indexOf("/") + 1), 
					(clientRequest.indexOf("HTTP") - 1)));
			}
			if (authRequest != null || authRequest.contains("Basic")) {
				authRequest = authRequest.substring(authRequest.indexOf("Basic") + 6);
			}
            
			if (dir.length() <= 0) {
				//If the home directory has been entered, display the available files
				System.out.println("Requested: Home Directory");

				String baseReply = "HTTP/1.1 200 OK \r\n" +
					"Date: " + readableDate + "\r\n" +
					"Content-Type: text/plain\r\n" +
					"Server: Russell Waterson's Server (Unix) \r\n" +
					"Connection: close\r\n" +
					"\r\n";

		            	output.write((baseReply + "WorkerRunnable: " + this.serverText + 
					" - " + time + "\n\n").getBytes());

				File f = new File(System.getProperty("user.dir") + "/Files/");
				ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));
				output.write(("More files in the locations:\n").getBytes());
				for (String n : names) {
					output.write(("\t/" + n + "\n").getBytes());
				}
				output.write("\n".getBytes());
			} else {
				//If the client requests a file
				File file = new File(System.getProperty("user.dir") + "/Files/" + dir);
				String mimeType = URLConnection.guessContentTypeFromName(file.getName());
				System.out.println("Requested: " + dir);

				if (file.length() <= 0) {
					//If the searched file does not exist
					System.out.println("Requested: Invalid Page 404");
					String failingText = "WorkerRunnable: " + this.serverText + " - " + 
					time + "\n404 Page Not Found\n";

					String notFoundReply = "HTTP/1.1 404 Not Found \r\n" +
						"Date: " + readableDate + "\r\n" +
						"Content-Type: text/plain\r\n" +
						"Content-Length: " + failingText.length() + "\r\n" +
						"Server: Russell Waterson's Server (Unix) \r\n" +
						"Connection: close\r\n" +
						"\r\n";

					output.write((notFoundReply + failingText).getBytes());
				} else {
					//When the searched file has been found

					String AUTHORISATION = DatatypeConverter.printBase64Binary("RussellW:password1".getBytes("utf-8"));

					if (authRequest.equals(AUTHORISATION)) {
						//Authentication check: if the entered username and password match the encoded Base64 login
						String okayReply = "HTTP/1.1 200 OK \r\n" +
							"Date: " + readableDate + "\r\n" +
							"Content-Type: " + mimeType + "\r\n" +
							"Content-Length: " + file.length() + "\r\n" +
							"Server: Russell Waterson's Server (Unix) \r\n" +
							"Connection: close\r\n" +
							"\r\n";

						output.write(okayReply.getBytes());

						Path path = file.toPath();
						Files.copy(path, output);
						output.flush();

					} else {
						//If the entered username and password DOES NOT match the encoded Base64 login
						System.out.println("Requested: Invalid Authorisation 401");
						String failingText = "WorkerRunnable: " + this.serverText + " - " + 
							time + "\n401 Unauthorized\n";

						String invalidAuthReply = "HTTP/1.1 401 Unauthorized \r\n" +
							"Date: " + readableDate + "\r\n" +
							"Content-Type: text/plain\r\n" +
							"Content-Length: " + failingText.length() + "\r\n" +
							"Server: Russell Waterson's Server (Unix) \r\n" +
							"Connection: close\r\n" +
							"\r\n";

						output.write((invalidAuthReply + failingText).getBytes());
					}
				}
			}

			output.close();
			input.close();
			System.out.println("Request processed: " + time);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
