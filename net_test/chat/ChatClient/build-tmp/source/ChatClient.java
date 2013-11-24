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

public class ChatClient extends PApplet {


Client chatClient;
float Val;
String msg,smsg;
String id;

public void setup() {
  size(200, 200);
  //\u30b5\u30fc\u30d0\u30fc\u306b\u63a5\u7d9a\u8981\u6c42
  chatClient = new Client(
     this, "127.0.0.1", 2001);
  background(0);
  msg="";
  id="taro>";
}

public void draw(){
  if(chatClient.available()>0){
    smsg=chatClient.readStringUntil('\n');
    println(smsg);
  }
}

public void keyPressed(){
  int dmy;
  msg = msg + key;
  if(key =='\n') {
    chatClient.write(id+msg);//\u30b5\u30fc\u30d0\u30fc\u306b\u6570\u5b57\u3092\u9001\u308b
    msg="";
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ChatClient" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
