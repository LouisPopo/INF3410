import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class Client {
    public static void main(String[] args) throws Exception {
        
    	Image newimg;
        BufferedImage bimg;
        byte[] bytes;
    	
        try (var socket = new Socket("localhost", 59898)) {
            System.out.println("Reading image");
            
            Robot bot;
            bot = new Robot();
            bimg = bot.createScreenCapture(new Rectangle(0, 0, 200, 100));
            
            File initfile = new File("/Users/louispopovic/Documents/Poly/A2020/INF3410/INF3410/mi.jpg");
            ImageIO.write(bimg, "jpg", initfile);
            
            /*final float FACTOR  = 4f;
            BufferedImage img = ImageIO.read(new File("/Users/louispopovic/Documents/Poly/A2020/INF3410/INF3410/TP1/client/mi.png"));
            int scaleX = (int) (img.getWidth() * FACTOR);
            int scaleY = (int) (img.getHeight() * FACTOR);
            Image image = img.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
            BufferedImage buffered = new BufferedImage(scaleX, scaleY, 0);
            buffered.getGraphics().drawImage(image, 0, 0 , null);*/
            
            System.out.println("created image");
            
            ImageIO.write(bimg,"JPG",socket.getOutputStream());
            
            
            // wait for answer
            
            BufferedImage returned_img = ImageIO.read(ImageIO.createImageInputStream(socket.getInputStream()));
            
            File outputfile = new File("/Users/louispopovic/Documents/Poly/A2020/INF3410/INF3410/image.jpg");
            ImageIO.write(returned_img, "jpg", outputfile);
            
            System.out.println("Saved image");
            
            
            
            socket.close();
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