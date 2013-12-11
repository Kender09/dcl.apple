import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

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









  





SimpleOpenNI  kinect;

PoseOperation pose;

ArDroneOrder con;

DatagramPacket sendPacket;
DatagramPacket receivePacket;
DatagramSocket receiveSocket;

Server chatServer;
Client cl;
String ip_addr;
int flag1 = 0, flag2 = 0;

String msg;

byte[] sendBytes;
//\u53d7\u4fe1\u3059\u308b\u30d0\u30a4\u30c8\u914d\u5217\u3092\u683c\u7d0d\u3059\u308b\u7bb1
byte[] receivedBytes = new byte[300000];
 
//oculur rift\u3088\u3046\u306b\u753b\u50cf\u5909\u63db
PShader barrel;
PGraphics fb;
PGraphics scene;
int eye_width = 640;
int eye_height = 800;

int drawKinectFlag = 1;

PImage receiveImage;

public void setup() {
  size(640*2, 800, P3D);

  fb = createGraphics(width, height, P3D);
  // Create PGraphics for actual scene
  scene = createGraphics(eye_width, eye_height, P3D);
  // Load fragment shader for oculus rift barrel distortion
  barrel = loadShader("barrel_frag.glsl");  

  // kinect = new SimpleOpenNI(this);
  // kinect.enableDepth();
  // kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
  
  pose = new PoseOperation(kinect);

  con = new ArDroneOrder();
  con.yaw = 0;
  con.roll = 0;

  frameRate(10);
  textSize(50);
}


public void draw() {
  background(0);

  if(flag1 == 0){

    return;
  }else if(flag1 == 1){
    ip_addr = "localhost";
  }

  if(flag2 == 0){
    cl =  new Client(this, ip_addr, 2001);

    flag2 = 1;
      cl.write("test\n");
      text(ip_addr,100,100);
    

    try {
    //\u53d7\u4fe1\u30dd\u30fc\u30c8
      receiveSocket = new DatagramSocket(5100);
    }catch(SocketException e) {
      println("receive err");
    }
    //\u53d7\u4fe1\u7528\u30d1\u30b1\u30c3\u30c8
      receivePacket = new DatagramPacket(receivedBytes,receivedBytes.length);
    try{
        receiveSocket.setSoTimeout(1000);
      }catch(SocketException e){
        println("packet err");
    }
    return;
  }

  int flag5 = 1;
  //AR\u30ab\u30e1\u30e9\u6620\u50cf\u306e\u53d6\u5f97
  try {
    receiveSocket.receive(receivePacket);
  }
  catch(IOException e) {
    text("miss",100,100);
    flag5 = 0;
  } 
  if(flag5 != 0){
    Image awtImage = Toolkit.getDefaultToolkit().createImage(receivedBytes);
    receiveImage = loadImageMT(awtImage);
  }

  //kinect \u30d7\u30ed\u30b0\u30e9\u30e0
  // kinect.update();  

  // IntVector userList = new IntVector();
  // kinect.getUsers(userList);
  // if (userList.size() > 0) {
  //   int userId = userList.get(0);
  //   if( kinect.isTrackingSkeleton(userId) ){
  //     drawKinectFlag = 0;
  //     con = pose.posePressed(userId);
  //     msg = con.yaw + ":" + con.roll +  ":" + con.spin + "\n";
  //     println(msg);
  //     cl.write(msg);
  //   }else{
  //     drawKinectFlag = 1;
  //     con.yaw = 0;
  //     con.roll = 0;
  //     msg = con.yaw + ":" + con.roll + ":" + con.spin + "\n";
  //   }
  // }

  //oculusrift\u3088\u3046\u306b\u6620\u50cf\u3092\u5408\u6210
  scene.beginDraw();
  scene.background(0);
  if(receiveImage != null)
    scene.image(receiveImage, 0, 0, 640, 800);
  if(drawKinectFlag == 1){
    // scene.image(kinect.depthImage(), 320-((640*0.8)/2), 400-((480*0.8)/2), 640*0.8,480*0.8);
  }else if(drawKinectFlag == 0){
    // scene.image(kinect.depthImage(), 0, 800-(480*0.8), 640*0.8,480*0.8);
    scene.textSize(30);
    scene.fill(250, 0, 0);
    scene.text(msg,250, 500);
  }
  scene.translate(scene.width/2, scene.height/2, 100);
  scene.endDraw();

  //\u52a0\u5de5\u3057\u305f\u753b\u50cf\u3092\u63cf\u753b
  blendMode(ADD);
   // Render left eye
  set_shader("left");
  shader(barrel);
  fb.beginDraw();
  fb.background(0);
  fb.image(scene, 50, 0, eye_width, eye_height);
  fb.endDraw();
  image(fb, 0, 0);
  
  resetShader();
  
  // Render right eye
  set_shader("right");
  shader(barrel);
  fb.beginDraw();
  fb.background(0);
  fb.image(scene, eye_width-50, 0, eye_width, eye_height);
  fb.endDraw();
  image(fb, 0, 0);

}

