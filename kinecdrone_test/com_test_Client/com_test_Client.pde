import processing.net.*;

Client chatClient;
void setup() {
  size(640, 480);

  chatClient = new Client(this, "192.168.10.38", 2001);
}


void draw() {
  background(204);  
}