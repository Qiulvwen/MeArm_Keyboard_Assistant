import cv2
import numpy as np

# 在视频中标出光心位置
def draw_principal_point():
    #Python opencv警告异常:SourceReaderCB::~SourceReaderCB terminating async callback
    #第一种： 修改代码，添加一个参数；cv2.VideoCapture(1,cv2.CAP_DSHOW)
    #第二种：只能是windows系统下,在cmd并输入：setx OPENCV_VIDEOIO_PRIORITY_MSMF 0
    cap = cv2.VideoCapture(1, cv2.CAP_DSHOW)
    if not cap.isOpened():
        print("Cannot open camera")
        exit()

    # 光心坐标
    cx=newcam_mtx[0,2]
    cy=newcam_mtx[1,2]
    fx=newcam_mtx[0,0]
    print(cx)
    print(cy)
    while True:
        # 逐帧捕获
        ret, frame = cap.read()
        # 如果正确读取帧，ret为True
        if not ret:
            print("Can't receive frame (stream end?). Exiting ...")
            break
        # 显示结果帧
        cv2.circle(frame,(int(cx),int(cy)),20,(0,255,0),2)
        cv2.imshow('frame', frame)
        if cv2.waitKey(1) == ord('q'):
            break
    # 完成所有操作后，释放捕获器
    cap.release()
    #cv2.destroyAllWindows()
        


if __name__ == '__main__':
    # 读取相机内参
    with np.load('camera_data.npz') as X:
        mtx, dist, newcam_mtx = [X[i] for i in ('mtx', 'dist','newcam_mtx')]
        print(newcam_mtx)
    draw_principal_point()
