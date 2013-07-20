
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
  
  poseOperation(SimpleOpenNI context, ARDroneForP5 ardrone){
    this.context = context;
    this.ardrone = ardrone;
  }
  
  void posePressed(int userId){

      println("in");
      
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
      
     if(rightElbow.y > rightShoulder.y && rightElbow.x > rightShoulder.x){
        println("pose");
        ardrone.takeOff();
      } else {
        ardrone.landing();
      }
  }
}
