#include <chrono>
#include <iostream>
#include <opencv2/opencv.hpp>

std::vector<cv::Point2f> control_points;

void mouse_handler(int event, int x, int y, int flags, void *userdata) 
{
    if (event == cv::EVENT_LBUTTONDOWN /* && control_points.size() < 4 */) 
    {
        std::cout << "Left button of the mouse is clicked - position (" << x << ", "
        << y << ")" << '\n';
        control_points.emplace_back(float(x), float(y));
    }     
}

void naive_bezier(const std::vector<cv::Point2f> &points, cv::Mat &window) 
{
    auto &p_0 = points[0];
    auto &p_1 = points[1];
    auto &p_2 = points[2];
    auto &p_3 = points[3];

    for (double t = 0.0; t <= 1.0; t += 0.001) 
    {
        auto point = std::pow(1. - t, 3.) * p_0 + 3. * t * std::pow(1. - t, 2.) * p_1 +
                 3 * std::pow(t, 2.) * (1. - t) * p_2 + std::pow(t, 3.) * p_3;

        window.at<cv::Vec3b>(point.y, point.x)[2] = 255;
    }
}

cv::Point2f recursive_bezier(const std::vector<cv::Point2f> &control_points, float t) 
{
    // TODO: Implement de Casteljau's algorithm
    std::vector<cv::Point2f> arr[2];
    arr[0] = control_points;

    while(arr[0].size()>1){
        size_t len = arr[0].size();
        arr[1].clear();
        arr[1].reserve(len-1);

        for(int i=0; i+1<len; ++i){
            arr[1].push_back({arr[0][i]*t + arr[0][i+1]*(1.0-t)});
        }

        std::swap(arr[0], arr[1]);
    }

    return arr[0][0];
}

void bezier(const std::vector<cv::Point2f> &control_points, cv::Mat &window) 
{
    // TODO: Iterate through all t = 0 to t = 1 with small steps, and call de Casteljau's 
    // recursive Bezier algorithm.
    for (float i = 0.0; i <= 1.0; i += 0.001) 
    {
        auto res = recursive_bezier(control_points, i);
        window.at<cv::Vec3b>(res.y, res.x)[2] = 255;
    }

}

int main() 
{
    cv::Mat window = cv::Mat(700, 700, CV_8UC3, cv::Scalar(0));
    cv::cvtColor(window, window, cv::COLOR_BGR2RGB);
    cv::namedWindow("Bezier Curve", cv::WINDOW_AUTOSIZE);

    cv::setMouseCallback("Bezier Curve", mouse_handler, nullptr);

    int key = -1;
    while (key != 27) 
    {
        for (auto &point : control_points) 
        {
            cv::circle(window, point, 3, {255, 255, 255}, 3);
        }

        // 修改此处数值，实现任意点数曲线
        if (control_points.size() == 7) 
        {
            // naive_bezier(control_points, window);
            bezier(control_points, window);

            cv::imshow("Bezier Curve", window);
            cv::imwrite("my_bezier_curve.png", window);
            key = cv::waitKey(0);

            return 0;
        }


        cv::imshow("Bezier Curve", window);
        cv::imwrite("my_bezier_curve.png", window);
        key = cv::waitKey(20);
    }

    return 0;
}
