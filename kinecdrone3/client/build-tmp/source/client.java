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









  





// import fullscreen.*; 
// FullScreen fs;

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
//\u53d7\u4fe1\u3059\u308b\u30d0\u30a4\u30c8\u914d\u5217\u3092\u683c\u7d0d\u3059\u308b\u7bb1
byte[] receivedBytes = new byte[300000];
 

//oculur rift\u3088\u3046\u306b\u753b\u50cf\u5909\u63db
PShader barrel;
PGraphics fb;
PGraphics scene;
int eye_width = 640;
int eye_height = 800;

public void setup() {
  size(640*2, 800, P3D);

  // fs = new FullScreen(this); 
  // fs.enter();

  fb = createGraphics(width, height, P3D);
  // Create PGraphics for actual scene
  scene = createGraphics(eye_width, eye_height, P3D);
  // Load fragment shader for oculus rift barrel distortion
  barrel = loadShader("barrel_frag.glsl");  

  chatServer = new Server(this,2001);

  kinect = new SimpleOpenNI(this);
  kinect.enableDepth();
  kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
  
  pose = new PoseOperation(kinect);

  con = new ArDroneOrder();
  con.yaw = 0;
  con.roll = 0;
  // \u30c6\u30ad\u30b9\u30c8\u306e\u592a\u3055
  strokeWeight(5);

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
  background(0);

    cl = chatServer.available();
  if(cl !=null) println("connected");

  //AR\u30ab\u30e1\u30e9\u6620\u50cf\u306e\u53d6\u5f97
  try {
    receiveSocket.receive(receivePacket);
  }
  catch(IOException e) {
  } 
  Image awtImage = Toolkit.getDefaultToolkit().createImage(receivedBytes);
  PImage receiveImage = loadImageMT(awtImage);
  // AR\u30ab\u30e1\u30e9\u63cf\u753b
  // image(receiveImage,640,0, 640, 800);
  // image(receiveImage,0,0, 640, 800);

  //kinect \u30d7\u30ed\u30b0\u30e9\u30e0
  textSize(50);  
  kinect.update();  
  // image(kinect.depthImage(), 0, 800-(480/4),640/4,480/4);
  // image(kinect.depthImage(), 640, 800-(480/4),640/4,480/4);

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

  //oculusrift\u3088\u3046\u306b\u6620\u50cf\u3092\u51e6\u7406
  scene.beginDraw();
  scene.background(0);
  scene.image(receiveImage, 0, 0, 640, 800);
  scene.image(kinect.depthImage(), 0, 800-(480/2), 640/2,480/2);
  scene.translate(scene.width/2, scene.height/2, 100);
  scene.endDraw();

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

public void drawSkeleton(int userId) {
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


public void keyPressed() {
    int dmy;
    msg = msg + key;
    if(key =='\n') {
      chatServer.write(msg);//\u30b5\u30fc\u30d0\u30fc\u306b\u6570\u5b57\u3092\u9001\u308b
      msg="";
    }
}

public void set_shader(String eye)
{
  float x = 0.0f;
  float y = 0.0f;
  float w = 0.5f;
  float h = 1.0f;
  float DistortionXCenterOffset = 0.25f;
  float as = w/h;

  float K0 = 1.0f;
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
public  int count;

class ArDroneOrder{
  int yaw;
  int roll;
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
  
  PVector rightFoot = new PVector();
  PVector rightKnee = new PVector();
  PVector rightHip = new PVector();
  PVector leftFoot = new PVector();
  PVector leftKnee = new PVector();
  PVector leftHip = new PVector();
  
  float baseScale;
  int flag;
  int move_speed = 50;
  
  final int DelayTime = 10;
  
  float playerRoll;
  float playerYaw;

  PoseOperation(SimpleOpenNI context){
    this.context = context;
    count = 0;
    flag = 0;
    textSize(50);
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

    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_FOOT, rightFoot);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, rightKnee);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HIP, rightHip);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_FOOT, leftFoot);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_KNEE, leftKnee);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HIP, leftHip);  

    baseScale = head.y - torso.y;
    
    playerRoll = leftHand.z - rightHand.z;

    playerYaw = (rightHand.y + leftHand.y)/2.0f;
    
    if(playerYaw > head.y){
      playerYaw = playerYaw - head.y;
    }else if(playerYaw < (torso.y + baseScale/3.0f) ){
      playerYaw = playerYaw - (torso.y + baseScale/3.0f);
    }else{
      playerYaw = 0;
    }
    
    ArDroneOrder poseCon = new ArDroneOrder();

    // println("playerRoll: " + playerRoll);
    // println("playerYaw: " + playerYaw);

    playerRoll = playerRoll/move_speed;
    playerYaw = playerYaw/(move_speed-10);

    // println("playerRoll: " + playerRoll);
    // println("playerYaw: " + playerYaw);

    if(abs(playerRoll) < 5){
      playerRoll = 0;
    }else{
      if(playerRoll>0){
        text("left", 100,200);
        text("left", 700,200);
        if(playerRoll > 30){
          playerRoll = 30;
        }
      }else if(playerRoll<0){
        text("right", 100,200);
        text("right", 700,200);
        if(playerRoll < -30){
          playerRoll = -30;
        }
      }
    }

    if(abs(playerYaw) <5){
      playerYaw = 0;
    }else{
      if(playerYaw>0){
        text("forward", 100,100);
        text("forward", 700,100);
        if(playerYaw>30){
          playerYaw = 30;
        }
      }else if(playerYaw<0){
        text("back", 100,100);
        text("back", 700,100);
          playerYaw  = playerYaw;
        if(playerYaw<-30){
          playerYaw = -30;
        }
      }
    }

    if(playerYaw != 0 || playerRoll != 0){
      stroke(0,255,255);
      poseCon.yaw = (int)playerYaw;
      poseCon.roll = (int)playerRoll;
    }else{
      stroke(255,255,255);
      poseCon.yaw = 0;
      poseCon.roll = 0;
    } 
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
