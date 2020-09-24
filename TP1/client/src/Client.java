import java.io.ByteArrayOutputStream;
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
	
<<<<<<< HEAD
=======
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
	
	public static BufferedImage byteToBufferedImg(byte[] inputByteArray) {
		
    	try	{
    		InputStream in = new ByteArrayInputStream(inputByteArray);
    		return ImageIO.read(in);
    		
    	}       	
    	catch(IOException e) {
    		throw new RuntimeException("Image conversion failed");
    	}        	
    }
	
	

    public static void main(String[] args) throws Exception {
        
    	Image newimg;
        BufferedImage bimg;
        byte[] bytes;
    	
        try (var socket = new Socket("localhost", 59898)) {
            

        	// get image in byte 
        	
        	byte[] message = null;
        	int messageLength = 10;
        	
        	DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
        	
        	dOut.writeInt(messageLength);
        	dOut.write(message);
        	
        	// should now wait for image to return in byte[]
        	
        	// convert byte[] to jpeg
        	

        }
    }
}