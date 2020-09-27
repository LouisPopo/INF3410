import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;


public class Server {

	
    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(59898)) {
            System.out.println("The Sobel server is running...");
            var pool = Executors.newFixedThreadPool(20);
            while (true) {
                pool.execute(new ImageProcesser(listener.accept()));
            }
        }
    }

    private static class ImageProcesser implements Runnable {
        private Socket socket;
     
        private Sobel sobel = new Sobel(); 
        
        ImageProcesser(Socket socket) {
            this.socket = socket;
        }
        
        public byte[] bufferedImgToByte(BufferedImage buffImg) throws IOException {
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		ImageIO.write(buffImg, "jpg", baos);
    		baos.flush();
    		byte[] byteImage = baos.toByteArray();
    		baos.close();
    		return byteImage;
    	}
        
        public BufferedImage byteToBufferedImg(byte[] inputByteArray) throws IOException {
			
    		InputStream in = new ByteArrayInputStream(inputByteArray);
    		return ImageIO.read(in);
        	     	
        }
       
        public static byte[] receiveByteArray(DataInputStream dIn) throws IOException {
        	
        	int length = dIn.readInt();
        	byte[] message = new byte[length];
        	dIn.readFully(message, 0, message.length);
        	
        	return message;
        	
        }
        
        public void sendByteArray(byte[] byteArray, DataOutputStream dOut) throws IOException {
    		
    		dOut.writeInt(byteArray.length);
    		dOut.write(byteArray);
    		
    	}
        
        @Override
        public void run(){
            System.out.println("Connected: " + socket);
            try {
            	DataInputStream dIn = new DataInputStream(this.socket.getInputStream());
    			byte[] preProcessedImgByte = receiveByteArray(dIn);
                
                BufferedImage preProcessedBuffImg = this.byteToBufferedImg(preProcessedImgByte);
                BufferedImage processedImg = Sobel.process(preProcessedBuffImg);
                
                byte[] processedImgByte = this.bufferedImgToByte(processedImg);
                
                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            	this.sendByteArray(processedImgByte, dOut);
            	
            } catch (Exception e) {
            	
            	System.out.println("Could not process image");
            	
            }
        }
    }
}