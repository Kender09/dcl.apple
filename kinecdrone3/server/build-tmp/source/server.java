import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 
import com.shigeodayo.ardrone.processing.*; 
import processing.video.*; 
import java.awt.image.*; 
import java.awt.*; 
import javax.imageio.*; 
import java.io.*; 
import java.net.DatagramPacket; 
import java.net.*; 

import org.slf4j.helpers.*; 
import com.xuggle.xuggler.video.*; 
import org.apache.commons.net.chargen.*; 
import org.apache.commons.net.bsd.*; 
import org.apache.commons.net.discard.*; 
import org.apache.commons.net.nntp.*; 
import com.shigeodayo.ardrone.command.*; 
import org.apache.commons.net.ftp.*; 
import org.apache.commons.net.finger.*; 
import org.apache.commons.net.ftp.parser.*; 
import org.apache.commons.net.ntp.*; 
import org.apache.commons.net.smtp.*; 
import com.shigeodayo.ardrone.*; 
import org.apache.commons.net.whois.*; 
import com.xuggle.ferry.*; 
import org.apache.commons.net.*; 
import org.apache.commons.net.io.*; 
import org.apache.commons.net.imap.*; 
import com.xuggle.mediatool.event.*; 
import com.xuggle.mediatool.demos.*; 
import org.apache.commons.net.tftp.*; 
import com.xuggle.xuggler.io.*; 
import org.apache.commons.net.time.*; 
import org.slf4j.impl.*; 
import com.shigeodayo.ardrone.utils.*; 
import org.apache.commons.net.echo.*; 
import com.xuggle.xuggler.*; 
import com.shigeodayo.ardrone.processing.*; 
import com.shigeodayo.ardrone.video.*; 
import org.slf4j.*; 
import com.xuggle.mediatool.*; 
import org.apache.commons.net.pop3.*; 
import org.slf4j.spi.*; 
import com.shigeodayo.ardrone.manager.*; 
import org.apache.commons.net.telnet.*; 
import com.shigeodayo.ardrone.navdata.*; 
import org.apache.commons.net.util.*; 
import com.xuggle.xuggler.demos.*; 
import org.apache.commons.net.daytime.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class server extends PApplet {









  


ARDroneForP5 ardrone;

InetSocketAddress remoteAddress;
DatagramPacket sendPacket;
DatagramPacket receivePacket;
DatagramSocket receiveSocket;
 
//\u9001\u4fe1\u3059\u308b\u30d0\u30a4\u30c8\u914d\u5217
byte[] sendBytes;

byte[] receivedBytes = new byte[300000];

Client chatClient;
float Val;
String smsg;

public void setup() {
  size(640, 480);
  String ip_addr = "localhost";

  remoteAddress = new InetSocketAddress(ip_addr,5100);


  chatClient = new Client(this, ip_addr, 2001);


  ardrone = new ARDroneForP5("192.168.1.1");
  ardrone.connect();  
  //connect to the sensor info.
  ardrone.connectNav();
  //connect to the image info.
  ardrone.connectVideo();
  //start the connections.
  ardrone.start();

}


public void draw() {
  background(204);  
  PImage img = ardrone.getVideoImage(false);
  if (img == null){
    return;
  }else{
  // image(img, 0, 0,640,480);
  }
  // capture.read();
  if(chatClient.available()>0){
    smsg=chatClient.readStringUntil('\n');
    // println(smsg);
    // \u6587\u5b57\u5217\u304b\u3089yaw,roll\u306e\u6570\u5024\u3092\u53d6\u5f97
    int [] yaw_roll = PApplet.parseInt(split(smsg, ":"));

    // ardrone\u64cd\u4f5c\u306e\u547d\u4ee4
    println(yaw_roll[0] + " : " + yaw_roll[1]);
    // ardrone.move3D(yaw_roll[0], yaw_roll[1], 0, 0);
  }

   //\u30d0\u30c3\u30d5\u30a1\u30fc\u30a4\u30e1\u30fc\u30b8\u306b\u5909\u63db
  BufferedImage bfImage = PImage2BImage(img);
  //\u30b9\u30c8\u30ea\u30fc\u30e0\u306e\u6e96\u5099
  ByteArrayOutputStream bos = new ByteArrayOutputStream();
  BufferedOutputStream os = new BufferedOutputStream(bos);
 
  try {
    bfImage.flush();
    ImageIO.write(bfImage,"jpg",os);
    os.flush();
    os.close();
  }
  catch(IOException e) {
  }
  sendBytes = bos.toByteArray();
  try {
    sendPacket = new DatagramPacket(sendBytes, sendBytes.length, remoteAddress);
    try{
      new DatagramSocket().send(sendPacket);
    } catch(IOException e){
    }
    // println("bufferImageSended:"+sendBytes.length+" bytes #2");
  }
  catch(SocketException e) {
  }
  image(img, 0, 0,640,480);
}

public BufferedImage PImage2BImage(PImage pImg) {  
    BufferedImage bImg = new BufferedImage(pImg.width, pImg.height, BufferedImage.TYPE_INT_ARGB);  
    for(int y = 0; y < bImg.getHeight(); y++) {  
        for(int x = 0; x < bImg.getWidth(); x++) {  
            bImg.setRGB(x, y, pImg.pixels[y * pImg.width + x]);  
        }  
    }  
    return bImg;  
}  

public void keyPressed() {
   if (key == 'e') {
      noLoop(); 
      exit(); //end proglam
    }

   if (key == CODED) {
    if (keyCode == UP)         ardrone.move3D(10, 0, 0, 0);//forward
    else if (keyCode == DOWN)  ardrone.move3D(-10, 0, 0, 0);//backward
    else if (keyCode == LEFT)  ardrone.move3D(0, 10, 0, 0);//go left
    else if (keyCode == RIGHT) ardrone.move3D(0, -10, 0, 0);//go right
    else if (keyCode == SHIFT){
      // ardrone.takeOff();//take off
      // println("takeOff");
      // text("takeOff", 100,100);
    }
    else if (keyCode == CONTROL) {
      ardrone.landing();//land
      text("landing", 100,100);
    }
  }
  else {
    if (key == 's') ardrone.stop();//stop
    else if (key == 'r') ardrone.move3D(0, 0, 0, -20); //spin right(cw)
    else if (key == 'l') ardrone.move3D(0, 0, 0, 20);//spin left(ccw)
    else if (key == 'u') ardrone.move3D(0, 0, -10, 0);//up
    else if (key == 'd') ardrone.move3D(0, 0, 10, 0);//down
    else if (key == 'z') ardrone.move3D(10, 10, 0, 0);//diagonally forward left
    else if (key == 'x') ardrone.move3D(-10, -10, 0, 0);//diagonally backward right
    else if (key == 'c') ardrone.move3D(10, 10, -10, 0);//diagonally forward left up
    else if (key == 'v') ardrone.move3D(-10, -10, 10, 0);//diagonally backward right down
    else if (key == 'b') ardrone.move3D(10, 0, 0, -20);//turn right
    else if (key == 'n') ardrone.move3D(10, 0, 0, 20);//turn left
  }
}

public void exit() {
  //\u3053\u3053\u306b\u7d42\u4e86\u51e6\u7406
  ardrone.stop();
  ardrone.landing();
  println("exit");
  super.exit();
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "server" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
