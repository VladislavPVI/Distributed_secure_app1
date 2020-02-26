import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocketFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.Security;
import com.sun.net.ssl.internal.ssl.Provider;

public class Client {

    private static Socket clientSocket;
    private static OutputStream out;

    public static void main(String[] args) throws IOException {
       /*Adding the JSSE (Java Secure Socket Extension) provider which provides SSL and TLS protocols
        and includes functionality for data encryption, server authentication, message integrity,
        and optional client authentication.*/
        Security.addProvider(new Provider());
        //specifing the trustStore file which contains the certificate & public of the server
        System.setProperty("javax.net.ssl.trustStore", "myTrustStore.jts");
        //specifing the password of the trustStore file
        System.setProperty("javax.net.ssl.trustStorePassword","19972020");
        //This optional and it is just to show the dump of the details of the handshake process
        System.setProperty("javax.net.debug","all");

        SSLSocketFactory sslSocketFactory =
                (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {

            BufferedImage image = ImageIO.read(new File("photo.jpg"));
            clientSocket = sslSocketFactory.createSocket("localhost", 9098);
            //clientSocket = new Socket("localhost", 9595);
            System.out.println("Connected to server");
            out = clientSocket.getOutputStream();

            int width = image.getWidth();
            int height = image.getHeight();

            for(int i=0; i<height; i++) {

                for (int j = 0; j < width; j++) {

                    Color c = new Color(image.getRGB(j, i));
                    int red = (int) (c.getRed() * 0.299);
                    int green = (int) (c.getGreen() * 0.587);
                    int blue = (int) (c.getBlue() * 0.114);
                    int rgb = range(red + green + blue, 10);
                    Color newColor = new Color(rgb, rgb, rgb);

                    image.setRGB(j, i, newColor.getRGB());
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image,"jpg",baos);
            out.write(baos.toByteArray());
            out.flush();

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName())
                    .log(Level.SEVERE, null, ex);
        } finally {
            clientSocket.close();
            out.close();
        }


    }
    private static int range(int n, double prob) {
        double res = ((100 * prob)/10);

        int[]array = new int[(int)res];
        array[0]= 1;
        array[1]=255;

        for (int i = 2 ; i <= res - 2; i++)
        {
            array[i] = n;
        }
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }
}
