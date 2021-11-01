/*
MeArm舵机安装试运行程序 v1.0 (MeArm_Servo_Chk.ino)
by 太极创客 (2017-06-02)
http://www.taichi-maker.com

本程序用于组装MeArm过程中，当安装完每一个电机后，对电机
的运行情况进行测试确认。获得具体舵机测试方法以及电路连接
细节，请见太极创客制作的《零基础入门学用Arduino教程 - 
MeArm篇》相关页面。


太极创客团队为MeArm机械臂安装和使用制作了视频教程和装配说明书。
您可通过太极创客网站免费下载/收看该教程所有内容。

=================================
  舵机控制线与Arduino连接说明
 -----------------------------
 Arduino Uno R3      MeArm舵机
  引脚编号          PWM控制线

    6   -------  钳子舵机(c)law                
    9   -------  前臂舵机(f)Arm
    10  -------  后臂舵机(r)Arm
    11  -------  底盘舵机(b)ase  
=================================    

《零基础入门学用Arduino教程》面向对象是完全没有任何Arduino和编程基础的“小白”，
这套教程通过视频教学，实验演示以及示例程序讲解，为您讲述Arduino的硬件和程序开发
基础知识。

MeArm官方网站网址：MeArm.io
This example code is in the public domain.
*/

#include <Servo.h> 
 
Servo base, rArm, fArm, claw ;  //建立4个舵机对象
 
void setup() 
{ 
  base.attach(11);     // base 伺服舵机连接引脚11 舵机代号'b'
  rArm.attach(10);     // rArm 伺服舵机连接引脚10 舵机代号'r'
  fArm.attach(9);      // fArm 伺服舵机连接引脚9  舵机代号'f'
  claw.attach(6);      // claw 伺服舵机连接引脚6  舵机代号'c'
} 
 
void loop() { //各电机开始测试运行
  for (int pos = 0; pos <= 180; pos += 1) { 
    base.write(pos);             
    delay(10);   
    rArm.write(pos);             
    delay(10);  
    fArm.write(pos);             
    delay(10);
    claw.write(pos/2);             
    delay(10);              
  }
  for (int pos = 180; pos >= 0; pos -= 1) { 
    base.write(pos);             
    delay(10);   
    rArm.write(pos);             
    delay(10);  
    fArm.write(pos);             
    delay(10);   
    claw.write(pos/2);             
    delay(10);       
  }  
} 
