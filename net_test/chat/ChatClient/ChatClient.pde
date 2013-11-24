import processing.net.*;
Client chatClient;
float Val;
String msg,smsg;
String id;

void setup() {
  size(200, 200);
  //サーバーに接続要求
  chatClient = new Client(
     this, "127.0.0.1", 2001);
  background(0);
  msg="";
  id="taro>";
}

void draw(){
  if(chatClient.available()>0){
    smsg=chatClient.readStringUntil('\n');
    println(smsg);
  }
}

void keyPressed(){
  int dmy;
  msg = msg + key;
  if(key =='\n') {
    chatClient.write(id+msg);//サーバーに数字を送る
    msg="";
  }
}
