import cv2
import numpy as np

# 标定图像
def calibration_photo(photo_path):
    #使用**cv.getOptimalNewCameraMatrix**()基于自由缩放参数来优化相机矩阵。
    #如果缩放参数alpha = 0，则返回具有最少不需要像素的未失真图像。
    #因此，它甚至可能会删除图像角落的一些像素。如果alpha = 1，则所有像素都保留有一些额外的黑色图像。
    #此函数还返回可用于裁剪结果的图像ROI。
    image = cv2.imread(photo_path)
    h,  w = image.shape[:2]
    newcameramtx, roi = cv2.getOptimalNewCameraMatrix(mtx, dist, (w,h), 1, (w,h))
    #使用cv.undistort()
    # undistort
    dst = cv2.undistort(image, mtx, dist, None, newcameramtx)
    # 剪裁图像
    x, y, w, h = roi
    dst = dst[y:y+h, x:x+w]
    cv2.imshow('calibresult', dst)


if __name__ == '__main__':
    # 读取相机内参
    with np.load('camera_data.npz') as X:
        mtx, dist = [X[i] for i in ('mtx', 'dist')]
        print(mtx, '\n', dist)
    #photo_path = "right12.jpg" # 标定图像保存路径
    photo_path = "../CVControl/CameraCap/Cap_20211102_005426.jpg"
    calibration_photo(photo_path)
    cv2.waitKey()
    cv2.destroyAllWindows()
