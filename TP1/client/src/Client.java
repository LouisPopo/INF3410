import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
	
	
	
    public static void main(String[] args) throws Exception {
        
    	Image newimg;
        BufferedImage bimg;
        byte[] bytes;
    	
        try (var socket = new Socket("localhost", 59898)) {
            System.out.println("Reading image");
            
//            Robot bot;
//            bot = new Robot();
//            bimg = bot.createScreenCapture(new Rectangle(0, 0, 500, 500));
//            
            
            BufferedImage image = null;
            image = ImageIO.read(new File("C:\\Users\\thier\\Documents\\!Réseaux\\Labo\\INF3410\\TP1\\test.jpg"));
            
            
            System.out.println("created image");
            
            ImageIO.write(image,"JPG",socket.getOutputStream());
            socket.close();
            
            // wait for answer
            
            //BufferedImage returned_img = ImageIO.read(ImageIO.createImageInputStream(socket.getInputStream()));
            
            //File outputfile = new File("/Users/louispopovic/Documents/Poly/A2020/INF3410/INF3410/TP1/image.jpg");
            //ImageIO.write(returned_img, "jpg", outputfile);
            
            System.out.println("Saved image");
            
            
            
            //socket.close();
            /*var scanner = new Scanner(System.in);
            var in = new Scanner(socket.getInputStream());
            var out = new PrintWriter(socket.getOutputStream(), true);
            while (scanner.hasNextLine()) {
                out.println(scanner.nextLine());
                System.out.println(in.nextLine());
            }*/
        }
    }
}