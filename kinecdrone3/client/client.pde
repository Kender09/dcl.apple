import processing.net.*;

import SimpleOpenNI.*;

import processing.video.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;
import java.net.DatagramPacket;  
import java.net.DatagramSocket;
import java.net.*;

import java.*;

SimpleOpenNI  kinect;

PoseOperation pose;

ArDroneOrder con;

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
  size(640*2, 800);

  chatServer = new Server(this,2001);


  kinect = new SimpleOpenNI(this);
  kinect.enableDepth();
  kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
  
  pose = new PoseOperation(kinect);

  con = new ArDroneOrder();
  con.yaw = 0;
  con.roll = 0;
  // テキストの太さ
  strokeWeight(5);

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

    cl = chatServer.available();
  if(cl !=null) println("connected");

  //ARカメラ映像の取得
  try {
    receiveSocket.receive(receivePacket);
  }
  catch(IOException e) {
  } 
  Image awtImage = Toolkit.getDefaultToolkit().createImage(receivedBytes);
  PImage receiveImage = loadImageMT(awtImage);
  //ARカメラ描画
  image(receiveImage,640,0, 640, 800);
  image(receiveImage,0,0, 640, 800);

  //kinect プログラム
  textSize(50);  
  kinect.update();  
  image(kinect.depthImage(), 0, 800-(480/4),640/4,480/4);
  image(kinect.depthImage(), 640, 800-(480/4),640/4,480/4);

  IntVector userList = new IntVector();
  kinect.getUsers(userList);
  if (userList.size() > 0) {
    int userId = userList.get(0);
    if( kinect.isTrackingSkeleton(userId) ){
      con = pose.posePressed(userId);
      msg = con.yaw + ":" + con.roll + "\n";
      println(msg);
      // drawSkeleton(userId);
      chatServer.write(msg);
    }else{
      con.yaw = 0;
      con.roll = 0;
      msg = con.yaw + ":" + con.roll + "\n";
    }
  }

}

void drawSkeleton(int userId) {
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);
  kinect.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_LEFT_HIP);
}

// user-tracking callbacks!
void onNewUser(int userId) {
  println("start pose detection");
  kinect.startPoseDetection("Psi", userId);
}

void onEndCalibration(int userId, boolean successful) {
  if (successful) { 
    println("  User calibrated !!!");
    kinect.startTrackingSkeleton(userId);
  } 
  else { 
    println("  Failed to calibrate user !!!");
    kinect.startPoseDetection("Psi", userId);
  }
}

void onStartPose(String pose, int userId) {
  println("Started pose for user");
  kinect.stopPoseDetection(userId); 
  kinect.requestCalibrationSkeleton(userId, true);
}


void keyPressed() {
    int dmy;
    msg = msg + key;
    if(key =='\n') {
      chatServer.write(msg);//サーバーに数字を送る
      msg="";
    }
}