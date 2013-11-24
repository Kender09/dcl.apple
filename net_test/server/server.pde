import processing.net.*;

import processing.video.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;
import java.net.DatagramPacket;  
import java.net.DatagramSocket;
import java.net.*;

import java.*;

// InetSocketAddress remoteAddress;
DatagramPacket sendPacket;
DatagramPacket receivePacket;
DatagramSocket receiveSocket;

// Server server;

byte[] sendBytes;
//受信するバイト配列を格納する箱
byte[] receivedBytes = new byte[300000];
 

void setup() {
  size(640, 480);
  
    remoteAddress = new InetSocketAddress("localhost",5000);

  try {
    //受信ポート
    receiveSocket = new DatagramSocket(5100);
  }
  catch(SocketException e) {
  }
  //受信用パケット
  receivePacket = new DatagramPacket(receivedBytes,receivedBytes.length);
  try{
    receiveSocket.setSoTimeout(1000);
  }catch(SocketException e){
  }
}


void draw() {
  background(204);
  // Client c = server.available();
  // if(c != null){
  //   String s = c.readString();
  //   text("S",100,100);
  //   println(s);
  //   server.write(s);
  // }

  // println(receiveSocket.isBound());
  try {
    receiveSocket.receive(receivePacket);
  }
  catch(IOException e) {
  }
 
  Image awtImage = Toolkit.getDefaultToolkit().createImage(receivedBytes);
  PImage receiveImage = loadImageMT(awtImage);

  image(receiveImage,0,0, 640, 480);

}


void keyPressed() {

    if (key == 's') {
      
    }
}
