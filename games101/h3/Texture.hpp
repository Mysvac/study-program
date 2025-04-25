//
// Created by LEI XU on 4/27/19.
//

#ifndef RASTERIZER_TEXTURE_H
#define RASTERIZER_TEXTURE_H
#include "global.hpp"
#include <Eigen/Eigen>
#include <opencv2/opencv.hpp>
class Texture{
private:
    cv::Mat image_data;

public:
    Texture(const std::string& name)
    {
        image_data = cv::imread(name);
        cv::cvtColor(image_data, image_data, cv::COLOR_RGB2BGR);
        width = image_data.cols;
        height = image_data.rows;
    }

    int width, height;

    Eigen::Vector3f getColor(float u, float v)
    {
        auto u_img = u * width;
        auto v_img = (1 - v) * height;
        auto color = image_data.at<cv::Vec3b>(v_img, u_img);
        return Eigen::Vector3f(color[0], color[1], color[2]);
    }

    Eigen::Vector3f getColorBilinear(float u, float v)
    {
        auto u_img = u * width;
        auto v_img = (1 - v) * height;
        auto color1 = image_data.at<cv::Vec3b>(v_img, u_img);
        auto color2 = image_data.at<cv::Vec3b>(v_img+1, u_img);
        auto color3 = image_data.at<cv::Vec3b>(v_img, u_img+1);
        auto color4 = image_data.at<cv::Vec3b>(v_img+1, u_img+1);
        color1 = color1 * (int(v_img + 1) - v_img) + color2 * (v_img - int(v_img));
        color2 = color3 * (int(v_img + 1) - v_img) + color4 * (v_img - int(v_img));
        auto color = color1 * (int(u_img +1) - u_img) + color2 * (u_img - int(u_img));
        return Eigen::Vector3f(color[0], color[1], color[2]);
    }

};
#endif //RASTERIZER_TEXTURE_H
