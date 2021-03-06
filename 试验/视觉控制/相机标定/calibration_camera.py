import cv2
import numpy as np
import glob

# 标定图像
'''
标定步骤：
1）
'''
def calibration_camera(photo_path):
    # 设置要标定的角点个数（我这里使用的是11 × 8的棋盘，11×8代
    #表的是内角点，这里要注意，不懂的话可以数数我拍摄的棋盘你就知道哪个是内角点了）
    #x_nums = 11  # x方向上的角点个数
    #y_nums = 8
    x_nums = 9  # x方向上的角点个数
    y_nums = 14
    # 设置(生成)标定图在世界坐标中的坐标
    world_point = np.zeros((x_nums * y_nums, 3), np.float32)  # 生成x_nums*y_nums个坐标，每个坐标包含x,y,z三个元素
    print(world_point)
    world_point[:, :2] =20 * np.mgrid[:x_nums, :y_nums].T.reshape(-1, 2)  # mgrid[]生成包含两个二维矩阵的矩阵，每个矩阵都有x_nums列,y_nums行，我这里用的是20mm×20mm的方格，所以乘了20，以mm代表世界坐标的计量单位
    print(world_point)#打印出来的就是某一张出图片的世界坐标了
    # .T矩阵的转置
    # reshape()重新规划矩阵，但不改变矩阵元素
    # 保存角点坐标
    world_position = [] #存放世界坐标
    image_position = [] #存放棋盘角点对应的图片像素坐标
    
    '''
    下面就是查找图片中角点的像素坐标存入image_position了
    '''
    
    # 设置角点查找限制
    criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 30, 0.001)
    # 获取所有标定图
    images = glob.glob(photo_path + '\\*.jpg')
    # print(images)
    for image_path in images:
        image = cv2.imread(image_path)
        gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
        # 查找角点
        ok, corners = cv2.findChessboardCorners(gray, (x_nums, y_nums), None)
        """
		如果能找得到角点：返回角点对应的像素坐标，并且将其对应到世界坐标中
		世界坐标[0,0,0],[0,1,0].....
		图像坐标[10.123123,20.123122335],[19.123123,21.123123123]....
        """
        if ok:
            print(image_path)
            # 把每一幅图像的世界坐标放到world_position中
            world_position.append(world_point)
            # 获取更精确的角点位置
            exact_corners = cv2.cornerSubPix(gray, corners, (11, 11), (-1, -1), criteria)
            # 把获取的角点坐标放到image_position中
            image_position.append(exact_corners)
            # 可视化角点
            image = cv2.drawChessboardCorners(image,(x_nums,y_nums),exact_corners,ok)
            cv2.imshow('image_corner',image)
            cv2.waitKey(1000)
        img1 = image
     
    """
    点对应好了，开始计算内参，畸变矩阵，外参
    """
    print(">==> Starting calibration")
    ret, mtx, dist, rvecs, tvecs = cv2.calibrateCamera(world_position, image_position, gray.shape[::-1], None, None)
    print(">==> Calibration ended")

    h,  w = img1.shape[:2]
    print("Image Width, Height")
    print(w, h)
    #if using Alpha 0, so we discard the black pixels from the distortion.  this helps make the entire region of interest is the full dimensions of the image (after undistort)
    #if using Alpha 1, we retain the black pixels, and obtain the region of interest as the valid pixels for the matrix.
    #i will use Apha 1, so that I don't have to run undistort.. and can just calculate my real world x,y
    newcam_mtx, roi=cv2.getOptimalNewCameraMatrix(mtx, dist, (w,h), 1, (w,h))


    inverse = np.linalg.inv(newcam_mtx)
    print("Inverse New Camera Matrix")
    print(inverse)

    # undistort
    undst = cv2.undistort(img1, mtx, dist, None, newcam_mtx)

    #内参是mtx，畸变矩阵是dist，旋转向量（要得到矩阵还要进行罗德里格斯变换）rvecs，外参：平移矩阵tvecs
    #感兴趣区域 roi, 新内参 newcam_mtx
    # 将内参保存起来
    np.savez('camera_data', mtx=mtx, dist=dist, rvecs=rvecs, tvecs=tvecs, roi=roi, newcam_mtx=newcam_mtx )
    
    print('内参是：\n', mtx, '\n畸变参数是：\n', dist,
       '\n外参：旋转向量（要得到矩阵还要进行罗德里格斯变换，下章讲）是：\n',rvecs, '\n外参：平移矩阵是：\n',tvecs)
          
    # 计算偏差
    mean_error = 0
    for i in range(len(world_position)):
        image_position2, _ = cv2.projectPoints(world_position[i], rvecs[i], tvecs[i], mtx, dist)
        error = cv2.norm(image_position[i], image_position2, cv2.NORM_L2) / len(image_position2)
        mean_error += error
    print("total error: ", mean_error / len(image_position))

    # crop the image
    #x, y, w, h = roi
    #dst = dst[y:y+h, x:x+w]
    #cv2.circle(dst,(308,160),5,(0,255,0),2)
    cv2.destroyAllWindows()
    cv2.imshow('img1', img1)    
    cv2.imshow('undst', undst)
    cv2.waitKey()  


def main():
    # 标定图像保存路径
    #photo_path = "checkerboard"
    #路径不能有中文
    photo_path = "../CVControl/CameraCap"
    calibration_camera(photo_path)
if __name__ == '__main__':
    main()
