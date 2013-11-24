import processing.net.*;
Server chatServer;
Client cl;

void setup() {
size(200,200);
//サーバを設定port番号は20001とする
chatServer = new Server(this,2001);
}

void draw() {

String msg;
  cl =chatServer.available();
  if(cl !=null) println("connected");
  //クライアントがnullでないならループへ
  if((cl != null)&&(cl.available()>0)) {
    msg=cl.readStringUntil('\n');
    println(msg);
    chatServer.write(msg);//全員に送信
  }
}