import processing.net.*;
import com.shigeodayo.ardrone.processing.*;

import processing.video.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;
import java.net.DatagramPacket;  
import java.net.*;

ARDroneForP5 ardrone;

InetSocketAddress remoteAddress;
DatagramPacket sendPacket;
DatagramPacket receivePacket;
DatagramSocket receiveSocket;
 
//送信するバイト配列
byte[] sendBytes;

byte[] receivedBytes = new byte[300000];

Client chatClient;
float Val;
String smsg;

void setup() {
  size(640, 480);
  String ip_addr = "192.168.10.30";

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

  textSize(50);  

}


void draw() {
  background(0);  
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
    // 文字列からyaw,rollの数値を取得
    int [] yaw_roll = int(split(smsg, ":"));

    // ardrone操作の命令
    println(yaw_roll[0] + " : " + yaw_roll[1] + " : " + yaw_roll[2]);
    text(yaw_roll[0] + " : " + yaw_roll[1] + " : " + yaw_roll[2], 400,100);
    // ardrone.move3D(yaw_roll[0], yaw_roll[1], 0, yaw_roll[2]);  //AR.Droneに命令を送る
  }

   //バッファーイメージに変換
  BufferedImage bfImage = PImage2BImage(img);
  //ストリームの準備
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
  // image(img, 0, 0,640,480);
}

BufferedImage PImage2BImage(PImage pImg) {  
    BufferedImage bImg = new BufferedImage(pImg.width, pImg.height, BufferedImage.TYPE_INT_ARGB);  
    for(int y = 0; y < bImg.getHeight(); y++) {  
        for(int x = 0; x < bImg.getWidth(); x++) {  
            bImg.setRGB(x, y, pImg.pixels[y * pImg.width + x]);  
        }  
    }  
    return bImg;  
}  

void keyPressed() {
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
      ardrone.takeOff();//take off
      println("takeOff");
      text("takeOff", 100,100);
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

void keyReleased() {
  ardrone.stop();
}

void exit() {
  //ここに終了処理
  ardrone.stop();
  ardrone.landing();
  println("exit");
  super.exit();
}