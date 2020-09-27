import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.Scanner;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class Client {
	
	public static byte[] jpegToByte(String pathJpeg) throws IOException {
		
		BufferedImage image = ImageIO.read(new File(pathJpeg));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", baos);
		baos.flush();
		byte[] byteImage = baos.toByteArray();
		baos.close();
		
		return byteImage;
		
	}
	
	public static void byteToJpeg(byte[] inputByteArray, String outputPath) throws IOException {
		
		InputStream in = new ByteArrayInputStream(inputByteArray);
		BufferedImage image = ImageIO.read(in);
		File outputImage = new File(outputPath);
		ImageIO.write(image,"jpg",outputImage);
		        	
    }
	
	public static void sendByteArray(byte[] byteArray, DataOutputStream dOut) throws IOException {
		
		dOut.writeInt(byteArray.length);
		dOut.write(byteArray);
		
	}
	
	public static byte[] receiveByteArray(DataInputStream dIn) throws IOException {
		
		int length = dIn.readInt();
		byte[] message = new byte[length];
		dIn.readFully(message, 0, message.length);
		
		return message;
	}
	
    public static void main(String[] args) throws Exception {
        
    	
        try (var socket = new Socket("localhost", 59898)) {
            
        	byte[] image = jpegToByte("/Users/louispopovic/Documents/Poly/A2020/INF3410/pre-process.jpg");
        
        	DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
        	sendByteArray(image, dOut);
        	
        	// should now wait to receive processed image as a byte[]
        	
        	DataInputStream dIn = new DataInputStream(socket.getInputStream());
			byte[] processedImage = receiveByteArray(dIn);
			
			byteToJpeg(processedImage, "/Users/louispopovic/Documents/Poly/A2020/INF3410/post-process.jpg");
        	
        }
    }
}