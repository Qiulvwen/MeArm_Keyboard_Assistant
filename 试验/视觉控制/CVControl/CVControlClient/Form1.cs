using OpenCvSharp;
using OpenCvSharp.Extensions;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO.Ports;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace CVControlClient
{
    public partial class Form1 : Form
    {
        SerialPort port;
        VideoCapture capture;
        public Form1()
        {
            InitializeComponent();
            capture = new VideoCapture();

            /*
            if (port == null)
            {
                //打开串口准备跟 Arduino 通信
                port = new SerialPort(tbCOM.Text, int.Parse(tbBaudRate.Text));
                port.Open();
            }
            */
        }


        private void Form1_Load(object sender, EventArgs e)
        {
            capture.Open(1, VideoCaptureAPIs.ANY);
            if (!capture.IsOpened())
            {
                Close();
                return;
            }
            backgroundWorker1.RunWorkerAsync();
        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            backgroundWorker1.CancelAsync();
            capture.Dispose();
        }

        private void Form1_FormClosed(object sender, FormClosedEventArgs e)
        {
            if (port != null && port.IsOpen)
            {
                port.Close();
                //port.WriteLine(message);
            }
        }
        private void btGo_Click(object sender, EventArgs e)
        {
            tbResult.Focus();
            MoveArmToImageXY(int.Parse(tbBasePivotX.Text), int.Parse(tbBasePivotY.Text), 381, 768);
        }

        /// <summary>
        /// 将机械臂移动到图形对于的像素点位置
        /// </summary>
        /// <param name="baseX">机器臂 Base 电机的中心点在图像上的 X 像素位置</param>
        /// <param name="baseY">机器臂 Base 电机的中心点在图像上的 Y 像素位置</param>
        /// <param name="targetX">目标像素点位置 X</param>
        /// <param name="targetY">目标像素点位置 Y</param>
        private void MoveArmToImageXY(int baseX, int baseY, int targetX, int targetY)
        {
            /*图像像素坐标到机械臂坐标的转换（二维）
             * 让图像坐标平移让两个坐标系的原点重合，Xc 与 Xa 重合：x2'=x2-x1, y2'=y2-y1
             * 然后新的像素坐标 Yc 翻转与 Ya 重合： x2''=x2'=x2-x1, y2''=-y2'=y1-y2
             * 就可以获得 x2,y2 在机械臂坐标系中的位置
             * ---------------->Xc
             * |     Ya
             * |     ↑
             * |     |     .x2,y2
             * |     |
             * | x1,y1---------------->Xa
             * ↓
             * Yc
             */
            int targetXa = targetX - baseX;
            int targetYa = baseY - targetY;

            int dpiX = 72, dpiY = 72; //相机分辨率
            int targetXa1 = (int)(targetXa / dpiX * 2.54 * 10);// 单位 mm
            int targetYa1 = (int)(targetYa / dpiY * 2.54 * 10);// 单位 mm


            string msg = string.Format("{{x{0}y{1}z-10co}}", targetXa1, targetYa1);
            PortWrite(msg);
        }

        //向串口输出命令字符
        private void PortWrite(string message)
        {
            if (port != null && port.IsOpen)
            {
                port.Write(message);
                //port.WriteLine(message);
            }
        }


        private void btCap_Click(object sender, EventArgs e)
        {
            using (var frameMat = capture.RetrieveMat())
            {
                var frameBitmap = BitmapConverter.ToBitmap(frameMat);

                frameBitmap.Save(@"E:\Project\MeArm\试验\视觉控制\CVControl\CameraCap\Cap_" + DateTime.Now.ToString("yyyyMMdd_HHmmss")+".jpg", ImageFormat.Jpeg);
            }
        }

        private void backgroundWorker1_DoWork(object sender, DoWorkEventArgs e)
        {
            var bgWorker = (BackgroundWorker)sender;

            while (!bgWorker.CancellationPending)
            {
                using (var frameMat = capture.RetrieveMat())
                {
                    var frameBitmap = BitmapConverter.ToBitmap(frameMat);

                    bgWorker.ReportProgress(0, frameBitmap);
                }
                Thread.Sleep(100);
            }

        }

        private void backgroundWorker1_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            var frameBitmap = (Bitmap)e.UserState;
            pbCap.Image?.Dispose();
            pbCap.Image = frameBitmap;
        }
    }
}
