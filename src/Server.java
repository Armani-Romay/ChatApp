import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable{
    //list of connections for users
    private ArrayList<ConnectionHandler> connections;


//    Runnable interface allows the class to be executed concurrently with other runnable classes
    @Override
    public void run() {
        try{
            ServerSocket server = new ServerSocket(9999);
            Socket client = server.accept();

            ConnectionHandler handler = new ConnectionHandler(client);
            connections.add(handler);

        } catch (IOException e){
            //TODO: handle
        }
    }
    //broadcast message to all users
    public void broadcast(String message){
        for (ConnectionHandler ch : connections){
            if (ch != null){
                ch.sendMessage(message);
            }
        }
    }

//    Open a new connection handler for every client that connects
//     - pass the clients to it
    class ConnectionHandler implements Runnable{
        //variables
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        //constructor
        public ConnectionHandler(Socket client){
            this.client = client;
        }


        @Override      //have its own run function
        public void run() {
            try{
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                //ask the client for a nickname
                out.println("Please enter a nickname: ");
                nickname = in.readLine(); //sets the nickname to whatever the client inputs above
                System.out.println(nickname + "connected!");
                //broadcast to all users in the chat, someone joined
                broadcast(nickname + " joined the chat!");
                String message;
                while ((message = in.readLine()) != null){
                    if (message.startsWith("/nick ")){
                        //TODO: handle nickname
                    } else if (message.startsWith("/quit ")){
                        //TODO: quit
                    } else{
                        //if there is no known command, then broadcast message to all in chat
                        broadcast(nickname + ": " + message);
                    }
                }


            } catch (IOException e){
                //TODO: handle
            }
        }
        //function to send message
        public void sendMessage(String message){
            out.println(message);
        }
    }

}