// user-tracking callbacks!
public void onNewUser(int userId) {
  println("start pose detection");
  kinect.startPoseDetection("Psi", userId);
}

public void onEndCalibration(int userId, boolean successful) {
  if (successful) { 
    println("  User calibrated !!!");
    kinect.startTrackingSkeleton(userId);
  } 
  else { 
    println("  Failed to calibrate user !!!");
    kinect.startPoseDetection("Psi", userId);
  }
}

public void onStartPose(String pose, int userId) {
  println("Started pose for user");
  kinect.stopPoseDetection(userId); 
  kinect.requestCalibrationSkeleton(userId, true);
}

public void set_shader(String eye)
{
  float x = 0.0f;
  float y = 0.0f;
  float w = 0.5f;
  float h = 1.0f;
  float DistortionXCenterOffset = 0.25f;
  float as = w/h;

  float K0 = 1.5f;
  float K1 = 0.22f;
  float K2 = 0.24f;
  float K3 = 0.0f;

  float scaleFactor = 0.7f;

  if (eye == "left")
  {
    x = 0.0f;
    y = 0.0f;
    w = 0.5f;
    h = 1.0f;
    DistortionXCenterOffset = 0.25f;
  }
  else if (eye == "right")
  {
    x = 0.5f;
    y = 0.0f;
    w = 0.5f;
    h = 1.0f;
    DistortionXCenterOffset = -0.25f;
  }

  barrel.set("LensCenter", x + (w + DistortionXCenterOffset * 0.5f)*0.5f, y + h*0.5f);
  barrel.set("ScreenCenter", x + w*0.5f, y + h*0.5f);
  barrel.set("Scale", (w/2.0f) * scaleFactor, (h/2.0f) * scaleFactor * as);
  barrel.set("ScaleIn", (2.0f/w), (2.0f/h) / as);
  barrel.set("HmdWarpParam", K0, K1, K2, K3);
}

public void keyPressed() {
   if (key == 'e') {
      flag1 = 1;
    }
    if(key == 'a'){
      cl.write("test\n");
    }
}
public  int count;

class ArDroneOrder{
  int yaw;
  int roll;
  int spin;
}

class PoseOperation{
  SimpleOpenNI context;
  
  PVector rightHand = new PVector();
  PVector rightElbow = new PVector();
  PVector rightShoulder = new PVector();
  PVector leftHand = new PVector();
  PVector leftElbow = new PVector();
  PVector leftShoulder = new PVector();
  
  PVector head = new PVector();
  PVector neck = new PVector();
  PVector torso = new PVector();
  
  // PVector rightFoot = new PVector();
  // PVector rightKnee = new PVector();
  // PVector rightHip = new PVector();
  // PVector leftFoot = new PVector();
  // PVector leftKnee = new PVector();
  // PVector leftHip = new PVector();
  
  float baseScale;
  float hhhead;
  int flag;
  int move_speed = 50;

  final int DelayTime = 10;
  
  float playerRoll;
  float playerYaw;
  float playerSpin;

  float[] ave_playerRoll = new float[5];
  float[] ave_playerYaw = new float[5];
  float[] ave_playerSpin = new float[5];

  int ave_count;

  PoseOperation(SimpleOpenNI context){
    this.context = context;
    count = 0;
    flag = 0;
    ave_count = 0;
    for(int i = 0; i<5; i++){
      ave_playerRoll[i] = 0;
      ave_playerYaw[i] = 0;
      ave_playerSpin[i] = 0;
    }
  }

