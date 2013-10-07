import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

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

public class kinecdrone2 extends PApplet {





ARDroneForP5 ardrone;
SimpleOpenNI  kinect;

poseOperation pose;


public float[] velocity;
public float pitch;
public float roll;
/*
 *  added new method.
 *  void move3D(int speedX, int speedY, int speedZ, int speedSpin)
 *  @param speedX : the speed of x-axis, [-100,100]
 *  @param speedY : the speed of y-axis, [-100,100]
 *  @param speedZ : the speed of z-axis, [-100,100]
 *  @param speedSpin : the speed of rotation, [-100,100]
 */

public void setup() {
  size(640*2, 480);

  ardrone = new ARDroneForP5("192.168.1.1");
  //connect to the AR.Drone.
  ardrone.connect();
  //connect to the sensor info.
  ardrone.connectNav();
  //connect to the image info.
  ardrone.connectVideo();
  //start the connections.
  ardrone.start();
  kinect = new SimpleOpenNI(this);
  kinect.enableDepth();
  kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
  
  pose = new poseOperation(kinect,ardrone);
  strokeWeight(5);
}


public void draw() {
  background(204);  
  
  //get image from AR.Drone.
  PImage img = ardrone.getVideoImage(false);
  if (img == null){
//    return;
  }else{
  image(img, 640, 0,640*2,480);
  }
  //print all sensor information.
  //ardrone.printARDroneInfo();
  //get each sensor information.
  pitch = ardrone.getPitch();
  roll = ardrone.getRoll();
  float yaw = ardrone.getYaw();
  float altitude = ardrone.getAltitude();
  velocity = ardrone.getVelocity();
  int battery = ardrone.getBatteryPercentage();
  textSize(16);
  String attitude = "pitch:" + pitch + "\nroll:" + roll + "\nyaw:" + yaw + "\naltitude:" + altitude;
  text(attitude, 660, 300);
  String vel = "vx:" + velocity[0] + "\nvy:" + velocity[1];
  text(vel, 660, 440);
  String bat = "battery:" + battery + " %";
  text(bat, 1060, 470);
  textSize(50);  
  kinect.update();  
  image(kinect.depthImage(), 0, 0);

  IntVector userList = new IntVector();
  kinect.getUsers(userList);
if (userList.size() > 0) {
    int userId = userList.get(0);
    if ( kinect.isTrackingSkeleton(userId)) {
      pose.posePressed(userId);
      drawSkeleton(userId);
    }else{
       ardrone.landing(); 
    }
  }
  
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

//control the AR.Drone via keyboard input.
public void keyPressed() {
  if (key == CODED) {
    if (keyCode == UP) {
      ardrone.move3D(10, 0, 0, 0);//forward
    }
    else if (keyCode == DOWN) {
      ardrone.move3D(-10, 0, 0, 0);//backward
    }
    else if (keyCode == LEFT) {
      ardrone.move3D(0, 10, 0, 0);//go left
    }
    else if (keyCode == RIGHT) {
      ardrone.move3D(0, -10, 0, 0);//go right
    }
    else if (keyCode == SHIFT) {
      ardrone.takeOff();//take off
      println("push");
      text("takeOff", 700,100);
    }
    else if (keyCode == CONTROL) {
      ardrone.landing();//land
      text("landing", 700,100);
    }
  }
  else {
    if (key == 's') {
      ardrone.stop();//stop
    }
    else if (key == 'r') {
      ardrone.move3D(0, 0, 0, -20); //spin right(cw)
    }
    else if (key == 'l') {
      ardrone.move3D(0, 0, 0, 20);//spin left(ccw)
    }
    else if (key == 'u') {
      ardrone.move3D(0, 0, -10, 0);//up
    }
    else if (key == 'd') {
      ardrone.move3D(0, 0, 10, 0);//down
    }
    else if (key == 'z') {
      ardrone.move3D(10, 10, 0, 0);//diagonally forward left
    }
    else if (key == 'x') {
      ardrone.move3D(-10, -10, 0, 0);//diagonally backward right
    }
    else if (key == 'c') {
      ardrone.move3D(10, 10, -10, 0);//diagonally forward left up
    }
    else if (key == 'v') {
      ardrone.move3D(-10, -10, 10, 0);//diagonally backward right down
    }
    else if (key == 'b') {
      ardrone.move3D(10, 0, 0, -20);//turn right
    }
    else if (key == 'n') {
      ardrone.move3D(10, 0, 0, 20);//turn left
    }
  }
}
public void keyReleased() {
  ardrone.stop();
}


public void  attitudeControl(float xVelocity , float yVelocity){
  ardrone.move3D(-(int)(xVelocity*10)/10,-(int)(yVelocity*10)/10,0,0);
  println((int)(xVelocity*10));
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

public void exit() {
  //\u3053\u3053\u306b\u7d42\u4e86\u51e6\u7406
  ardrone.stop();
  ardrone.landing();
  super.exit();
}

public  int count;
;
class poseOperation{
  SimpleOpenNI context;
  ARDroneForP5 ardrone;
  
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

  poseOperation(SimpleOpenNI context, ARDroneForP5 ardrone){
    this.context = context;
    this.ardrone = ardrone;
    count = 0;
    flag = 0;
    textSize(50);
  }
  
  public void posePressed(int userId){

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
    
    playerYaw = (rightHand.y + leftHand.y)/2.0f - head.y;

    // println("playerRoll: " + playerRoll);
    // println("playerYaw: " + playerYaw);

    playerRoll = playerRoll/move_speed;
    playerYaw = playerYaw/move_speed;

    println("playerRoll: " + playerRoll);
    println("playerYaw: " + playerYaw);

    if(abs(playerRoll) < 5){
      playerRoll = 0;
    }else{
      if(playerRoll>0){
        text("left", 700,200);
        if(playerRoll > 30){
          playerRoll = 30;
        }
      }else if(playerRoll<0){
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
        text("forward", 700,100);
        if(playerYaw>30){
          playerYaw = 30;
        }
      }else if(playerYaw<0){
        text("back", 700,100);
        if(playerYaw<-30){
          playerYaw = -30;
        }
      }
    }

    if(playerYaw != 0 || playerRoll != 0){
      stroke(0,255,255);
      ardrone.move3D((int)playerYaw,(int)playerRoll, 0, 0);
    }else{
      stroke(255,0,0);
      ardrone.stop();
    } 
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "kinecdrone2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
