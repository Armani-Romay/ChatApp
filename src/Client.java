import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable{

    @Override
    public void run() {
        try{
            Socket client = new Socket("127.0.0.1", 8888);
        } catch(IOException e){
            //TODO: handle
        }
    }
}
