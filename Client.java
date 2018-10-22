import java.awt.Color;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * @author rjw357 Russell Waterson
 */
public class Client {

	//Constraints for JFrame where HTML response will be rendered
	private static final int HEIGHT = 720;
	private static final int WIDTH = 1280;
	private static final String TITLE = "HTML Rendering";
	private static final String FILEDIR = System.getProperty("user.dir") + "/Files/response.html";

	private static File file = new File(FILEDIR);
	
	public static void main(String[] args) {
	
		try {
		
			//User input for their website of choice			
			Scanner reader = new Scanner(System.in);
			System.out.print("Enter your webpage: ");
			String url = reader.next();

			//User input for whether to print html to terminal
			System.out.print("Do you want to print the html to the terminal (y/n): ");
			String printHTML = reader.next();
			

			//Defaults to a secure https if one isn't entered
			if (!(url.startsWith("http://") || url.startsWith("https://"))) {
				url = "https://" + url;
			}
		
			HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();

			//HTTP response status
			int status = httpConnection.getResponseCode();
			System.out.println("\nStatus: " + status);
			
			System.out.println("\n******* HTTP RESPONSE HEADERS *******\n");

			//HTTP response headers
			for (Entry<String, List<String>> header : httpConnection.getHeaderFields().entrySet()) {
				System.out.println(header.getKey() + "=" + header.getValue());
			}
			
			if (status >= 400) {
				//Failing response status
				InputStream streamError = httpConnection.getErrorStream();
				String error = streamToString(streamError);
				if (printHTML.toLowerCase().equals("y")) {
					System.out.println("\n****** HTTP FAIL RESPONSE HTML ******\n");
					System.out.println(error);
				}
				htmlToGUI(error, status);
			} else {
				//OK response status
				InputStream streamResponse = httpConnection.getInputStream();
				String response = streamToString(streamResponse);
				if (printHTML.toLowerCase().equals("y")) {
					System.out.println("\n******** HTTP RESPONSE HTML *********\n");
					System.out.println(response);
				}
				htmlToGUI(response, status);
			}	

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String streamToString(InputStream stream) throws IOException {
		//Building a string from the the response stream
		BufferedReader rd = new BufferedReader(new InputStreamReader(stream));
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append("\n");
		}
		rd.close();

		//Writing response HTML to a file for rendering
		try(PrintWriter out = new PrintWriter(FILEDIR)){
			out.println(response.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return response.toString();
	}
	
	private static void htmlToGUI(String html, int status) {

		SwingUtilities.invokeLater( new Runnable() {
	    
			public void run() {

				System.out.println("\nStarting GUI Element");
				final JFrame jframe = new JFrame(TITLE + " - Status code: " + status);
				Container contentPane = jframe.getContentPane();
				jframe.setSize(WIDTH, HEIGHT);

				contentPane.setBackground(Color.WHITE);
				contentPane.setForeground(Color.WHITE);
				jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JEditorPane jep = new JEditorPane();
				try {
					jep.setPage(file.toURI().toURL());
				} catch (IOException e) {
					e.printStackTrace();
				}

				jep.setContentType("text/html");
				jep.setEditable(false);
				JScrollPane jsp = new JScrollPane(jep,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				contentPane.add(jsp);
				jframe.validate();
				jframe.setVisible(true);		        
			}
		});
		
	}
}
