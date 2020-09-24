import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
				BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(this.socket.getInputStream()));
				
				System.out.println("Received image");
				
				BufferedImage processImg = this.sobel.process(img);
				
				System.out.println("Processed image");
				
				File outputfile = new File("C:\\Users\\Michael\\Desktop\\Reseaux\\INF3410\\TP1\\img.jpg");
	            ImageIO.write(processImg, "jpg", outputfile);
				
				// re-send BufferedImage
				
				//ImageIO.write(img,"JPG",socket.getOutputStream());
				//System.out.println("Image sended");
		
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            
            /*try {
                var in = new Scanner(socket.getInputStream());
                var out = new PrintWriter(socket.getOutputStream(), true);
                while (in.hasNextLine()) {
                    out.println(in.nextLine().toUpperCase());
                }
            } catch (Exception e) {
                System.out.println("Error:" + socket);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
                System.out.println("Closed: " + socket);
            }*/ 
        }
    }
}