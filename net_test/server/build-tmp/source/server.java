import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 
import processing.video.*; 
import java.awt.image.*; 
import java.awt.*; 
import javax.imageio.*; 
import java.net.DatagramPacket; 
import java.net.DatagramSocket; 
import java.net.*; 
import java.*; 

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







  





DatagramPacket sendPacket;
DatagramPacket receivePacket;
DatagramSocket receiveSocket;

Server chatServer;
Client cl;

String msg;

byte[] sendBytes;
//\u53d7\u4fe1\u3059\u308b\u30d0\u30a4\u30c8\u914d\u5217\u3092\u683c\u7d0d\u3059\u308b\u7bb1
byte[] receivedBytes = new byte[300000];
 

public void setup() {
  size(640, 480);
  
    // remoteAddress = new InetSocketAddress("localhost",5000);

    chatServer = new Server(this,2001);

  try {
    //\u53d7\u4fe1\u30dd\u30fc\u30c8
    receiveSocket = new DatagramSocket(5100);
  }
  catch(SocketException e) {
  }
  //\u53d7\u4fe1\u7528\u30d1\u30b1\u30c3\u30c8
  receivePacket = new DatagramPacket(receivedBytes,receivedBytes.length);
  try{
    receiveSocket.setSoTimeout(1000);
  }catch(SocketException e){
  }
}


public void draw() {
  background(204);
  
  cl =chatServer.available();
  if(cl !=null) println("connected");
  //\u30af\u30e9\u30a4\u30a2\u30f3\u30c8\u304cnull\u3067\u306a\u3044\u306a\u3089\u30eb\u30fc\u30d7\u3078
  // if((cl != null)&&(cl.available()>0)) {
  //   msg=cl.readStringUntil('\n');
  //   println(msg);
  //   // chatServer.write(msg);//\u5168\u54e1\u306b\u9001\u4fe1
  // }

  try {
    receiveSocket.receive(receivePacket);
  }
  catch(IOException e) {
  }
 
  Image awtImage = Toolkit.getDefaultToolkit().createImage(receivedBytes);
  PImage receiveImage = loadImageMT(awtImage);

  image(receiveImage,0,0, 640, 480);

}


public void keyPressed() {
    int dmy;
    msg = msg + key;
    if(key =='\n') {
      chatServer.write(msg);//\u30b5\u30fc\u30d0\u30fc\u306b\u6570\u5b57\u3092\u9001\u308b
      msg="";
    }
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
