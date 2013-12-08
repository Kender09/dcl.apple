import processing.net.*;

import SimpleOpenNI.*;

import processing.video.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;


import java.*;

import com.shigeodayo.ardrone.processing.*;

ARDroneForP5 ardrone;

// import fullscreen.*; 
// FullScreen fs;

SimpleOpenNI  kinect;

PoseOperation pose;

ArDroneOrder con;

String msg;

//oculur riftように画像変換
PShader barrel;
PGraphics fb;
PGraphics scene;
int eye_width = 640;
int eye_height = 800;

int drawKinectFlag = 1;

void setup() {
  size(640*2, 800, P3D);

  // fs = new FullScreen(this); 
  // fs.enter();

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


void draw() {
  background(0);


  //ARカメラ映像の取得
  PImage img = ardrone.getVideoImage(false);

  //kinect プログラム
  textSize(50);  
  kinect.update();  

  IntVector userList = new IntVector();
  kinect.getUsers(userList);
  if (userList.size() > 0) {
    int userId = userList.get(0);
    if( kinect.isTrackingSkeleton(userId) ){
      drawKinectFlag = 0;
      con = pose.posePressed(userId);
      msg = con.yaw + ":" + con.roll +  ":" + con.spin + "\n";
      println(msg);
      ardrone.move3D(con.yaw, con.roll, 0, con.spin);
    }else{
      drawKinectFlag = 1;
      con.yaw = 0;
      con.roll = 0;
      msg = con.yaw + ":" + con.roll + ":" + con.spin + "\n";
      ardrone.landing();
    }
  }

  //oculusriftように映像を合成
  scene.beginDraw();
  scene.background(0);
  if(img != null)
    scene.image(img, 0, 0, 640, 800);

  if(drawKinectFlag == 1){
    scene.image(kinect.depthImage(), 320-((640*0.8)/2), 400-((480*0.8)/2), 640*0.8,480*0.8);
  }else if(drawKinectFlag == 0){
    // scene.image(kinect.depthImage(), 0, 800-(480*0.8), 640*0.8,480*0.8);
    scene.textSize(30);
    scene.fill(250, 0, 0);
    scene.text(msg,150, 500);
  }
  scene.translate(scene.width/2, scene.height/2, 100);
  scene.endDraw();

  //加工した画像を描画
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


void keyPressed() {
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

void set_shader(String eye)
{
  float x = 0.0;
  float y = 0.0;
  float w = 0.5;
  float h = 1.0;
  float DistortionXCenterOffset = 0.25;
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

void exit() {
  //ここに終了処理
  ardrone.stop();
  ardrone.landing();
  println("exit");
  super.exit();
}
