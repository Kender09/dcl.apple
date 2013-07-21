public  int count;
;
class poseOperation{
  SimpleOpenNI context;
  ARDroneForP5 ardrone;
  
  PVector rightHand = new PVector();
  PVector rightElbow = new PVector();
  PVector rightShoulder = new PVector();
  PVector leftHand = new PVector();
  PVector leftElbow = new PVector();
  PVector leftShoulder = new PVector();
  
  PVector head = new PVector();
  PVector neck = new PVector();
  PVector torso = new PVector();
  
  PVector rightFoot = new PVector();
  PVector rightKnee = new PVector();
  PVector rightHip = new PVector();
  PVector leftFoot = new PVector();
  PVector leftKnee = new PVector();
  PVector leftHip = new PVector();
  
  float baseScale;
  int flag;
  int move_speed = 5;
  
  final int DelayTime = 20;

  poseOperation(SimpleOpenNI context, ARDroneForP5 ardrone){
    this.context = context;
    this.ardrone = ardrone;
    count = 0;
    flag = 0;
    textSize(50);
  }
  
  void posePressed(int userId){

    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, rightHand);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, rightElbow);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, rightShoulder);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, leftHand);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, leftElbow);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, leftShoulder);     

    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_HEAD, head);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_NECK, neck);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_TORSO, torso);

    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_FOOT, rightFoot);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, rightKnee);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HIP, rightHip);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_FOOT, leftFoot);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_KNEE, leftKnee);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HIP, leftHip);  

    baseScale = head.y - torso.y;

//      println(baseScale);
    // print(flag);
    println(count);

    if(rightHand.y < torso.y && leftHand.y < torso.y && rightHand.z > torso.z + baseScale/2 && leftHand.z > torso.z + baseScale/2){     
     if(flag != 1){
       count=0;
       flag = 1;
     }
     count=0;
     ardrone.landing();
     text("landing", 700, 100);
     stroke(255, 255, 255);
   }else if(rightHand.y < torso.y && leftHand.y < torso.y && rightHand.z < torso.z - baseScale/2 && leftHand.z < torso.z - baseScale/2){
    text("takeoff", 700,100);
    if(flag != 2){
      count=0;
      flag = 2;
    }
    count++;
    if(count!=DelayTime) return;
    else count=0;

    ardrone.takeOff();
    stroke(0,0,0);
  }else if(rightHand.y > head.y && leftHand.y > head.y){
        //範囲1,2
    if(rightHand.z < neck.z - baseScale/2 && leftHand.z < neck.z - baseScale/2){
           //上
    text("up", 700,100);
     if(flag != 3){
      count=0;
      flag = 3;
    }
    count++;
    if(count!=DelayTime) return;
    else count=0;

    stroke(153, 0, 255);
    ardrone.move3D(0, 0, -move_speed, 0);
  }else if(neck.z + baseScale/2 < rightHand.z  && neck.z + baseScale/2 < leftHand.z){
            //下
    text("down", 700,100);
    if(flag != 4){
      count=0;
      flag = 4;
    }
    count++;
    if(count!=DelayTime) return;
    else count=0;

    stroke(204,102,0);
    ardrone.move3D(0, 0, move_speed, 0);
  }
}else if(rightHand.x > head.x && rightHand.y > head.y && leftHand.x < head.x && leftHand.y < head.y && leftHand.y > torso.y){
        //左
 text("left", 700,100);
  if(flag != 5){
   count=0;
   flag = 5;
 }
 count++;
 if(count!=DelayTime) return;
 else count=0;

 stroke(255,255,0);
 ardrone.move3D(0, move_speed, 0, 0);
}else if(rightHand.x > head.x && leftHand.y > head.y && leftHand.x < head.x && rightHand.y < head.y && rightHand.y > torso.y){
        //右
 text("right", 700,100);
  if(flag != 6){
   count=0;
   flag = 6;
 }
 count++;
 if(count!=DelayTime) return;
 else count=0;

 stroke(0,255,0);
 ardrone.move3D(0, -move_speed, 0, 0);
}else if(rightHand.y > head.y && leftHand.y < torso.y){
        //前
 text("forward", 700,100);
  if(flag != 7){
   count=0;
   flag = 7;
 }
 count++;
 if(count!=DelayTime) return;
 else count=0;

 stroke(0,0,255);
 ardrone.move3D(move_speed, 0, 0, 0);
}else if(rightHand.y < torso.y && leftHand.y > head.y){
    //後
 text("back", 700,100);
  if(flag != 8){
   count=0;
   flag = 8;
 }
 count++;
 if(count!=DelayTime) return;
 else count=0;

 stroke(255,102,255);
 ardrone.move3D(-move_speed, 0, 0, 0);
}else{
  flag = 0;
  count = 0;
  stroke(255,0,0);
  attitudeControl(pitch,roll);
  ardrone.stop();
  text("stop", 700,100);
  ;
}
}

}
