import SimpleOpenNI.*;

import com.shigeodayo.ardrone.processing.*;

ARDroneForP5 ardrone;
SimpleOpenNI  kinect;

poseOperation pose;

public float[] velocity;

/*
 *  added new method.
 *  void move3D(int speedX, int speedY, int speedZ, int speedSpin)
 *  @param speedX : the speed of x-axis, [-100,100]
 *  @param speedY : the speed of y-axis, [-100,100]
 *  @param speedZ : the speed of z-axis, [-100,100]
 *  @param speedSpin : the speed of rotation, [-100,100]
 */

void setup() {
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


void draw() {
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
  float pitch = ardrone.getPitch();
  float roll = ardrone.getRoll();
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

//control the AR.Drone via keyboard input.
void keyPressed() {
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
    }
    else if (keyCode == CONTROL) {
      ardrone.landing();//land
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
void keyReleased() {
  ardrone.stop();
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

