import java.awt.*; //これが要る

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

void setup() {
  size(360,140);
  
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


