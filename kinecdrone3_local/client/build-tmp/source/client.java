import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 
import SimpleOpenNI.*; 
import com.shigeodayo.ardrone.processing.*; 

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
SimpleOpenNI  kinect;
PoseOperation pose;
ArDroneOrder con;
String droneConMsg;
int drawKinectFlag = 1;
//oculur rift\u3088\u3046\u306b\u753b\u50cf\u5909\u63db
PShader barrel;
PGraphics fb;
PGraphics scene;
int eye_width = 640;
int eye_height = 800;

public void setup() {
  size(640*2, 800, P3D);

  ardrone = new ARDroneForP5("192.168.1.1");
  //connect to the AR.Drone.
  ardrone.connect();
  //connect to the sensor info.
  ardrone.connectNav();
  //connect to the image info.
  ardrone.connectVideo();
  //start the connections.
  ardrone.start();

  fb = createGraphics(width, height, P3D);
  // Create PGraphics for actual scene
  scene = createGraphics(eye_width, eye_height, P3D);
  // Load fragment shader for oculus rift barrel distortion
  barrel = loadShader("barrel_frag.glsl");  

  kinect = new SimpleOpenNI(this);
  kinect.enableDepth();
  kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
  
  pose = new PoseOperation(kinect);
  con = new ArDroneOrder();
  con.yaw = 0;
  con.roll = 0;
}


public void draw() {
  background(0);
  //AR\u30ab\u30e1\u30e9\u6620\u50cf\u306e\u53d6\u5f97
  PImage img = ardrone.getVideoImage(false);

  //kinect \u30d7\u30ed\u30b0\u30e9\u30e0
  textSize(50);  
  kinect.update();  

  IntVector userList = new IntVector();
  kinect.getUsers(userList);
  if (userList.size() > 0) {
    int userId = userList.get(0);
    if( kinect.isTrackingSkeleton(userId) ){
      drawKinectFlag = 0;
      con = pose.posePressed(userId);
      droneConMsg = con.yaw + ":" + con.roll +  ":" + con.spin + "\n";
      println(droneConMsg);
      ardrone.move3D(con.yaw, con.roll, 0, con.spin);
    }else{
      drawKinectFlag = 1;
      con.yaw = 0;
      con.roll = 0;
      droneConMsg = con.yaw + ":" + con.roll + ":" + con.spin + "\n";
      ardrone.landing();
    }
  }

  //oculusrift\u3088\u3046\u306b\u6620\u50cf\u3092\u5408\u6210
  scene.beginDraw();
  scene.background(0);
  if(img != null)
    scene.image(img, 0, 0, 640, 800);

  if(drawKinectFlag == 1){
    scene.image(kinect.depthImage(), 320-((640*0.8f)/2), 400-((480*0.8f)/2), 640*0.8f,480*0.8f);
  }else if(drawKinectFlag == 0){
    // scene.image(kinect.depthImage(), 0, 800-(480*0.8), 640*0.8,480*0.8);
    scene.textSize(30);
    scene.fill(250, 0, 0);
    scene.text(droneConMsg,150, 500);
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


public void keyPressed() {
  if (key == CODED) {
    if (keyCode == UP)         ardrone.move3D(10, 0, 0, 0);//forward
    else if (keyCode == DOWN)  ardrone.move3D(-10, 0, 0, 0);//backward
    else if (keyCode == LEFT)  ardrone.move3D(0, 10, 0, 0);//go left
    else if (keyCode == RIGHT) ardrone.move3D(0, -10, 0, 0);//go right
    else if (keyCode == SHIFT){
      ardrone.takeOff();//take off
      println("takeOff");
    }
    else if (keyCode == CONTROL) {
      ardrone.landing();//land
      println("landing");
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
    else if (key == 'e') {
      noLoop(); 
      exit(); //end proglam
    }
  }
}

public void keyReleased() {
  ardrone.stop();
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

public void exit() {
  //\u3053\u3053\u306b\u7d42\u4e86\u51e6\u7406
  ardrone.stop();
  ardrone.landing();
  println("exit");
  super.exit();
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
