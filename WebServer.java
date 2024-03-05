

/**
 * WebServer Class
 * 
 * Implements a multi-threaded web server
 * supporting non-persistent connections.
 *
 */


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.logging.*;



public class WebServer extends Thread {
	// global logger object, configures in the driver class
	private static final Logger logger = Logger.getLogger("WebServer");

	private boolean shutdown = false; // shutdown flag
	public int port;
	public String root;
	public int timeout;
	ServerSocket serverSocket;
	Socket clientSocket;
	Worker wThread;
	private final List<Worker> wThreads = new ArrayList<>();


	
    /**
     * Constructor to initialize the web server
     * 
     * @param port 	Server port at which the web server listens > 1024
	 * @param root	Server's root file directory
	 * @param timeout	Idle connection timeout in milli-seconds
     * 
     */
	public WebServer(int port, String root, int timeout){
		//implement the constructor here
		this.port = port;
		this.root = root;
		this.timeout = timeout;

	}
	
    /**
	 * Main method in the web server thread.
	 * The web server remains in listening mode 
	 * and accepts connection requests from clients 
	 * until it receives the shutdown signal.
	 * 
     */
	public void run(){
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(timeout);

			while(!shutdown){
				try {
					clientSocket = serverSocket.accept();
					System.out.println("Client information: IP- "+clientSocket.getInetAddress().getHostAddress()+" Port- "+clientSocket.getPort());
					wThread = new Worker(clientSocket, root);
					wThread.start();
					wThreads.add(wThread);
				} catch (SocketTimeoutException e){

				}
			}
			//wait for sometime before this
			for(Worker w: wThreads){
				w.join();
			}

			//clean up
			serverSocket.close();
			clientSocket.close();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			try {
				serverSocket.close();
				clientSocket.close();
				//cleanup streams as well
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}


	}
	

    /**
     * Signals the web server to shutdown.
	 *
     */
	public void shutdown() {
		shutdown = true;
	}

	static class Worker extends Thread {
		private final Socket clientSocket;
		public String root;
		InputStream inpStream;
		OutputStream outputStream;
		BufferedReader inputStream;
		InputStream fileInp;
		String responseOk;
		String otherResponse;
		String objectPath;
		String filePath;

		public Worker(Socket clientSocket, String root){
			this.clientSocket = clientSocket;
			this.root = root;
		}

		public void run(){
			try{
				clientSocket.setSoTimeout(300000);
				inpStream = clientSocket.getInputStream();
				inputStream = new BufferedReader(new InputStreamReader(inpStream));
				outputStream = clientSocket.getOutputStream();

				//read the request line
				String reqHeader = inputStream.readLine();

				//reading the header lines
				StringBuilder headerB = new StringBuilder();
				String header;
				while((header = inputStream.readLine()) != null && !header.isEmpty()){
					headerB.append(header).append("\r\n");
				}

				System.out.println(reqHeader+"\n"+headerB.toString());
				//null req
				if(reqHeader == null){
					//send 400 response [format error]
					otherResponse = "HTTP/1.1 400 Bad Request\r\n"
							+"Date: "+ServerUtils.getCurrentDate()+"\r\n"
							+"Server: WServer\r\n"
							+"Connection: close\r\n\r\n";
					outputStream.write(otherResponse.getBytes("US-ASCII"));
					outputStream.flush();
					//cleanup and return
					clientSocket.close();
					inputStream.close();
					outputStream.close();
					System.out.println(otherResponse);
					return;
				}
				//splitting the string to get the object path
				String [] parts = reqHeader.split("\\s+");
				objectPath = parts[1];
				String reqC = parts[0];
				String protocol = parts[2];

				if(objectPath.equals("/")){
					objectPath = "/index.html";
				}

				//formatting issue 400
				if(!reqC.equals("GET") || !protocol.equals("HTTP/1.1")){
					otherResponse = "HTTP/1.1 400 Bad Request\r\n"
							+"Date: "+ServerUtils.getCurrentDate()+"\r\n"
							+"Server: WServer\r\n"
							+"Connection: close\r\n\r\n";
					outputStream.write(otherResponse.getBytes("US-ASCII"));
					outputStream.flush();
					//cleanup and return
					clientSocket.close();
					inputStream.close();
					outputStream.close();
					System.out.println(otherResponse);
					return;
				}
				//creating the file path;
				filePath = root+objectPath;


				//check if file object exists;
				File file = new File(filePath);
				//might have to check for a directory as well
				if(!file.exists() || file.isDirectory()){
					otherResponse = "HTTP/1.1 404 Not Found\r\n"
							+"Date: "+ServerUtils.getCurrentDate()+"\r\n"
							+"Server: WServer\r\n"
							+"Connection: close\r\n\r\n";
					outputStream.write(otherResponse.getBytes("US-ASCII"));
					outputStream.flush();
					//cleanup and return
					clientSocket.close();
					inputStream.close();
					outputStream.close();
					System.out.println(otherResponse);
					return;
				}
				fileInp = new FileInputStream(file);

				//OK response
				responseOk = "HTTP/1.1 200 OK\r\n"
						+"Date: "+ServerUtils.getCurrentDate()+"\r\n"
						+"Server: WServer\r\n"
						+"Last-Modified: "+ServerUtils.getLastModified(file)+"\r\n"
						+"Content-Length: "+ServerUtils.getContentLength(file)+"\r\n"
						+"Content-Type: "+ServerUtils.getContentType(file)+"\r\n"
						+"Connection: close\r\n\r\n";
				outputStream.write(responseOk.getBytes("US-ASCII"));
				outputStream.flush();
				System.out.println(responseOk);

				//start sending the file object
				//number of bytes read
				int numOfBytes;
				//buffer to store bytes for reading and writing
				byte[] buffer = new byte[1024];

				//loop to keep reading bytes from the file and writing them to the socket
				while((numOfBytes = fileInp.read(buffer)) != -1){
					outputStream.write(buffer, 0, numOfBytes);
					outputStream.flush();
				}
				//cleanup
				clientSocket.close();
				fileInp.close();
				inputStream.close();
				outputStream.close();
				return;


			} catch (SocketTimeoutException e) {
				//create http req with status code 408
				otherResponse = "HTTP/1.1 408 Request Timeout\r\n"
						+"Date: "+ServerUtils.getCurrentDate()+"\r\n"
						+"Server: WServer\r\n"
						+"Connection: close\r\n\r\n";
				try {
					outputStream.write(otherResponse.getBytes("US-ASCII"));
					outputStream.flush();
					//cleanup and return
					clientSocket.close();
					inputStream.close();
					outputStream.close();
					return;
				} catch (IOException ex){
					ex.printStackTrace();
				}

			} catch (IOException e){
				try {
					clientSocket.close();
					inputStream.close();
					outputStream.close();
					return;
				} catch (IOException ex) {
					ex.printStackTrace();
					//return;
				}

			}
		}

	}
	
}


