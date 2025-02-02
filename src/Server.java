import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{
    //list of connections for users
    private ArrayList<ConnectionHandler> connections;  //initialize array
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool; //threadpool

    public Server() {
        connections = new ArrayList<>();
        done = false;
    }


//    Runnable interface allows the class to be executed concurrently with other runnable classes
    @Override
    public void run() {
        try{
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();

            while (!done){
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }

        } catch (IOException e){
            shutdown();
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

    public void shutdown(){
        try {
            done = true;
            if (!server.isClosed()) {
                server.close();
            }
            for (ConnectionHandler ch : connections){
                ch.shutdown();
            }
        } catch (IOException e) {
            //ignore

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
                    //command to change nickname
                    if (message.startsWith("/nick ")){
                        //split the message at the whitespace
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2){
                            broadcast(nickname + "renamed themselves to " + messageSplit[1]);
                            System.out.println(nickname + "renamed themselves to " + messageSplit[1]);
                            nickname = messageSplit[1];// new nickname is the second part of the message
                            out.println("Successfully changed nickname  to " + nickname);
                        } else{
                            out.println("No nickname provided.");
                        }
                    } else if (message.startsWith("/quit ")){
                        broadcast(nickname + " left the chat!");
                        shutdown();
                    } else{
                        //if there is no known command, then broadcast message to all in chat
                        broadcast(nickname + ": " + message);
                    }
                }


            } catch (IOException e){
                shutdown();
            }
        }
        //function to send message
        public void sendMessage(String message){
            out.println(message);
        }

        public void shutdown(){
            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
            }catch (IOException e){
                //ignore
            }
        }
    }
    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
