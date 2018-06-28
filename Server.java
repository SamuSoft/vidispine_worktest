import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.lang.Runnable;

public class Server{
  public static void main(String argv[]) throws Exception{
    ServerSocket welcomeSocket = new ServerSocket(4444);

    while(true){
      new Thread(Calculate(welcomeSocket.accept()));
    }
  }

  private class CalcThread implements Runnable{
    private Socket socket;

    public CalcThread(Socket socket){
      socket = this.socket;
    }
    public void run(){
      try{
        //TODO
      }catch(){
        socket.close();
      }
    }

    private void calcResult(){
      //TODO
    }
  }
}
