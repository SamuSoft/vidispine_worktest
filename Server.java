import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.lang.Runnable;
import java.math.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class Server{
  ServerSocket welcomeSocket;

  public static void main(String argv[]) throws Exception{
    new Server();
  }
  public Server(){
    while(true){
      try{
        welcomeSocket = new ServerSocket(4444);
        while(true){
          new Thread(new CalcThread(welcomeSocket.accept()));
        }
      }catch(IOException e){
        System.err.println("Failed to run as intended, restarting...");
      }
    }
  }

  private class CalcThread implements Runnable{
    private Socket socket;
    ObjectOutputStream out;
    BufferedReader in;

    public CalcThread(Socket socket){
      socket = this.socket;
    }
    public void run(){
      try{
        //Creates the necessary IO from the allotted socket
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String[] commands = in.readLine().split("/");
        assert commands[0].toUpperCase().equals("GET ");
        assert commands[1].toLowerCase().equals("mandelbrot");
        out.flush();
        ImageIO.write(calcResult(
          Double.parseDouble(commands[2]),
          Double.parseDouble(commands[3]),
          Double.parseDouble(commands[4]),
          Double.parseDouble(commands[5]),
          Integer.parseInt(commands[6]),
          Integer.parseInt(commands[7]),
          Integer.parseInt(commands[8])),
          "png",out);
      }catch(Exception e){
        // Fail silently
        try{
          socket.close();
        }catch(Exception ee){}
      }
    }

    private BufferedImage calcResult(double min_c_re,
                            double min_c_im,
                            double max_c_re,
                            double max_c_im,
                            int x,
                            int y,
                            int inf_n){

      BufferedImage image = new BufferedImage(x,y,BufferedImage.TYPE_BYTE_GRAY);
      int istep = (int) ((max_c_re-min_c_re)/(double) x);
      int jstep = (int) ((max_c_im-min_c_im)/(double) y);
      int newval;
      for(int i = 0; i < x; i++)
        for(int j = 0; j < y; j++){
          newval = calcPixel(min_c_re+i*istep, min_c_im+j*jstep, inf_n);
          image.setRGB(i,j,newval);
        }
      return image;
    }
    /*
    / From here on is the real Mandelbrot calculation for c
    /
    */
    private int calcPixel(double c_re, double c_im, int n){

      double c_re_prev;
      double c_im_prev;
      double c = Math.sqrt(c_re*c_re + c_im*c_im);
      for(int i = 0; i < n; i++){
        c_re_prev = c_re;
        c_im_prev = c_im;
        c_re = c_re*c_re - c_im*c_im;
        c_im = c_re_prev * c_im_prev;
        if(Math.sqrt(c_re*c_re + c_im*c_im) > c)
          return i+1;
      }
      return n;
    }
  }
}
