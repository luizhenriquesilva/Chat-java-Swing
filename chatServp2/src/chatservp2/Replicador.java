package chatservp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Replicador extends Thread {

    private ChatServerTela servidor;
    private Socket socket;
    private DataOutputStream dout;

    public Replicador(ChatServerTela servidor, Socket socket) throws IOException {
        this.servidor = servidor;
        this.socket = socket;
        dout = new DataOutputStream(socket.getOutputStream());
        //somos uma thread, vamos começar...
        start();
    }

    @Override
    public void run() {
        try {
            //como noutras situações, obter as streams de leitura e escrita.
            DataInputStream din = new DataInputStream(socket.getInputStream());
            String mensagem;
            
            InputStream recebMSGcripto = socket.getInputStream();
            
            while (true) {
                byte[] bytes = null;
                
                /*
                recebMSGcripto.read();
                
                String msgDecript = Decritpografa(); 
                System.out.println("Ah se funcona" + msgDecript +" "+ getDateTime());
                servidor.replicarMensagem(msgDecript);
                
                */
                mensagem = din.readUTF();
                System.err.println("SR LIDO: " + mensagem + getDateTime());
                //se não foi enviada a mensagem de saida então enviamos o
                //texto para todos
                servidor.replicarMensagem(mensagem);
                //
            }
        } catch (EOFException ex) {
            //DO NOTHING
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        /*
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Replicador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Replicador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Replicador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Replicador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Replicador.class.getName()).log(Level.SEVERE, null, ex);
*/
        } finally {
            servidor.removeConnection(socket);
        }
    }

    public void enviarMensagem(String mensagem) {
        try {
            dout.writeUTF(mensagem);
                        
            System.out.println("enviar mensagem :" + getDateTime());
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void fechar() throws IOException {
        socket.close();
        System.out.println("socket fechado " + getDateTime());
        
    }
    public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss:mmm");
        Date date = new Date();
        String data = dateFormat.format(date);
        return data;
    }
    public String Decritpografa(byte[] msgCriptada) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
        SecretKey chaveDES = keygenerator.generateKey();

        Cipher cifraDES;

        // Cria a cifra 
        cifraDES = Cipher.getInstance("DES/ECB/PKCS5Padding");

        cifraDES.init(Cipher.DECRYPT_MODE, chaveDES);

        byte[] textoDecriptografado = cifraDES.doFinal(msgCriptada);

        String DecriptStr = new String(textoDecriptografado);

        return DecriptStr;

    }
}
