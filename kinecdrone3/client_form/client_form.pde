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
String ip_addr;
int flag1 = 0, flag2 = 0;
String msg;
byte[] sendBytes;
//受信するバイト配列を格納する箱
byte[] receivedBytes = new byte[300000];
//oculur riftように画像変換
PShader barrel;
PGraphics fb;
PGraphics scene;
int eye_width = 640;
int eye_height = 800;
int drawKinectFlag = 1;

// **********  
// input form用

TextField inputLine = new TextField("Input IP Address"); 
int rectX, rectY;      // Position of square button
int circleX, circleY;  // Position of circle button
int rectSize = 90;     // Diameter of rect
int circleSize = 93;   // Diameter of circle
color rectColor, circleColor, baseColor;
color rectHighlight, circleHighlight;
color currentColor;
boolean rectOver = false;
boolean circleOver = false;
int got_address_flag = 0;
String input_address = "";

// **********





void setup() {
  size(640*2, 800, P3D);

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

  frameRate(10);

// **********
  rectColor = color(250);
  rectHighlight = color(51);
  baseColor = color(102);
  currentColor = baseColor;
  rectX = width/2+rectSize-25;
  rectY = height/2;//-rectSize/2;
  ellipseMode(CENTER);  
  
  setLayout(null);
  inputLine.setBounds(30,37,125,25);
  add(inputLine);

// **********

}


void draw() {

  if(got_address_flag == 0){
    update(mouseX, mouseY);
    background(currentColor);
    
    if (rectOver) {
      fill(rectHighlight);
    } else {
      fill(rectColor);
    }
    stroke(255);
    rect(rectX, rectY, rectSize, rectSize/2);

  //  ellipse(circleX, circleY, circleSize, circleSize);
  }
  else{
    //　接続してそこからの処理
    background(0);

    if(flag1 == 0){

      return;
    }else if(flag1 == 1){
      ip_addr = "localhost";
    }

    if(flag2 == 0){
      cl =  new Client(this, ip_addr, 2001);

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
      flag2 = 1;
    }

    //ARカメラ映像の取得
    try {
      receiveSocket.receive(receivePacket);
    }
    catch(IOException e) {
    } 
    Image awtImage = Toolkit.getDefaultToolkit().createImage(receivedBytes);
    PImage receiveImage = loadImageMT(awtImage);

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
        cl.write(msg);
      }else{
        drawKinectFlag = 1;
        con.yaw = 0;
        con.roll = 0;
        msg = con.yaw + ":" + con.roll + ":" + con.spin + "\n";
      }
    }

    //oculusriftように映像を合成
    scene.beginDraw();
    scene.background(0);
    scene.image(receiveImage, 0, 0, 640, 800);
    if(drawKinectFlag == 1){
      scene.image(kinect.depthImage(), 320-((640*0.8)/2), 400-((480*0.8)/2), 640*0.8,480*0.8);
    }else if(drawKinectFlag == 0){
      // scene.image(kinect.depthImage(), 0, 800-(480*0.8), 640*0.8,480*0.8);
      scene.textSize(30);
      scene.fill(250, 0, 0);
      scene.text(msg,250, 500);
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

void keyPressed() {
   if (key == 'e') {
      flag1 = 1;
    }
}

void mousePressed(){
  if (rectOver) {
    String iLine = inputLine.getText();
    input_address = iLine;
    println("inputLine: " + iLine);  
    got_address_flag = 1;
    //currentColor = rectColor;
  }  
}

void update(int x, int y) {
  if(overRect(rectX, rectY, rectSize, rectSize/2)){
    rectOver = true;
    circleOver = false;
  }else{
    circleOver = rectOver = false;
  }
}

boolean overRect(int x, int y, int width, int height)  {
  if (mouseX >= x && mouseX <= x+width && 
      mouseY >= y && mouseY <= y+height) {
    return true;
  } else {
    return false;
  }
}
