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

public class client extends PApplet {









  


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

  remoteAddress = new InetSocketAddress("192.168.10.42",5100);


  chatClient = new Client(this, "192.168.10.42", 2001);


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
    println(smsg);
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
}


  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "client" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
