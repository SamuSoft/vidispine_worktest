import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.regex.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.nio.file.Path;
import java.nio.file.Paths;
public class Client{

  private BufferedImage[] images;

  public void saveToImageBuffer(BufferedImage image, int index) throws Exception{
    if(images == null){
      throw new Exception("Image buffer not yet initialized");
    }
    images[index] = image;
  }

  public static void main(String[] args) {
    try{

      if(args.length <= 8){
        throw new Exception();
      }
      double min_c_re = Double.parseDouble(args[0]);
      double min_c_im = Double.parseDouble(args[1]);
      double max_c_re = Double.parseDouble(args[2]);
      double max_c_im = Double.parseDouble(args[3]);

      int n = Integer.parseInt(args[4]);
      int x = Integer.parseInt(args[5]);
      int y = Integer.parseInt(args[6]);
      int div = Integer.parseInt(args[7]);
      if(div == 0){
        throw new Exception();
      }

      //Split up the addresses and port nums
      String[][] addresslist = new String[args.length-8][2];
      for(int i = 8; i < args.length; i++){
        addresslist[i-8] = args[i].split(":");
      }
      new Client(min_c_re, min_c_im, max_c_re, max_c_im, n, x, y, div, addresslist);
    }catch(Exception e){
      e.printStackTrace();
      System.err.println("Invalid input");
      System.exit(0);
    }

  }

  /*
  / This client connects to servers in its addresslist and pushes work to them.
  / Each piece of work is pushed as a slice of the final image, and as such only
  / the divition parameter is limited to the biggest picture dimension (X or Y)
  / in pixels.
  / @param  min_c_re  C:s real lower limit
  / @param  min_c_im  C:s imaginary lower limit
  / @param  max_c_re  C:s real upper limit
  / @param  max_c_im  C:s imaginary upper limit
  / @param  n         Maximum amount of recursion per pixel
  / @param  x         Horizontal pixel count
  / @param  y         Vertical pixel count
  / @param  div       The ammount of pieces to split the work into
  */
  public Client(double min_c_re,
                double min_c_im,
                double max_c_re,
                double max_c_im,
                int n,
                int x,
                int y,
                int div,
                String[][] addresslist)
  {
    double c_re_stepval = (max_c_re-min_c_re)/x;
    double c_im_stepval = (max_c_im-min_c_im)/y;
    if(div <= x || div <= y){

    }else{
      //TODO
      //If bigger than the dimensions of the picture use kdtree algorithm
      //lookalike to split picture
    }
    try{

      images = new BufferedImage[1];
      Thread t = new ClientThread(this,
                                  min_c_re,
                                  min_c_im,
                                  max_c_re,
                                  max_c_im,
                                  n,
                                  x,
                                  y, InetAddress.getLocalHost(),9090,0);
      t.start();

      // Writing image to file
      File f = new File("picture.png");
      ImageIO.write(images[0], "png", f);
    }catch(Exception e){}


  }

  private class ClientThread extends Thread{
    Client par;
    double min_c_re;
    double min_c_im;
    double max_c_re;
    double max_c_im;
    int n;
    int x;
    int y;
    InetAddress address;
    int portnr;
    int id;
    public ClientThread(Client par,
                        double min_c_re,
                        double min_c_im,
                        double max_c_re,
                        double max_c_im,
                        int n,
                        int x,
                        int y,
                        InetAddress address,
                        int portnr,
                        int id){
      this.par = par;
      this.min_c_re = min_c_re;
      this.min_c_im = min_c_im;
      this.max_c_re = max_c_re;
      this.max_c_im = max_c_im;
      this.n = n;
      this.x = x;
      this.y = y;
      this.address = address;
      this.portnr = portnr;
      this.id = id;
    }

    public void run(){
      try{

        Socket socket = new Socket(address, portnr);
        //Autoflush on
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        String command = String.format("GET /mandelbrot/%a/%a/%a/%a/%d/%d/%d",
                    min_c_re,
                    min_c_im,
                    max_c_re,
                    max_c_im,
                    x,
                    y,
                    n);
        out.println(command);

        BufferedImage image = ImageIO.read(in);
        socket.close();

        // Saving image to parent
        synchronized(par){
          par.saveToImageBuffer(image,id);
        }
      }catch(Exception e){
        System.err.println(e.getMessage());
        System.err.printf("Thread %d has failed%n", id);
      }
    }

  }

}
