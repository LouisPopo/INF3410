import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

/** Server Class
 * Contains the methods to communicate with multiple clients and
 * implements an application that applies a Sobel filter to images it
 * receives. It also keeps a log of usernames and their matching passwords
 * @authors  Michael Chehab, Thierry Beiko, Louis Popovic
 * @version 1.0
 */
public class Server {


	/** validateIpAddr method
	 * Verifies if the IP address sent by the client is a valid IP
	 * 
	 * @param String ip 
	 * @returns boolean (true if valid IP, false if not)
	 */
	public static boolean validateIpAddr(final String ip) {
		String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

		return ip.matches(PATTERN);
	}

	/** askIpAddress method
	 * Scanner asking the user to send the Ip Address until the valid IP of the host is received
	 *  
	 * @return InetAddress This returns the valid address of the machine hosting the server sent by the user
	 */
	public static InetAddress askIpAddress(Scanner scanner) throws UnknownHostException {
		String ipAddrString = "";
		do {
			System.out.println("Enter ip address of the machine hosting the server : ");
			ipAddrString = scanner.nextLine();
			if(!validateIpAddr(ipAddrString))
				System.out.println("This IP address is not valid ! Try another one.");
		} while(!validateIpAddr(ipAddrString));
		return InetAddress.getByName(ipAddrString);
	}

	/** askPortNumber method
	 * Scanner asking user to send a Port Number until a valid portnum is received
	 *  
	 * @return int portNb  This returns the valid port number typed in by the user
	 */
	public static int askPortNumber(Scanner scanner) {
		int portNb = 0;
		do {
			System.out.println("Enter port number: ");
			portNb = Integer.parseInt(scanner.nextLine());
			if(portNb < 5000 || portNb > 5050)
				System.out.println("This port number is not valid ! Try another one.");
		} while(portNb < 5000 || portNb > 5050);
		return portNb;
	}

	public static void main(String[] args) throws Exception {

		Scanner scanner = new Scanner(System.in);

		InetAddress addr = askIpAddress(scanner);
		int portNb = askPortNumber(scanner);

		try (ServerSocket listener = new ServerSocket(portNb, 50, addr)) {
			System.out.println("The Sobel server is running...");
			System.out.println("Waiting for user to connect...");
			ExecutorService pool = Executors.newFixedThreadPool(20);
			while (true) {
				pool.execute(new ImageProcesser(listener.accept()));
			}
		}
	}
	/** ImageProcesser Class
	 * Contains the methods allowing to receive, send and modify images sent by
	 * the client
	 * 
	 */
	private static class ImageProcesser implements Runnable {
		private Socket socket;

		private Sobel sobel = new Sobel(); 

		ImageProcesser(Socket socket) {
			this.socket = socket;
		}

		/** bufferedImgToByte method
		 * Transforms a BufferedImage object to a byte array
		 *  
		 * @param BufferedImage buffImg image to be transformed
		 * @return byte[] byteImage This is a byte array containing the information
		 * relative to the image
		 */
		public byte[] bufferedImgToByte(BufferedImage buffImg) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(buffImg, "jpg", baos);
			baos.flush();
			byte[] byteImage = baos.toByteArray();
			baos.close();
			return byteImage;
		}

		/** byteToBufferedImg method
		 * Transforms a byte array to a BufferedImage object
		 *  
		 * @param byte[] inputByteArray byte array to be transformed
		 * @return ImageIO.read(in) This is an image created from a byte input stream
		 */
		public BufferedImage byteToBufferedImg(byte[] inputByteArray) throws IOException {

			InputStream in = new ByteArrayInputStream(inputByteArray);
			return ImageIO.read(in);

		}

		/** receiveByteArray method
		 * This function receives a byte array from a socket as an input stream
		 * It first reads the size of the stream, and then reads the message contained
		 * in the stream
		 *  
		 * @param DataInputSteam dIn received byte array containing an int giving the length
		 * of the message and the message to read.
		 * @return byte[] message  The message contained in the byte array 
		 */
		public static byte[] receiveByteArray(DataInputStream dIn) throws IOException {

			int length = dIn.readInt();
			byte[] message = new byte[length];
			dIn.readFully(message, 0, message.length);

			return message;

		}
		
		/** sendByteArray  method
		 * Sends a byte array and its size as a DataOutputStream 
		 *  
		 * @param byte[] byteArray The size of the message and the message as a byte array
		 * @param DataOutputStream dOut output stream to send to the client
		 * 
		 */
		public void sendByteArray(byte[] byteArray, DataOutputStream dOut) throws IOException {

			dOut.writeInt(byteArray.length);
			dOut.write(byteArray);

		}

		/** run  method
		 * Runs the server on the host machine in order to connect
		 * with potential clients.
		 *  
		 */
		@Override
		public void run(){
			System.out.println("Connected: " + socket);
			try {
				DataInputStream dIn = new DataInputStream(this.socket.getInputStream());
				DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
				Boolean connected = false;
				String username = new String();

				while(!connected) {
					username = dIn.readUTF();
					String password = dIn.readUTF();
					
					File userList = new File("userList.csv");
					userList.createNewFile();
					
					
					BufferedReader csvReader = new BufferedReader(new FileReader("userList.csv"));
					String row;
					Boolean wrongPasswrong = false;
					while ((row = csvReader.readLine()) != null) {
						String[] data = row.split(",");
						if (data[0].equals(username)) {
							if (data[1].equals(password)) {
								connected = true;
								dOut.writeUTF("Accepted");
								System.out.println(username + " is connected");
								break;
							} else {
								dOut.writeUTF("Declined");
								wrongPasswrong = true;
								break;
							}
						}
					}
					csvReader.close();
					if (connected == false && wrongPasswrong == false) {
						FileWriter csvWriter = new FileWriter("userList.csv", true);
						csvWriter.append(username);
						csvWriter.append(",");
						csvWriter.append(password);
						csvWriter.append("\n");
						csvWriter.flush();
						csvWriter.close();
						connected = true;
						System.out.println(username + " is connected");
						dOut.writeUTF("NewUserConnected");
					}
				}
				String imageName = dIn.readUTF();
				System.out.println("Waiting to receive image to process...");

				int length = dIn.readInt();
				if (length > 0) {
					byte[] message = new byte[length];
					dIn.readFully(message, 0, message.length);
					//Image received
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					String clientIp = socket.getInetAddress().toString();
					int clientPort = socket.getPort();
					System.out.println("[" + username + " - " + clientIp + ":" + clientPort + " - "
							+ dateFormat.format(date) + "]: Image " + imageName);
					
					
					BufferedImage buffImg = this.byteToBufferedImg(message);

					// convert to bufferedImage

					// process image
					BufferedImage processedImg = this.sobel.process(buffImg);


					// re-convert to byte[]
					byte[] processedByte = this.bufferedImgToByte(processedImg);

					// send byte[]

					dOut.writeInt(processedByte.length);
					dOut.write(processedByte);
				}

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}

		}
	}
}
