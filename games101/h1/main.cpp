#include "Triangle.hpp"
#include "rasterizer.hpp"
#include <Eigen/Eigen>
#include <iostream>
#include <opencv2/opencv.hpp>

constexpr double MY_PI = 3.1415926;

Eigen::Matrix4f get_view_matrix(Eigen::Vector3f eye_pos)
{
    Eigen::Matrix4f view = Eigen::Matrix4f::Identity();

    Eigen::Matrix4f translate;
    translate << 1, 0, 0, -eye_pos[0], 0, 1, 0, -eye_pos[1], 0, 0, 1,
        -eye_pos[2], 0, 0, 0, 1;

    view = translate * view;

    return view;
}

Eigen::Matrix4f get_model_matrix(float rotation_angle)
{
    Eigen::Matrix4f model = Eigen::Matrix4f::Identity();

    // TODO: Implement this function
    // Create the model matrix for rotating the triangle around the Z axis.
    // Then return it.
    // 转弧度
    float t_angle = rotation_angle / 180.0f * MY_PI;
    model << 
    std::cosf(t_angle) , -std::sinf(t_angle) , 0.0f, 0.0f,
    std::sinf(t_angle) , std::cosf(t_angle), 0.0f, 0.0f,
    0.0f, 0.0f, 1.0f, 0.0f,
    0.0f, 0.0f, 0.0f, 1.0f;

    return model;
}

Eigen::Matrix4f get_projection_matrix(float eye_fov, float aspect_ratio,
                                      float zNear, float zFar)
{
    // Students will implement this function

    Eigen::Matrix4f projection = Eigen::Matrix4f::Identity();

    // TODO: Implement this function
    // Create the projection matrix for the given parameters.
    // Then return it.

    // 转弧度
    float t_eye_fov = eye_fov / 180.0f * MY_PI;
    float half_height = tanf(t_eye_fov / 2.0f) * abs(zNear);
    float half_width = aspect_ratio * half_height;

    float n = -zNear;
    float f = -zFar;


    /**
     * [x, y, z, 1] -> [x * zN, y * zN , unKnow, z]
     * [xN, yN, zN, 1] -> [xN * zN, yN * zN, zN * zN, zN]
     * [0, 0, zF, 1] -> [0, 0, zF * zF, zF]
     * A*zF + B = zF*zF
     * A*zN + B = ZN*ZN
     * A(ZF-ZN) = (zF-zN)(zF+zN)
     * A = zF + zN
     * B = - (ZF * zN)
     */

    projection << 
    n , 0.0f , 0.0f , 0.0f,
    0.0f, n, 0.0f, 0.0f,
    0.0f, 0.0f, f + n, -(f * n),
    0.0f, 0.0f, 1, 0.0f;


    /**
     * 位移与缩放
     */
    Eigen::Matrix4f m, t;
    m << 
    1.0f, 0.0f, 0.0f, 0.0f,
    0.0f, 1.0f, 0.0f, 0.0f,
    0.0f, 0.0f, 1.0f, (n + f) / 2.0f,
    0.0f, 0.0f, 0.0f, 1.0f;

    t << 
    1.0f / half_height , 0.0f, 0.0f, 0.0f,
    0.0f, 1.0f / half_width , 0.0f, 0.0f,
    0.0f, 0.0f, 2.0f / (f - n), 0.0f,
    0.0f, 0.0f, 0.0f, 1.0f;

    Eigen::Matrix4f o = m * t;

    projection = o * projection;
    

    return projection;
}

Eigen::Matrix4f get_rotation(Vector3f axis, float angle){
    Eigen::Matrix4f model = Eigen::Matrix4f::Identity();
    Eigen::Vector3f naxis = axis.normalized();
    float cos_a = std::cos(angle / 180.0f * MY_PI);
    float sin_a = std::sin(angle / 180.0f * MY_PI);
    // 套公式
    model <<
    cos_a + naxis.x() * naxis.x() * (1.0f - cos_a),
    naxis.x() * naxis.y() * (1.0f - cos_a) - naxis.z() * sin_a,
    naxis.x() * naxis.z() * (1.0f - cos_a) + naxis.y() * sin_a,
    0.0f,
    naxis.y() * naxis.x() * (1.0f - cos_a) + naxis.z() * sin_a,
    cos_a + naxis.y() * naxis.y() * (1.0f - cos_a),
    naxis.y() * naxis.z() * (1.0f - cos_a) - naxis.x() * sin_a,
    0.0f,
    naxis.z() * naxis.x() * (1.0f - cos_a) - naxis.y() * sin_a,
    naxis.z() * naxis.y() * (1.0f - cos_a) + naxis.x() * sin_a,
    cos_a + naxis.z() * naxis.z() * (1.0f - cos_a),
    0.0f,
    0.0f, 0.0f, 0.0f, 1.0f;
    return model;
}


int main(int argc, const char** argv)
{
    float angle = 0;
    bool command_line = false;
    std::string filename = "output.png";

    if (argc >= 3) {
        command_line = true;
        angle = std::stof(argv[2]); // -r by default
        if (argc == 4) {
            filename = std::string(argv[3]);
        }
        else
            return 0;
    }

    rst::rasterizer r(700, 700);

    Eigen::Vector3f eye_pos = {0, 0, 5};

    std::vector<Eigen::Vector3f> pos{{2, 0, -2}, {0, 2, -2}, {-2, 0, -2}};

    std::vector<Eigen::Vector3i> ind{{0, 1, 2}};

    auto pos_id = r.load_positions(pos);
    auto ind_id = r.load_indices(ind);

    int key = 0;
    int frame_count = 0;

    if (command_line) {
        r.clear(rst::Buffers::Color | rst::Buffers::Depth);

        r.set_model(get_rotation({ 0, 0, 1} ,angle));
        r.set_view(get_view_matrix(eye_pos));
        r.set_projection(get_projection_matrix(45, 1, 0.1, 50));

        r.draw(pos_id, ind_id, rst::Primitive::Triangle);
        cv::Mat image(700, 700, CV_32FC3, r.frame_buffer().data());
        image.convertTo(image, CV_8UC3, 1.0f);

        cv::imwrite(filename, image);

        return 0;
    }

    while (key != 27) {
        r.clear(rst::Buffers::Color | rst::Buffers::Depth);

        r.set_model(get_rotation({ 0, 0, 1} ,angle));
        r.set_view(get_view_matrix(eye_pos));
        r.set_projection(get_projection_matrix(45, 1, 0.1, 50));

        r.draw(pos_id, ind_id, rst::Primitive::Triangle);

        cv::Mat image(700, 700, CV_32FC3, r.frame_buffer().data());
        image.convertTo(image, CV_8UC3, 1.0f);
        cv::imshow("image", image);
        key = cv::waitKey(10);

        std::cout << "frame count: " << frame_count++ << '\n';

        if (key == 'a') {
            angle += 10;
        }
        else if (key == 'd') {
            angle -= 10;
        }
    }

    return 0;
}
