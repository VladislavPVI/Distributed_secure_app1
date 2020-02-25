import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocketFactory;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
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
}
