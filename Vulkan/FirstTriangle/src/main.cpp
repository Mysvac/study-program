#define GLFW_INCLUDE_VULKAN
#include <GLFW/glfw3.h>
// #include <vulkan/vulkan.h>
// GLFW_INCLUDE_VULKAN + <GLFW/glfw3.h> will include <vulkan/vulkan.h>

#include <iostream>
#include <string>
#include <vector>
#include <set>
#include <stdexcept>
#include <cstdlib>

class HelloTriangleApplication {
public:
    void run() {
        initWindow();
        initVulkan();
        mainLoop();
        cleanup();
    }

private:
    const uint32_t WIDTH = 800;
    const uint32_t HEIGHT = 600;
    GLFWwindow *window;
    VkInstance instance;

    void initWindow() {
        // initialize glfw library
        glfwInit();

        // tell glfw not use OpenGL API
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        // use unresizeable window, simplify program
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
    
        window = glfwCreateWindow(WIDTH, HEIGHT, "Vulkan", nullptr, nullptr);
    }
    void initVulkan() {
        createInstance();
    }

    void mainLoop() {
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
        }
    }

    void cleanup() {
        vkDestroyInstance(instance, nullptr);

        glfwDestroyWindow(window);

        glfwTerminate();
    }



    bool checkExtensionsAllProperties(std::vector<const char*> requiredExtensions){
        // 检查这些扩展是否都可用
        uint32_t extensionCount = 0;
        vkEnumerateInstanceExtensionProperties(nullptr, &extensionCount, nullptr);
        std::vector<VkExtensionProperties> extensions(extensionCount);
        vkEnumerateInstanceExtensionProperties(nullptr, &extensionCount, extensions.data());
        std::set<std::string> res;
        for (const auto& extension : extensions) {
            res.insert(extension.extensionName);
        }
        for(const char* name : requiredExtensions){
            if(res.find(name) == res.end()){
                std::cout << name << " not properties." << std::endl;
                return false;
            }
            else{
                std::cout << name << " is properties." << std::endl;
            }
        }
        return true;
    }

    void createInstance(){
        VkApplicationInfo appInfo{};
        appInfo.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
        appInfo.pApplicationName = "Hello Triangle";
        appInfo.applicationVersion = VK_MAKE_VERSION(1, 0, 0);
        appInfo.pEngineName = "No Engine";
        appInfo.engineVersion = VK_MAKE_VERSION(1, 0, 0);
        appInfo.apiVersion = VK_API_VERSION_1_0;

        VkInstanceCreateInfo createInfo{};
        createInfo.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
        createInfo.pApplicationInfo = &appInfo;

        uint32_t glfwExtensionCount = 0;
        const char** glfwExtensions;
        std::vector<const char*> requiredExtensions;


        glfwExtensions = glfwGetRequiredInstanceExtensions(&glfwExtensionCount);
        for(uint32_t i = 0; i < glfwExtensionCount; i++) {
            requiredExtensions.emplace_back(glfwExtensions[i]);
        }

        requiredExtensions.emplace_back(VK_KHR_PORTABILITY_ENUMERATION_EXTENSION_NAME);

        // checkExtensionsAllProperties(requiredExtensions);

        createInfo.flags |= VK_INSTANCE_CREATE_ENUMERATE_PORTABILITY_BIT_KHR;

        createInfo.enabledExtensionCount = (uint32_t) requiredExtensions.size();
        createInfo.ppEnabledExtensionNames = requiredExtensions.data();

        createInfo.enabledLayerCount = 0;

        if (vkCreateInstance(&createInfo, nullptr, &instance) != VK_SUCCESS) {
            throw std::runtime_error("failed to create instance!");
        }
    }
};

int main() {
    HelloTriangleApplication app;

    try {
        app.run();
    } catch (const std::exception& e) {
        std::cerr << e.what() << std::endl;
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

// #define GLFW_INCLUDE_VULKAN
// #include <GLFW/glfw3.h>

// #define GLM_FORCE_RADIANS
// #define GLM_FORCE_DEPTH_ZERO_TO_ONE
// #include <glm/vec4.hpp>
// #include <glm/mat4x4.hpp>

// #include <iostream>

// int main() {
//     glfwInit();

//     glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
//     GLFWwindow* window = glfwCreateWindow(800, 600, "Vulkan window", nullptr, nullptr);

//     uint32_t extensionCount = 0;
//     vkEnumerateInstanceExtensionProperties(nullptr, &extensionCount, nullptr);

//     std::cout << extensionCount << " extensions supported\n";

//     glm::mat4 matrix;
//     glm::vec4 vec;
//     auto test = matrix * vec;

//     while(!glfwWindowShouldClose(window)) {
//         glfwPollEvents();
//     }

//     glfwDestroyWindow(window);

//     glfwTerminate();

//     return 0;
// }
