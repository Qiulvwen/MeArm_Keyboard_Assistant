/*
MeArm机械臂控制程序-1
by 太极创客 (2017-06-02)
WWW.TAICHI-MAKER.COM

本程序用于太极创客《零基础入门学用Arduino教程 - MeArm篇》。
1   使用串口获得电机数据,设置机械臂动作
    将串口指令解读改写为函数,加入状态汇报函数reportStatus
 
如需要获得具体电路连接细节，请查阅太极创客制作的
《零基础入门学用Arduino教程 - MeArm篇》页面。
*/
#include <Servo.h>                //使用servo库
Servo base, fArm, rArm, claw ;    //创建4个servo对象
  
// 建立4个int型变量存储当前电机角度值
// 初始角度值为设备启动后初始状态所需要的电机角度数值
int basePos = 90;
int rArmPos = 90;
int fArmPos = 90;
int clawPos = 90;
 
//存储电机极限值(const指定该数值为常量,常量数值在程序运行中不能改变)
const int baseMin = 10;
const int baseMax = 180;
const int rArmMin = 45;
const int rArmMax = 180;
const int fArmMin = 35;
const int fArmMax = 120;
const int clawMin = 25;
const int clawMax = 100;
 
void setup(){
  base.attach(11);     // base 伺服舵机连接引脚11 舵机代号'b'
  delay(200);          // 稳定性等待
  rArm.attach(10);     // rArm 伺服舵机连接引脚10 舵机代号'r'
  delay(200);          // 稳定性等待
  fArm.attach(9);      // fArm 伺服舵机连接引脚9  舵机代号'f'
  delay(200);          // 稳定性等待
  claw.attach(6);      // claw 伺服舵机连接引脚6  舵机代号'c'
  delay(200);          // 稳定性等待
  
  Serial.begin(9600); 
  Serial.println("Welcome to Taichi-Maker Robot Arm Tutorial.");   
}
 
void loop(){
  if (Serial.available()>0) {      //使用串口监视器输入电机指令控制机械臂电机
    char serialCmd = Serial.read();//指令举例: b45，将底盘舵机调整到45度位置
    armDataCmd(serialCmd);
  }
 
  base.write(basePos); 
  delay(10);
  fArm.write(fArmPos); 
  delay(10);
  rArm.write(rArmPos); 
  delay(10);
  claw.write(clawPos);  
  delay(10);   
}
 
void armDataCmd(char serialCmd){
   
  Serial.print("serialCmd = ");
  Serial.print(serialCmd);  
  
  int servoData = Serial.parseInt(); 
  switch(serialCmd){
    case 'b':  
      basePos = servoData;
      Serial.print("  Set base servo value: ");
      Serial.println(servoData);
      break;
    case 'c':  
      clawPos = servoData;
      Serial.print("  Set claw servo value: ");
      Serial.println(servoData);
      break;
    case 'f':  
      fArmPos = servoData;
      Serial.print("  Set fArm servo value: ");
      Serial.println(servoData);
      break;
    case 'r':  
      rArmPos = servoData;
      Serial.print("  Set rArm servo value: ");
      Serial.println(servoData);
      break;
    case 'o':  
      reportStatus();
      break;
    default:
      Serial.println(" Unknown Command.");
  }  
}
 
void reportStatus(){
  Serial.println("");
  Serial.println("");
  Serial.println("++++++ Robot-Arm Status Report +++++");
  Serial.print("Claw Position: clawPos = "); Serial.println(claw.read());
  Serial.print("Base Position: basePos = "); Serial.println(base.read());
  Serial.print("Rear  Arm Position: rArmPos = "); Serial.println(rArm.read());
  Serial.print("Front Arm Position: fArmPos = "); Serial.println(fArm.read());
  Serial.println("++++++++++++++++++++++++++++++++++++");
  Serial.println("");
}
