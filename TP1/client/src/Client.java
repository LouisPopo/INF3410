import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
/** Client Class
 * Contains the methods to communicate with a server
 * 
 * @authors  Michael Chehab, Thierry Beiko, Louis Popovic
 * @version 1.0
 */
public class Client {
	
	/** jpegToByte method 
	 * Loads an image from a path and converts it to a byte array.
	 * @param pathJpeg : String of the path to the image
	 * @return byteImage : byteArray containing image data
	 * @throws IOException
	 */
	public static byte[] jpegToByte(String pathJpeg) throws IOException {
		
		BufferedImage image = ImageIO.read(new File(pathJpeg));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", baos);
		baos.flush();
		byte[] byteImage = baos.toByteArray();
		baos.close();
		
		return byteImage;
		
	}
	
	/** byteToJpeg method
	 *  Converts a byte array to an image file and saves it to a specific path 
	 * @param inputByteArray : byteArray containing the image data
	 * @param outputPath : String of the path for the saved image
	 * @throws IOException
	 */
	public static void byteToJpeg(byte[] inputByteArray, String outputPath) throws IOException {
		
		InputStream in = new ByteArrayInputStream(inputByteArray);
		BufferedImage image = ImageIO.read(in);
		File outputImage = new File(outputPath);
		ImageIO.write(image,"jpg",outputImage);
		        	
    }
	
	/** sendByteArray method
	 * Sends a byte array to an output stream
	 * @param byteArray : data to send
	 * @param dOut : DataOutputStream to write in
	 * @throws IOException
	 */
	public static void sendByteArray(byte[] byteArray, DataOutputStream dOut) throws IOException {
		
		dOut.writeInt(byteArray.length);
		dOut.write(byteArray);
		
	}
	
	/** receiveByteArray method
	 * Receives a byte array from an input stream
	 * @param dIn : DataInputStream to read
	 * @return message : byteArray received 
	 * @throws IOException
	 */
	public static byte[] receiveByteArray(DataInputStream dIn) throws IOException {
		
		int length = dIn.readInt();
		byte[] message = new byte[length];
		dIn.readFully(message, 0, message.length);
		
		return message;
	}
	
	/** validateIpAddr method
	 * Validates the format of an IP address 
	 * @param ip : String containing the IP address
	 * @return Boolean of the validation
	 */
	public static boolean validateIpAddr(final String ip) {
	    String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

	    return ip.matches(PATTERN);
	}
	
	/** askIpAddress method
	 *  Asks the user the IP address of the server
	 * @param scanner : Scanner linked to the system's command
	 * @return ipAddrString : String of the IP address
	 * @throws UnknownHostException
	 */
	public static String askIpAddress(Scanner scanner) throws UnknownHostException {
		String ipAddrString = "";
    	do {
    		System.out.println("Enter ip address of the server:");
    		ipAddrString = scanner.nextLine();
    	} while(!validateIpAddr(ipAddrString));
		return ipAddrString;
	}
	
	/** askPortNumber method
	 *  Asks the user the port number of the server
	 * @param scanner : Scanner linked to the system's command
	 * @return ipAddrString : int of the port number
	 * @throws UnknownHostException
	 */
	public static int askPortNumber(Scanner scanner) {
		int portNb = 0;
    	do {
    		System.out.println("Enter port number of the server:");
    		portNb = Integer.parseInt(scanner.nextLine());
    	} while(portNb < 5000 || portNb > 5050);
    	return portNb;
	}
	
	/** askUsername method
	 * Asks the user his username to log into the server
	 * @param scanner : Scanner linked to the system's command
	 * @return usernameString : String of the username
	 */
	public static String askUsername(Scanner scanner) {
    	System.out.println("Enter username:");
    	String usernameString = scanner.nextLine();
		return usernameString;
	}
	
	/** askPassword method
	 * Asks the user his password to log into the server
	 * @param scanner : Scanner linked to the system's command
	 * @return passwordString : String of the password
	 */
	public static String askPassword(Scanner scanner) {
    	System.out.println("Enter password:");
    	String passwordString = scanner.nextLine();
		return passwordString;
	}
	
	public static String askImageUploadName(Scanner scanner) {
    	
    	
    	String pathString = "";
    	Boolean fileExists = false;
    	
    	while(!fileExists) {
    		System.out.println("Enter the name of the image you wish to upload including the extension:");
    		pathString = scanner.nextLine();
    		File f = new File(pathString);
    		fileExists = f.exists();
    		if (!fileExists) {
    			System.out.println("This file does not exists.");
    		}
    	}
    	
		return pathString;
	}
	
	public static String askImageSaveName(Scanner scanner) {
    	System.out.println("Enter the name you wish to give the filtered image including the extension:");
    	String path = scanner.nextLine();
		return path;
	}
	
	public static Socket connectToServer(Scanner scanner) throws UnknownHostException {
		
		while (true) {
			
        	try {
        		Socket socket = new Socket();
    			
    			String ipAddr = askIpAddress(scanner);
            	int portNb = askPortNumber(scanner);
            	
            	SocketAddress sockAddr = new InetSocketAddress(ipAddr, portNb);
            	
            	socket.connect(sockAddr, 2000);
            	
            	// if reach here : socket is connected
            	
            	return socket;
        	} catch (Exception e) {
        		System.out.println("Cant reach IP.Port. Please try again.");
        	}
		}
		
	}
	
    public static void main(String[] args) throws Exception {
        
    	Scanner scanner = new Scanner(System.in);
    	
    	Socket socket = connectToServer(scanner);
    	
    	
    	String username = askUsername(scanner);
    	String password = askPassword(scanner);
    		
        	
    	DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
    	DataInputStream dIn = new DataInputStream(socket.getInputStream());
    	Boolean connected = false;
    	
    	dOut.writeUTF(username);
    	dOut.writeUTF(password);
    	
    	while(!connected) {
    		String serverAnswer = dIn.readUTF();
    		if (serverAnswer.equals("Accepted")) {
    			System.out.println("You are now connected!");
    			connected = true;
    		}
    		if (serverAnswer.equals("Declined")) {
    			System.out.println("Erreur dans la saisie du mot de passe.");
    			username = askUsername(scanner);
    			password = askPassword(scanner);
    			dOut.writeUTF(username);
    			dOut.writeUTF(password);
    		}
    		if (serverAnswer.equals("NewUserConnected")) {
    			System.out.println("Your user has been created.");
    			System.out.println("You are now connected!");
    			connected = true;
    		}
    	}
    	
    	String uploadName = askImageUploadName(scanner);
    	String saveName = askImageSaveName(scanner);
    	dOut.writeUTF(uploadName);
        
    	byte[] image = jpegToByte(uploadName);
    	
    	sendByteArray(image, dOut);
    	System.out.println("Image sent to the server...");
    	
    	// should now wait to receive processed image as a byte[]
    	
		byte[] processedImage = receiveByteArray(dIn);
		
		byteToJpeg(processedImage, saveName);
		String currentDirectory = System.getProperty("user.dir");
    	System.out.println("Image received from the server. File location : " + currentDirectory + "\\" + saveName);

        	
    }
}