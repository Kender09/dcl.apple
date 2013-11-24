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


void draw() {
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
  image(img, 0, 0,640,480);
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
}


