/*
源码地址及坐标体系：https://github.com/yorkhackspace/meArm/blob/master/Geometry.md

void begin(int pinBase, int pinShoulder, int pinElbow, int pinGripper) - The four PWM-capable pins used to drive the servos. Begin must be called in setup() before any other calls to the meArm instance are made.
void openGripper() - opens the gripper, letting go of anything it was holding
void closeGripper() - closes the gripper, perhaps grabbing and holding something as it does so
void gotoPoint(float x, float y, float z) - move in a straight line from the current point to the requested position
void goDirectlyTo(float x, float y, float z) - set the servo angles to immediately go to the requested point without caring what path the arm swings through to get there - faster but less predictable than gotoPoint
bool isReachable() - returns true if the point can theoretically be reached by the arm
float getX() - current x coordinate,  i.e. 100mm
float getY() - current y coordinate, i.e. 100mm
float getZ() - current z coordinate, i.e. 100mm
void end() - Disable all servo motors. If you want to continue using the meArm again later, call begin() again.

The algorithm needs a point of reference (the origin) to refer to things with, which I chose to place directly above the axis the arm rotates around at the base - as this never moves. 
Specifically, the point is directly above the base servo, at the same height as the shoulder servo.
 */
#include "meArm.h"
#include <Servo.h>

meArm arm;

void setup() {
  arm.begin(11, 10, 9, 6);//begin(int pinBase, int pinShoulder, int pinElbow, int pinGripper)
  //arm.gotoPoint(0,0,0); 
  Serial.begin(9600); 
  Serial.println("Contral command format:{x150y150z0cc} - Move to x=150mm,y=150mm,z=0mm"); 
}

int targetX=0;
int targetY=0;
int targetZ=0;

void loop() {
  if (Serial.available()>0) 
  {  
    char serialCmd = Serial.read();
    if(serialCmd == '{')
    {
      do 
      {
        serialCmd = Serial.read();
        if( serialCmd == 'x' or serialCmd == 'y' or serialCmd == 'z')
        {
          int targetCoordinate = Serial.parseInt();
          switch(serialCmd)
          {
            case 'x':
              targetX=targetCoordinate;
              break;
             case 'y':
              targetY=targetCoordinate;
              break;
             case 'z':
              targetZ=targetCoordinate;
              break;
          }
        }
        else if(serialCmd == 'c')
        {
          char openOrClose = Serial.read();
          if(openOrClose == 'c')
          {
            arm.closeGripper();
            Serial.println("Close Claw!");
          } 
          else if(openOrClose == 'o')
          {
            arm.openGripper();
            Serial.println("Open Claw!");
          }
        }
      }while(serialCmd!='}');
      if(arm.isReachable(targetX,targetY,targetZ))
      {
        arm.gotoPoint(targetX,targetY,targetZ);   
        Serial.print("Move to x:");Serial.print(targetX);Serial.print(" y:");Serial.print(targetY);Serial.print(" z:");Serial.println(targetZ);
      }
      else
      {
        Serial.print("UnReachable!");
      }
      
      delay(3000);
      reportStatus();
    }
  }
}

void reportStatus(){  //机器臂当前位置信息
  Serial.println("");
  Serial.println("");
  Serial.println("+ Robot-Arm Status Report +");
  Serial.print("X: "); Serial.println(arm.getX());
  Serial.print("Y: "); Serial.println(arm.getY());
  Serial.print("Z:"); Serial.println(arm.getZ());
  Serial.println("++++++++++++++++++++++++++");
  Serial.println("");
}
