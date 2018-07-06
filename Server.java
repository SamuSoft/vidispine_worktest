import java.io.*;
import java.net.*;
import java.lang.Thread;
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
        System.err.println("No portnr specified, falling back to default (9090)");
        new Server(9090);
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
        int clientnr = 0;
        while(true){
          Thread t = new Thread(new CalcThread(welcomeSocket.accept(), clientnr++));
          t.start();

        }
      }catch(IOException e){
        System.err.println("Failed to run as intended, restarting...");
      }
    }
  }
  /*
  / This thread is run for each connecting client.
  */
  private class CalcThread extends Thread{
    private Socket socket;
    private ObjectOutputStream out;
    private BufferedReader in;
    private int clientnr;


    public CalcThread(Socket socket, int clientnr){
      this.socket = socket;
      this.clientnr = clientnr;
      System.out.println("Client number "+clientnr+" connected");
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
        System.out.printf("Client connection %d finished\n", clientnr);
        socket.close();
      }catch(Exception e){
        e.printStackTrace();

        try{
          socket.close();
        }catch(Exception ee){

        }
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

      BufferedImage image = new BufferedImage(x,y,BufferedImage.TYPE_INT_RGB);
      BufferedImage gray = new BufferedImage(x,y,BufferedImage.TYPE_BYTE_GRAY);
      ColorConvertOp filter = new ColorConvertOp(
                                image.getColorModel().getColorSpace(),
                                gray.getColorModel().getColorSpace(),
                                null);
      double istep =  ((max_c_re-min_c_re)/(double) x);
      double jstep =  ((max_c_im-min_c_im)/(double) y);
      int newval;
      for(int i = 0; i < x; i++){
        for(int j = 0; j < y; j++){
          newval = calcPixel(min_c_re+i*istep, min_c_im+j*jstep, inf_n);
          image.setRGB(i,j,newval%256);
        }
      }
      filter.filter(image,gray);
      return gray;
    }
    /*
    / From here on is the real Mandelbrot calculation for each pixel
    / (This is a help-function for calcResult)
    */
    private int calcPixel(double c_re, double c_im, int n){

      double u = 0;
      double v = 0;
      double u2 = 0;
      double v2 = 0;
      double c = c_re*c_re + c_im*c_im;
      for(int i = 0; i < n; i++){
        v = 2*u*v+c_im;
        u= u2-v2+c_re;
        u2=u*u;
        v2=v*v;
        if(u2+v2 > 4)
          return i+1;
      }
      return n;
    }
  }
}
