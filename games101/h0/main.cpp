#include<cmath>
#include<Eigen/Core>
#include<Eigen/Dense>
#include<iostream>

int main(){

    const float pi = std::asin(1.0)*2.0;

    Eigen::Vector3f p(2.0f, 1.0f, 1.0f);
    Eigen::Matrix3f r, m;
    r << std::cos(pi/4), -std::sin(pi/4), 0.0f, std::sin(pi/4), std::cos(pi/4), 0.0f, 0.0f, 0.0f, 1.0f;
    m << 1.0f , 0.0f, 1.0f, 0.0f, 1.0f, 2.0f, 0.0f, 0.0f, 1.0f;

    // auto tmp = r * p;
    // std::cout << tmp[0] << ' ' << tmp[1] << std::endl;

    auto res = m * r * p;
    std::cout << res[0] << ' ' << res[1] << std::endl;
    // 1.70711 4.12132

    return 0;
}