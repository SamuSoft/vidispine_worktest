import java.io.*;
import java.net.*;
import java.util.ArrayList;
public class Client{
  public static void main(String[] args) {
    new Client();
  }

  public Client(){
    BufferedReader reader = new BufferedReader(System.in);
    String[] commands = reader.readLine().split(" ");
    ArrayList<String>
    try{

      double min_c_re = Double.parseDouble(commands[0]);
      double min_c_im = Double.parseDouble(commands[1]);
      double max_c_re = Double.parseDouble(commands[2]);
      double max_c_im = Double.parseDouble(commands[3]);

      boolean splitOnX = false;
      int n = Integer.parseInt(commands[4]);
      int x = Integer.parseInt(commands[5]);
      int y = Integer.parseInt(commands[6]);
      int div = Integer.parseInt(commands[7]);
      if(div == 0){
        throw new IOException();
      }
      else{
        
      }

      String[][] addresslist = new String[commands.size-7][2];
      for(int i = 8; i < commands.size; i++){
        addresslist[i-8][] = commands[i].split(":");
      }
    }catch(Exception e){
      System.err.println("An error occured");
      System.exit();
    }
  }

  private void connect(){}
}
