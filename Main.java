/**
 * @author rjw357 Russell Waterson
 */
public class Main {
	
	public static void main(String[] args) {
		MultiThreadedServer server = new MultiThreadedServer(8088);
		new Thread(server).start();

		try {
			System.out.println("Starting server for 30 seconds");
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Stopping Server");
		server.stop();
	}
}
