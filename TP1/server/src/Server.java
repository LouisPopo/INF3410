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

/**
 * A server program which accepts requests from clients to capitalize strings.
 * When a client connects, a new thread is started to handle it. Receiving
 * client data, capitalizing it, and sending the response back is all done on
 * the thread, allowing much greater throughput because more clients can be
 * handled concurrently.
 */
public class Server {

    /**
     * Runs the server. When a client connects, the server spawns a new thread to do
     * the servicing and immediately returns to listening. The application limits
     * the number of threads via a thread pool (otherwise millions of clients could
     * cause the server to run out of resources by allocating too many threads).
     */
	
    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(59898)) {
            System.out.println("The capitalization server is running...");
            var pool = Executors.newFixedThreadPool(20);
            while (true) {
                pool.execute(new Capitalizer(listener.accept()));
            }
        }
    }

    private static class Capitalizer implements Runnable {
        private Socket socket;
        
        private Sobel sobel = new Sobel(); 
        
        public static byte[] bufferedImgToByte(BufferedImage buffImg) throws IOException {
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		ImageIO.write(buffImg, "jpg", baos);
    		baos.flush();
    		byte[] byteImage = baos.toByteArray();
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
        
        
        
        Capitalizer(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            
            try {
				DataInputStream dIn = new DataInputStream(this.socket.getInputStream());
				
				int length = dIn.readInt();
				if (length > 0) {
					byte[] message = new byte[length];
					dIn.readFully(message, 0, message.length);
					
					BufferedImage buffImg = this.byteToBufferedImg(message);
					
					// convert to bufferedImage
					
					// process image
					BufferedImage processedImg = this.sobel.process(buffImg);

					
					// re-convert to byte[]
					byte[] processedByte = this.bufferedImgToByte(processedImg);
					
					// send byte[]
					
					DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
		        	
		        	dOut.writeInt(processedByte.length);
		        	dOut.write(processedByte);
				}		
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            
           
        }
    }
}