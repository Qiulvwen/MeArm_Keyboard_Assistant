
namespace CVControlClient
{
    partial class Form1
    {
        /// <summary>
        /// 必需的设计器变量。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// 清理所有正在使用的资源。
        /// </summary>
        /// <param name="disposing">如果应释放托管资源，为 true；否则为 false。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows 窗体设计器生成的代码

        /// <summary>
        /// 设计器支持所需的方法 - 不要修改
        /// 使用代码编辑器修改此方法的内容。
        /// </summary>
        private void InitializeComponent()
        {
            this.btGo = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.tbBasePivotX = new System.Windows.Forms.TextBox();
            this.tbBasePivotY = new System.Windows.Forms.TextBox();
            this.tbTyped = new System.Windows.Forms.TextBox();
            this.label4 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.tbResult = new System.Windows.Forms.TextBox();
            this.label6 = new System.Windows.Forms.Label();
            this.tbCOM = new System.Windows.Forms.TextBox();
            this.label7 = new System.Windows.Forms.Label();
            this.tbBaudRate = new System.Windows.Forms.TextBox();
            this.btCap = new System.Windows.Forms.Button();
            this.pbCap = new System.Windows.Forms.PictureBox();
            this.backgroundWorker1 = new System.ComponentModel.BackgroundWorker();
            ((System.ComponentModel.ISupportInitialize)(this.pbCap)).BeginInit();
            this.SuspendLayout();
            // 
            // btGo
            // 
            this.btGo.Location = new System.Drawing.Point(44, 126);
            this.btGo.Name = "btGo";
            this.btGo.Size = new System.Drawing.Size(123, 58);
            this.btGo.TabIndex = 0;
            this.btGo.Text = "开始";
            this.btGo.UseVisualStyleBackColor = true;
            this.btGo.Click += new System.EventHandler(this.btGo_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(42, 52);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(275, 12);
            this.label1.TabIndex = 1;
            this.label1.Text = "机器臂中心点(Base pivot) 在图像中的像素坐标：";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(447, 55);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(11, 12);
            this.label2.TabIndex = 2;
            this.label2.Text = "y";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(323, 52);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(11, 12);
            this.label3.TabIndex = 3;
            this.label3.Text = "x";
            // 
            // tbBasePivotX
            // 
            this.tbBasePivotX.Location = new System.Drawing.Point(341, 52);
            this.tbBasePivotX.Name = "tbBasePivotX";
            this.tbBasePivotX.Size = new System.Drawing.Size(100, 21);
            this.tbBasePivotX.TabIndex = 4;
            this.tbBasePivotX.Text = "1293";
            // 
            // tbBasePivotY
            // 
            this.tbBasePivotY.Location = new System.Drawing.Point(464, 52);
            this.tbBasePivotY.Name = "tbBasePivotY";
            this.tbBasePivotY.Size = new System.Drawing.Size(100, 21);
            this.tbBasePivotY.TabIndex = 5;
            this.tbBasePivotY.Text = "2296";
            // 
            // tbTyped
            // 
            this.tbTyped.Location = new System.Drawing.Point(173, 92);
            this.tbTyped.Name = "tbTyped";
            this.tbTyped.Size = new System.Drawing.Size(524, 21);
            this.tbTyped.TabIndex = 6;
            this.tbTyped.Text = "helloword";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(42, 95);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(125, 12);
            this.label4.TabIndex = 7;
            this.label4.Text = "需要机械臂打的文字：";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(42, 209);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(101, 12);
            this.label5.TabIndex = 8;
            this.label5.Text = "机械臂执行结果：";
            // 
            // tbResult
            // 
            this.tbResult.Location = new System.Drawing.Point(173, 200);
            this.tbResult.Name = "tbResult";
            this.tbResult.Size = new System.Drawing.Size(524, 21);
            this.tbResult.TabIndex = 9;
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(42, 9);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(107, 12);
            this.label6.TabIndex = 10;
            this.label6.Text = "Arduino串口位置：";
            // 
            // tbCOM
            // 
            this.tbCOM.Location = new System.Drawing.Point(155, 6);
            this.tbCOM.Name = "tbCOM";
            this.tbCOM.Size = new System.Drawing.Size(100, 21);
            this.tbCOM.TabIndex = 11;
            this.tbCOM.Text = "COM7";
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(285, 9);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(53, 12);
            this.label7.TabIndex = 12;
            this.label7.Text = "波特率：";
            // 
            // tbBaudRate
            // 
            this.tbBaudRate.Location = new System.Drawing.Point(341, 6);
            this.tbBaudRate.Name = "tbBaudRate";
            this.tbBaudRate.Size = new System.Drawing.Size(100, 21);
            this.tbBaudRate.TabIndex = 13;
            this.tbBaudRate.Text = "9600";
            // 
            // btCap
            // 
            this.btCap.Location = new System.Drawing.Point(725, 15);
            this.btCap.Name = "btCap";
            this.btCap.Size = new System.Drawing.Size(123, 58);
            this.btCap.TabIndex = 14;
            this.btCap.Text = "截图";
            this.btCap.UseVisualStyleBackColor = true;
            this.btCap.Click += new System.EventHandler(this.btCap_Click);
            // 
            // pbCap
            // 
            this.pbCap.BackColor = System.Drawing.SystemColors.WindowText;
            this.pbCap.Location = new System.Drawing.Point(725, 89);
            this.pbCap.Name = "pbCap";
            this.pbCap.Size = new System.Drawing.Size(482, 393);
            this.pbCap.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pbCap.TabIndex = 15;
            this.pbCap.TabStop = false;
            // 
            // backgroundWorker1
            // 
            this.backgroundWorker1.WorkerReportsProgress = true;
            this.backgroundWorker1.WorkerSupportsCancellation = true;
            this.backgroundWorker1.DoWork += new System.ComponentModel.DoWorkEventHandler(this.backgroundWorker1_DoWork);
            this.backgroundWorker1.ProgressChanged += new System.ComponentModel.ProgressChangedEventHandler(this.backgroundWorker1_ProgressChanged);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1219, 512);
            this.Controls.Add(this.pbCap);
            this.Controls.Add(this.btCap);
            this.Controls.Add(this.tbBaudRate);
            this.Controls.Add(this.label7);
            this.Controls.Add(this.tbCOM);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.tbResult);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.tbTyped);
            this.Controls.Add(this.tbBasePivotY);
            this.Controls.Add(this.tbBasePivotX);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.btGo);
            this.Name = "Form1";
            this.Text = "Form1";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.Form1_FormClosing);
            this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.Form1_FormClosed);
            this.Load += new System.EventHandler(this.Form1_Load);
            ((System.ComponentModel.ISupportInitialize)(this.pbCap)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button btGo;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.TextBox tbBasePivotX;
        private System.Windows.Forms.TextBox tbBasePivotY;
        private System.Windows.Forms.TextBox tbTyped;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.TextBox tbResult;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.TextBox tbCOM;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.TextBox tbBaudRate;
        private System.Windows.Forms.Button btCap;
        private System.Windows.Forms.PictureBox pbCap;
        private System.ComponentModel.BackgroundWorker backgroundWorker1;
    }
}

