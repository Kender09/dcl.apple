public  int count;

class ArDroneOrder{
  int yaw;
  int roll;
  int spin;
}

class PoseOperation{
  SimpleOpenNI context;
  
  PVector rightHand = new PVector();
  PVector rightElbow = new PVector();
  PVector rightShoulder = new PVector();
  PVector leftHand = new PVector();
  PVector leftElbow = new PVector();
  PVector leftShoulder = new PVector();
  
  PVector head = new PVector();
  PVector neck = new PVector();
  PVector torso = new PVector();
  
  // PVector rightFoot = new PVector();
  // PVector rightKnee = new PVector();
  // PVector rightHip = new PVector();
  // PVector leftFoot = new PVector();
  // PVector leftKnee = new PVector();
  // PVector leftHip = new PVector();
  
  float baseScale;
  float hhhead;
  int flag;
  int move_speed = 50;

  final int DelayTime = 10;
  
  float playerRoll;
  float playerYaw;
  float playerSpin;

  float[] ave_playerRoll = new float[5];
  float[] ave_playerYaw = new float[5];
  float[] ave_playerSpin = new float[5];

  int ave_count;

  PoseOperation(SimpleOpenNI context){
    this.context = context;
    count = 0;
    flag = 0;
    ave_count = 0;
    for(int i = 0; i<5; i++){
      ave_playerRoll[i] = 0;
      ave_playerYaw[i] = 0;
      ave_playerSpin[i] = 0;
    }
  }

  ArDroneOrder posePressed(int userId){

    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, rightHand);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, rightElbow);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, rightShoulder);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, leftHand);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, leftElbow);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, leftShoulder);     

    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_HEAD, head);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_NECK, neck);
    context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_TORSO, torso);

    // context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_FOOT, rightFoot);
    // context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, rightKnee);
    // context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HIP, rightHip);
    // context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_FOOT, leftFoot);
    // context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_KNEE, leftKnee);
    // context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HIP, leftHip);  

    baseScale = head.y - torso.y;
    
    hhhead = (head.y + neck.y)/2.0;

    playerRoll = leftHand.z - rightHand.z;

    playerYaw = (rightHand.y + leftHand.y)/2.0;

    playerSpin = (rightHand.y - leftHand.y);
    
    if(playerYaw > hhhead){
      playerYaw = playerYaw - hhhead;
    }else if(playerYaw < (torso.y + baseScale/3.0) ){
      playerYaw = playerYaw - (torso.y + baseScale/3.0);
    }else{
      playerYaw = 0;
    }
    
    ArDroneOrder poseCon = new ArDroneOrder();

    // println("playerRoll: " + playerRoll);
    // println("playerYaw: " + playerYaw);

    playerYaw = playerYaw/(move_speed-20);
    playerRoll = playerRoll/(move_speed-5);
    playerSpin = playerSpin/(move_speed+10);

    // println("playerRoll: " + playerRoll);
    // println("playerSpin: " + playerSpin);
    ave_playerRoll[ave_count] = playerRoll;
    ave_playerYaw[ave_count] = playerYaw;
    ave_playerSpin[ave_count] = playerSpin;
    ave_count = (ave_count+1)%5;

    float ave = 0;
    float subave = 0;
    int c;
    for(c = 0; c < 5; c++){
      ave = ave + ave_playerRoll[c];
    }
    ave = ave/5.0;
    subave = ave - playerRoll;
    if(abs(subave) > 5){
      playerRoll = ave;
    }
    ave = 0;

    for(c = 0; c < 5; c++){
      ave = ave + ave_playerYaw[c];
    }
    ave = ave/5.0;
    subave = ave - playerYaw;
    if(abs(subave) > 5){
      playerYaw = ave;
    }
    ave = 0;

    for(c = 0; c < 5; c++){
      ave = ave + ave_playerSpin[c];
    }
    ave = ave/5.0;
    subave = ave - playerSpin;
    if(abs(subave) > 5){
      playerSpin = ave;
    }
    ave = 0;



    if(abs(playerRoll) < 5){
      playerRoll = 0;
    }else{
      if(playerRoll>0){
        // text("left", 100,200);
        // text("left", 700,200);
        if(playerRoll > 30){
          playerRoll = 30;
        }
      }else if(playerRoll<0){
        // text("right", 100,200);
        // text("right", 700,200);
        if(playerRoll < -30){
          playerRoll = -30;
        }
      }
    }

    if(abs(playerYaw) <5){
      playerYaw = 0;
    }else{
      if(playerYaw>0){
        // text("forward", 100,100);
        // text("forward", 700,100);
        if(playerYaw>30){
          playerYaw = 30;
        }
      }else if(playerYaw<0){
        // text("back", 100,100);
        // text("back", 700,100);
        if(playerYaw<-30){
          playerYaw = -30;
        }
      }
    }

    if(abs(playerSpin) <10){
      playerSpin = 0;
    }else{
      if(playerSpin>0){
        // text("spinL", 100,300);
        // text("spinL", 700,300);
        if(playerSpin>30){
          playerSpin = 30;
        }
      }else if(playerSpin<0){
        // text("spinR", 100,300);
        // text("spinR", 700,300);
        if(playerSpin<-30){
          playerSpin = -30;
        }
      }
    }

    if(playerSpin != 0){
      playerYaw = 0;
      playerRoll = 0;
    }


    poseCon.yaw = (int)playerYaw;
    poseCon.roll = (int)playerRoll;
    poseCon.spin = (int)playerSpin;

    // if(playerYaw != 0 || playerRoll != 0 || playerSpin != 0){
    //   stroke(0,255,255);
    // }else{
    //   stroke(255,255,255);
    // } 
    return poseCon;
  }
}
