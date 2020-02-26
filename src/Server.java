import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.net.ssl.SSLServerSocketFactory;
import java.security.Security;
import com.sun.net.ssl.internal.ssl.Provider;
public class Server {

    private static Socket clientSocket;
    private static ServerSocket serverSocket;
    private static InputStream in;


    public static void main(String[] args) throws IOException {

        /*Adding the JSSE (Java Secure Socket Extension) provider which provides SSL and TLS protocols
        and includes functionality for data encryption, server authentication, message integrity,
        and optional client authentication.*/
        Security.addProvider(new Provider());
        //specifing the keystore file which contains the certificate/public key and the private key
        System.setProperty("javax.net.ssl.keyStore", "myKeyStore.jks");
        //specifing the password of the keystore file
        System.setProperty("javax.net.ssl.keyStorePassword","19972020");
        //This optional and it is just to show the dump of the details of the handshake process
        System.setProperty("javax.net.debug","all");

        SSLServerSocketFactory sslServerSocketFactory =
                (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {

            serverSocket =
                    sslServerSocketFactory.createServerSocket(9098);
            System.out.println("SSL ServerSocket started");
            System.out.println(serverSocket.toString());

            clientSocket = serverSocket.accept();
            System.out.println("ServerSocket accepted");

            try {
                System.out.println("Start receive..");
                in = clientSocket.getInputStream();

                BufferedImage img = ImageIO.read(in);

                System.out.println("Received!");
                Color[] pixel=new Color[9];
                int[] R=new int[9];
                int[] B=new int[9];
                int[] G=new int[9];

                for(int i=1;i<img.getWidth()-1;i++)
                    for(int j=1;j<img.getHeight()-1;j++)
                    {
                        pixel[0]=new Color(img.getRGB(i-1,j-1));
                        pixel[1]=new Color(img.getRGB(i-1,j));
                        pixel[2]=new Color(img.getRGB(i-1,j+1));
                        pixel[3]=new Color(img.getRGB(i,j+1));
                        pixel[4]=new Color(img.getRGB(i+1,j+1));
                        pixel[5]=new Color(img.getRGB(i+1,j));
                        pixel[6]=new Color(img.getRGB(i+1,j-1));
                        pixel[7]=new Color(img.getRGB(i,j-1));
                        pixel[8]=new Color(img.getRGB(i,j));
                        for(int k=0;k<9;k++){
                            R[k]=pixel[k].getRed();
                            B[k]=pixel[k].getBlue();
                            G[k]=pixel[k].getGreen();
                        }
                        Arrays.sort(R);
                        Arrays.sort(G);
                        Arrays.sort(B);
                        img.setRGB(i,j,new Color(R[4],B[4],G[4]).getRGB());
                    }



                ImageIO.write(img, "jpg", new File("test2.jpg"));


            } finally {
                System.out.println("Сокеты закрываем");
                clientSocket.close();
                in.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName())
                    .log(Level.SEVERE, null, ex);
        } finally {
            System.out.println("Сервер закрыт");
            serverSocket.close();
        }


    }
}
