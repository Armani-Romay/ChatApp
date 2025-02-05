import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedReader;

public class Client implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    @Override
    public void run() {
        try{
            //create the endpoint at port 9999 for the client
            Socket client = new Socket("127.0.0.1", 9999);
            //get the output and inputs
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch(IOException e){
            //TODO: handle
        }
    }

    public void shutdown(){
        done = true; // set the program to being done
        //close all of the inputs and outputs, and close the client
        try{
            in.close();
            out.close();
            if (!client.isClosed()){
                client.close();
            }
        } catch(IOException e){
            //ignore
        }
    }

    class InputHandler implements Runnable{

        @Override
        public void run(){
            try{
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done){
                    String message = inReader.readLine();
                    if (message.equals("/quit")){
                        inReader.close();
                        shutdown();
                    }
                }
            } catch(IOException e){
                //TODO: handle
            }
        }
    }
}
