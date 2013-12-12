import processing.net.*;
import com.shigeodayo.ardrone.processing.*;
import java.io.*;
import java.net.*;
import java.awt.image.*;
import javax.imageio.*;

ARDroneForP5 ardrone;

InetSocketAddress remoteAddress;
DatagramPacket sendPacket;
//送信するバイト配列
byte[] sendBytes;

Server droneConSever;
Client droneConClient;
float Val;
String droneConMsg;
String testMsg;
int startFlag = 0;
Thread droneConThread;
String ipAddr;
int conectFlag=0;

// PImage testImg;

void setup() {
  size(640, 480);

  droneConSever = new Server(this,2001);
  // testImg = loadImage("buzz2.jpg");
  ardrone = new ARDroneForP5("192.168.1.1");
  ardrone.connect();  
  //connect to the sensor info.
  ardrone.connectNav();
  //connect to the image info.
  ardrone.connectVideo();
  //start the connections.
  ardrone.start();

  textSize(30);  
}

void draw() {
  background(0);  
  PImage img = ardrone.getVideoImage(false);
  // PImage img = testImg;
  if (img == null){
    startFlag = 0;
    return;
  }else{
    startFlag = 1;
    image(img, 0, 0,640,480);
  }

  if(conectFlag == 0){
    if(droneConSever.available() != null){
      droneConClient = droneConSever.available();
      testMsg=droneConClient.readStringUntil('\n');
      ardroneMoveThread movethread = new ardroneMoveThread();
      droneConThread = new Thread(movethread);
      droneConThread.start();
      ipAddr = droneConClient.ip();
      remoteAddress = new InetSocketAddress(ipAddr,5100);
      println(ipAddr + testMsg);
      conectFlag = 1;
    }else{
      return;
    }
  }
  fill(250, 0, 0);
  text(ipAddr,100,100);

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
    text("error0",100,150);
  }
  sendBytes = bos.toByteArray();
  try {
    sendPacket = new DatagramPacket(sendBytes, sendBytes.length, remoteAddress);
    try{
      new DatagramSocket().send(sendPacket);
    } catch(IOException e){
      text("error1",100,200);
    }
  }
  catch(SocketException e) {
    text("error2",100,300);
  }
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
      droneConThread.stop();
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

public class ardroneMoveThread implements Runnable{

  public void run() {
    while (true){

      if(startFlag != 1 && conectFlag != 1){
        continue;
      }
      if(droneConSever.available() != null){
        droneConClient = droneConSever.available();
        droneConMsg=droneConClient.readStringUntil('\n');
        // println(droneConMsg);
        // 文字列からyaw,rollの数値を取得
        int [] yaw_roll = int(split(droneConMsg, ":"));
        // ardrone操作の命令
        println(yaw_roll[0] + " : " + yaw_roll[1] + " : " + yaw_roll[2]);
        fill(250, 250, 250);
        text(yaw_roll[0] + " : " + yaw_roll[1] + " : " + yaw_roll[2], 100,300);
        // ardrone.move3D(yaw_roll[0], yaw_roll[1], 0, yaw_roll[2]);  //AR.Droneに命令を送る
      }

    }
  }

}
