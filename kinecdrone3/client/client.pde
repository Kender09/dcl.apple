import SimpleOpenNI.*;
import processing.net.*;

SimpleOpenNI  kinect;

poseOperation pose;
ArDroneOrder con;

Client client;

public float[] velocity;
public float pitch;
public float roll;


void setup() {
  size(640, 480);

  kinect = new SimpleOpenNI(this);
  kinect.enableDepth();
  kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
  
  pose = new poseOperation(kinect);

  con = new ArDroneOrder();
  con.yaw = 0;
  con.roll = 0;
  strokeWeight(5);
}


void draw() {
  background(204);  
  
  // float yaw = ardrone.getYaw();
  // float altitude = ardrone.getAltitude();
  // velocity = ardrone.getVelocity();
  // int battery = ardrone.getBatteryPercentage();
  // textSize(16);
  // String attitude = "pitch:" + pitch + "\nroll:" + roll + "\nyaw:" + yaw + "\naltitude:" + altitude;
  // text(attitude, 660, 300);
  // String vel = "vx:" + velocity[0] + "\nvy:" + velocity[1];
  // text(vel, 660, 440);
  // String bat = "battery:" + battery + " %";
  // text(bat, 1060, 470);
  // textSize(50); 

  kinect.update();  
  image(kinect.depthImage(), 0, 0);

  IntVector userList = new IntVector();
  kinect.getUsers(userList);

if (userList.size() > 0) {
    int userId = userList.get(0);
    if ( kinect.isTrackingSkeleton(userId)) {
      con = pose.posePressed(userId);
      drawSkeleton(userId);
    }else{
      con.yaw = 0;
      con.roll = 0;
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

void keyPressed() {
  if (key == CODED) {
    if (keyCode == UP) {

    }
    else if (keyCode == DOWN) {
    }
    else if (keyCode == LEFT) {
    }
    else if (keyCode == RIGHT) {
    }
    else if (keyCode == SHIFT) {
    }
    else if (keyCode == CONTROL) {
    }
  }
  else {
    if (key == 's') {
    }
    else if (key == 'r') {
    }
    else if (key == 'l') {
    }
    else if (key == 'u') {
    }
    else if (key == 'd') {
    }
    else if (key == 'z') {
    }
    else if (key == 'x') {
    }
    else if (key == 'c') {
    }
    else if (key == 'v') {
    }
    else if (key == 'b') {
    }
    else if (key == 'n') {
    }
    else if (key == 'e') {
      noLoop(); 
      exit(); //end proglam
    }
  }
}
void keyReleased() {

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

void exit() {
  //ここに終了処理
  // ardrone.stop();
  // ardrone.landing();
  println("exit");
  super.exit();
}