  public ArDroneOrder posePressed(int userId){

    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, rightHand);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, rightElbow);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, rightShoulder);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, leftHand);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, leftElbow);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, leftShoulder);     

    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_HEAD, head);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_NECK, neck);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_TORSO, torso);

    // context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_FOOT, rightFoot);
    // context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, rightKnee);
    // context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HIP, rightHip);
    // context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_FOOT, leftFoot);
    // context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_KNEE, leftKnee);
    // context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HIP, leftHip);  

    baseScale = head.y - torso.y;
    
    hhhead = (head.y + neck.y)/2.0f;

    playerRoll = leftHand.z - rightHand.z;

    playerYaw = (rightHand.y + leftHand.y)/2.0f;

    playerSpin = (rightHand.y - leftHand.y);
    
    if(playerYaw > hhhead){
      playerYaw = playerYaw - hhhead;
    }else if(playerYaw < (torso.y + baseScale/3.0f) ){
      playerYaw = playerYaw - (torso.y + baseScale/3.0f);
    }else{
      playerYaw = 0;
    }
    
    ArDroneOrder poseCon = new ArDroneOrder();

    // println("playerRoll: " + playerRoll);
    // println("playerYaw: " + playerYaw);

    playerYaw = playerYaw/(move_speed-20);
    playerRoll = playerRoll/(move_speed-5);
    playerSpin = playerSpin/(move_speed+10);

    // println("playerRoll: " + playerRoll);
    // println("playerSpin: " + playerSpin);
    ave_playerRoll[ave_count] = playerRoll;
    ave_playerYaw[ave_count] = playerYaw;
    ave_playerSpin[ave_count] = playerSpin;
    ave_count = (ave_count+1)%5;

    float ave = 0;
    float subave = 0;
    int c;
    for(c = 0; c < 5; c++){
      ave = ave + ave_playerRoll[c];
    }
    ave = ave/5.0f;
    subave = ave - playerRoll;
    if(abs(subave) > 5){
      playerRoll = ave;
    }
    ave = 0;

    for(c = 0; c < 5; c++){
      ave = ave + ave_playerYaw[c];
    }
    ave = ave/5.0f;
    subave = ave - playerYaw;
    if(abs(subave) > 5){
      playerYaw = ave;
    }
    ave = 0;

    for(c = 0; c < 5; c++){
      ave = ave + ave_playerSpin[c];
    }
    ave = ave/5.0f;
    subave = ave - playerSpin;
    if(abs(subave) > 5){
      playerSpin = ave;
    }
    ave = 0;



    if(abs(playerRoll) < 5){
      playerRoll = 0;
    }else{
      if(playerRoll>0){
        // text("left", 100,200);
        // text("left", 700,200);
        if(playerRoll > 30){
          playerRoll = 30;
        }
      }else if(playerRoll<0){
        // text("right", 100,200);
        // text("right", 700,200);
        if(playerRoll < -30){
          playerRoll = -30;
        }
      }
    }

    if(abs(playerYaw) <5){
      playerYaw = 0;
    }else{
      if(playerYaw>0){
        // text("forward", 100,100);
        // text("forward", 700,100);
        if(playerYaw>30){
          playerYaw = 30;
        }
      }else if(playerYaw<0){
        // text("back", 100,100);
        // text("back", 700,100);
        if(playerYaw<-30){
          playerYaw = -30;
        }
      }
    }

    if(abs(playerSpin) <10){
      playerSpin = 0;
    }else{
      if(playerSpin>0){
        // text("spinL", 100,300);
        // text("spinL", 700,300);
        if(playerSpin>30){
          playerSpin = 30;
        }
      }else if(playerSpin<0){
        // text("spinR", 100,300);
        // text("spinR", 700,300);
        if(playerSpin<-30){
          playerSpin = -30;
        }
      }
    }

    if(playerSpin != 0){
      playerYaw = 0;
      playerRoll = 0;
    }


    poseCon.yaw = (int)playerYaw;
    poseCon.roll = (int)playerRoll;
    poseCon.spin = (int)playerSpin;

    // if(playerYaw != 0 || playerRoll != 0 || playerSpin != 0){
    //   stroke(0,255,255);
    // }else{
    //   stroke(255,255,255);
    // } 
    return poseCon;
  }
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
