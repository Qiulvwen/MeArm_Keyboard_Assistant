import cv2
import numpy as np

# 标定图像
def calibration_photo(photo_path):
    # 设置要标定的角点个数
    x_nums = 9  # x方向上的角点个数
    y_nums = 14
    # 设置(生成)标定图在世界坐标中的坐标
    world_point = np.zeros((x_nums * y_nums, 3), np.float32)  # 生成x_nums*y_nums个坐标，每个坐标包含x,y,z三个元素
    world_point[:, :2] = 15 * np.mgrid[:x_nums, :y_nums].T.reshape(-1, 2)  # mgrid[]生成包含两个二维矩阵的矩阵，每个矩阵都有x_nums列,y_nums行
    print('world point:',world_point)
    # .T矩阵的转置
    # reshape()重新规划矩阵，但不改变矩阵元素
    # 设置世界坐标的坐标
    axis = 15* np.float32([[3, 0, 0], [0, 3, 0], [0, 0, -3]]).reshape(-1, 3)
    # 设置角点查找限制
    criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 30, 0.001)

    image = cv2.imread(photo_path)

    gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
    # 查找角点
    ok, corners = cv2.findChessboardCorners(gray, (x_nums, y_nums), )
    # print(ok)
    if ok:
        # 获取更精确的角点位置
        exact_corners = cv2.cornerSubPix(gray, corners, (11, 11), (-1, -1), criteria)

        # 获取外参
        _, rvec, tvec, inliers = cv2.solvePnPRansac(world_point, exact_corners, mtx, dist)
        #获得的旋转矩阵是向量，是3×1的矩阵，想要还原回3×3的矩阵，需要罗德里格斯变换Rodrigues，
        
        rotation_m, _ = cv2.Rodrigues(rvec)#罗德里格斯变换
        # print(rotation_m)
        # print('旋转矩阵是：\n', rvec)
        # print('平移矩阵是:\n', tvec)
        rotation_t = np.hstack([rotation_m,tvec])
        rotation_t_Homogeneous_matrix = np.vstack([rotation_t,np.array([[0, 0, 0, 1]])])
        print(rotation_t_Homogeneous_matrix)
        imgpts, jac = cv2.projectPoints(axis, rvec, tvec, mtx, dist)
        # 可视化角点
        cv2.drawChessboardCorners(image, (x_nums,y_nums), corners, ok)
        cv2.imshow('img', image)
        return rotation_t_Homogeneous_matrix # 返回旋转矩阵和平移矩阵组成的其次矩阵


if __name__ == '__main__':
    # 读取相机内参
    with np.load('camera_data.npz') as X:
        mtx, dist = [X[i] for i in ('mtx', 'dist')]
        print(mtx, '\n', dist)
    #photo_path = "right14.jpg" # 标定图像保存路径
    photo_path = "../CVControl/CameraCap/Cap_20211102_005426.jpg"
    calibration_photo(photo_path)
    cv2.waitKey()
    cv2.destroyAllWindows()
