import processing.net.*;

import processing.video.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;
import java.net.DatagramPacket;  
import java.net.DatagramSocket;
import java.net.*;

import java.*;

DatagramPacket sendPacket;
DatagramPacket receivePacket;
DatagramSocket receiveSocket;

Server chatServer;
Client cl;

String msg;

byte[] sendBytes;
//受信するバイト配列を格納する箱
byte[] receivedBytes = new byte[300000];
 

void setup() {
  size(640, 480);

    chatServer = new Server(this,2001);

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
  
  cl =chatServer.available();
  if(cl !=null) println("connected");
  //クライアントがnullでないならループへ
  // if((cl != null)&&(cl.available()>0)) {
  //   msg=cl.readStringUntil('\n');
  //   println(msg);
  //   // chatServer.write(msg);//全員に送信
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


void keyPressed() {
    int dmy;
    msg = msg + key;
    if(key =='\n') {
      chatServer.write(msg);//サーバーに数字を送る
      msg="";
    }
}
