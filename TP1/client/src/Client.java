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
		BufferedImage image = null;
		byte[] byteImage;
        image = ImageIO.read(new File(pathJpeg));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", baos);
		baos.flush();
		byteImage = baos.toByteArray();
		baos.close();
		return byteImage;
	}
	
	public static void byteToJpeg(byte[] inputByteArray, String path) {
		
    	try	{
    		InputStream in = new ByteArrayInputStream(inputByteArray);
    		BufferedImage image;
    		image = ImageIO.read(in);
    		File outputImage = new File(path);
    		ImageIO.write(image,"jpg",outputImage);
    		
    	}       	
    	catch(IOException e) {
    		throw new RuntimeException("Image conversion failed");
    	}        	
    }
	
	

    public static void main(String[] args) throws Exception {
        
    	
        try (var socket = new Socket("localhost", 59898)) {
            

        	// get image in byte 
        	byte[] image = jpegToByte("/Users/louispopovic/Documents/Poly/A2020/INF3410/INF3410/TP1/test.jpg");
        
        	DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
        	
        	dOut.writeInt(image.length);
        	dOut.write(image);
        	
        	// should now wait for image to return in byte[]
        	
        	DataInputStream dIn = new DataInputStream(socket.getInputStream());
			
			int length = dIn.readInt();
			if (length > 0) {
				byte[] message = new byte[length];
				dIn.readFully(message, 0, message.length);
				
				byteToJpeg(message, "/Users/louispopovic/Documents/Poly/A2020/INF3410/INF3410/TP1/ProcessedTest.jpg");
				
			}
        	
        	// convert byte[] to jpeg
        	

        }
    }
}