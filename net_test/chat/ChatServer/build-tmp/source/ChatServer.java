import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ChatServer extends PApplet {


Server chatServer;
Client cl;

public void setup() {
size(200,200);
//\u30b5\u30fc\u30d0\u3092\u8a2d\u5b9aport\u756a\u53f7\u306f20001\u3068\u3059\u308b
chatServer = new Server(this,2001);
}

public void draw() {

String msg;
  cl =chatServer.available();
  if(cl !=null) println("connected");
  //\u30af\u30e9\u30a4\u30a2\u30f3\u30c8\u304cnull\u3067\u306a\u3044\u306a\u3089\u30eb\u30fc\u30d7\u3078
  if((cl != null)&&(cl.available()>0)) {
    msg=cl.readStringUntil('\n');
    println(msg);
    chatServer.write(msg);//\u5168\u54e1\u306b\u9001\u4fe1
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ChatServer" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
