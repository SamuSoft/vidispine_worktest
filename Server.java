import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.lang.Runnable;
import java.math.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class Server{
  ServerSocket welcomeSocket;

  public static void main(String[] args) throws Exception{
    try{
      if(args.length > 0)
        new Server(Integer.parseInt(args[0]));
      else{
        System.err.println("No portnr specified, falling back to default (4444)");
        new Server(4444);
      }
    }catch(Exception e){
      System.err.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }
  public Server(int portnr){
    System.out.println("Server starting... \nAwaiting connections");
    while(true){
      try{
        welcomeSocket = new ServerSocket(portnr);
        int i = 1;
        while(true){
          new Thread(new CalcThread(welcomeSocket.accept()));
          System.out.println("Client "+i+" connected");
          i++;
        }
      }catch(IOException e){
        System.err.println("Failed to run as intended, restarting...");
      }
    }
  }
  /*
  / This thread is run for each connecting client.
  */
  private class CalcThread implements Runnable{
    private Socket socket;
    ObjectOutputStream out;
    BufferedReader in;

    public CalcThread(Socket socket){
      socket = this.socket;
    }
    /*
    / This code-block is called when the thread is created, and reads in and
    / data from the assigned socket. This data is parsed and then used to call
    / the private calculation method.
    */
    public void run(){
      try{
        //Creates the necessary IO from the allotted socket
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // The following part isn't pretty, but it works.
        String[] commands = in.readLine().split("/");
        assert commands[0].toUpperCase().equals("GET ");
        assert commands[1].toLowerCase().equals("mandelbrot");
        out.flush();
        ImageIO.write(calcResult(
          Double.parseDouble(commands[2]),
          Double.parseDouble(commands[3]),
          Double.parseDouble(commands[4]),
          Double.parseDouble(commands[5]),
          Math.abs(Integer.parseInt(commands[6])),
          Math.abs(Integer.parseInt(commands[7])),
          Math.abs(Integer.parseInt(commands[8]))),
          "png",out);
          socket.close();
      }catch(Exception e){
        // Fail silently
        try{
          socket.close();
        }catch(Exception ee){}
      }
    }

    /*
    / This function calculates the values for a mandelbrot picture.
    /
    / @param min_c_re C:s real lower bound
    / @param min_c_im C:s imaginary lower bound
    / @param max_c_re C:s real upper bound
    / @param max_c_im C:s imaginary upper bound
    / @param x        The number of pixels to calculate on the x axis
    / @param y        The number of pixels to calculate on the y axis
    / @param inf_n    The upper recursion limit
    / @return         An image object of a mandelbrot calculation made from the
    /                 assigned values
    */
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
    / From here on is the real Mandelbrot calculation for each pixel
    / (This is a help-function for calcResult)
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
