#define GLFW_INCLUDE_VULKAN
#include <GLFW/glfw3.h>
// #include <vulkan/vulkan.h>
// GLFW_INCLUDE_VULKAN + <GLFW/glfw3.h> 会自动 include <vulkan/vulkan.h>

#include <iostream>
#include <optional>
#include <string>
#include <cstring>
#include <vector>
#include <algorithm>
#include <map>
#include <set>
#include <stdexcept>
#include <cstdlib>

/**
 * @brief 创建DebugUtilsMessengerEXT
 * @details
 * 由于它是扩展内的函数，默认可能不存在，不能直接调用。
 * 必须用过vkGetInstanceProcAddr尝试获取函数指针
 */
VkResult CreateDebugUtilsMessengerEXT(
    VkInstance instance, 
    const VkDebugUtilsMessengerCreateInfoEXT* pCreateInfo, 
    const VkAllocationCallbacks* pAllocator, 
    VkDebugUtilsMessengerEXT* pDebugMessenger
) {
    auto func = (PFN_vkCreateDebugUtilsMessengerEXT) vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT");
    if (func != nullptr) {
        return func(instance, pCreateInfo, pAllocator, pDebugMessenger);
    } else {
        return VK_ERROR_EXTENSION_NOT_PRESENT;
    }
}

/**
 * @brief 消耗DebugUtilsMessengerEXT
 * @details
 * 由于它是扩展内的函数，默认可能不存在，不能直接调用。
 * 必须用过vkGetInstanceProcAddr尝试获取函数指针
 */
void DestroyDebugUtilsMessengerEXT(
    VkInstance instance, 
    VkDebugUtilsMessengerEXT debugMessenger, 
    const VkAllocationCallbacks* pAllocator
) {
    auto func = (PFN_vkDestroyDebugUtilsMessengerEXT) vkGetInstanceProcAddr(instance, "vkDestroyDebugUtilsMessengerEXT");
    if (func != nullptr) {
        func(instance, debugMessenger, pAllocator);
    }
}


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
    VkDebugUtilsMessengerEXT debugMessenger;
    VkPhysicalDevice physicalDevice = VK_NULL_HANDLE;
    VkDevice device;
    VkQueue graphicsQueue;

    // 验证层名
    const std::vector<const char*> validationLayers = {
        "VK_LAYER_KHRONOS_validation"
    };
    
    // 根据语言标准设定的NDEBUG宏，控制验证层释放启用
    #ifdef NDEBUG
        const bool enableValidationLayers = false;
    #else
        const bool enableValidationLayers = true;
    #endif

    // 调试回调
    static VKAPI_ATTR VkBool32 VKAPI_CALL debugCallback(
        VkDebugUtilsMessageSeverityFlagBitsEXT messageSeverity,
        VkDebugUtilsMessageTypeFlagsEXT messageType,
        const VkDebugUtilsMessengerCallbackDataEXT* pCallbackData,
        void* pUserData) {
    
        std::cerr << "validation layer: " << pCallbackData->pMessage << std::endl;
    
        return VK_FALSE;
    }

    // 判断 层 是否支持
    bool checkLayerSupport(const std::vector<const char*>& requiredLayer) {
        uint32_t layerCount;
        vkEnumerateInstanceLayerProperties(&layerCount, nullptr);
        std::vector<VkLayerProperties> availableLayers(layerCount);
        vkEnumerateInstanceLayerProperties(&layerCount, availableLayers.data());

        for (const char* layerName : requiredLayer){
            if( std::find_if(availableLayers.begin(), availableLayers.end(), [layerName](const auto& x){
                return !strcmp(layerName, x.layerName);
            }) == availableLayers.end()){
                return false;
            }
        }
    
        return true;
    }

    // 判断 扩展 是否支持
    bool checkExtensionsSupport(const std::vector<const char*>& requiredExtensions){
        uint32_t extensionCount = 0;
        vkEnumerateInstanceExtensionProperties(nullptr, &extensionCount, nullptr);
        std::vector<VkExtensionProperties> extensions(extensionCount);
        vkEnumerateInstanceExtensionProperties(nullptr, &extensionCount, extensions.data());
        
        for(const char* name : requiredExtensions){
            if(std::find_if(extensions.begin(), extensions.end(), [name](const auto& x){
                return !std::strcmp(x.extensionName, name);
            }) == extensions.end()){
                return false;
            }
        }
        return true;
    }

    // 获取需要的层列表
    std::vector<const char*> getRequiredLayers() {
        std::vector<const char*> layers;

        // 添加验证层扩展
        if (enableValidationLayers) {
            for(const char* it : validationLayers){
                layers.emplace_back(it);
            }
        }
    
        return layers;
    }

    // 获取需要的扩展列表
    std::vector<const char*> getRequiredExtensions() {
        // 添加 glfw 依赖扩展
        uint32_t glfwExtensionCount = 0;
        const char** glfwExtensions;
        glfwExtensions = glfwGetRequiredInstanceExtensions(&glfwExtensionCount);
        std::vector<const char*> extensions(glfwExtensions, glfwExtensions + glfwExtensionCount);

        // 添加macOS转译兼容扩展
        extensions.emplace_back(VK_KHR_PORTABILITY_ENUMERATION_EXTENSION_NAME);

        // 添加验证层扩展
        if (enableValidationLayers) {
            extensions.emplace_back(VK_EXT_DEBUG_UTILS_EXTENSION_NAME);
        }
    
        return extensions;
    }

    // 填充DebugMessengerCreateInfo基本信息
    void populateDebugMessengerCreateInfo(VkDebugUtilsMessengerCreateInfoEXT& createInfo) {
        createInfo = {};
        createInfo.sType = VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT;
        createInfo.messageSeverity = VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT;
        createInfo.messageType = VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT;
        createInfo.pfnUserCallback = debugCallback;
        createInfo.pUserData = nullptr;
    }

    // 初始化调试控制器
    void setupDebugMessenger() {
        if (!enableValidationLayers) return;
        VkDebugUtilsMessengerCreateInfoEXT createInfo{};
        populateDebugMessengerCreateInfo(createInfo);

        if(CreateDebugUtilsMessengerEXT(instance, &createInfo, nullptr, &debugMessenger) != VK_SUCCESS) {
            throw std::runtime_error("failed to set up debug messenger!");
        }
    }


    // 判断显卡可用性
    bool rateDeviceSuitability(VkPhysicalDevice device) {
        // 获取设备参数
        VkPhysicalDeviceProperties deviceProperties;
        vkGetPhysicalDeviceProperties(device, &deviceProperties);
        VkPhysicalDeviceFeatures deviceFeatures;
        vkGetPhysicalDeviceFeatures(device, &deviceFeatures);
        if (!deviceFeatures.geometryShader) return 0;
        if(!findQueueFamilies(device).isComplete()) return 0;
        int score = 0;
        if (deviceProperties.deviceType == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU) {
            score += 1000;
        }
        score += deviceProperties.limits.maxImageDimension2D;
        return score;
    }

    // 选取物理显卡
    void pickPhysicalDevice(){
        uint32_t deviceCount = 0;
        vkEnumeratePhysicalDevices(instance, &deviceCount, nullptr);
        if (deviceCount == 0) {
            throw std::runtime_error("failed to find GPUs with Vulkan support!");
        }
        std::vector<VkPhysicalDevice> devices(deviceCount);
        vkEnumeratePhysicalDevices(instance, &deviceCount, devices.data());
        // 评分-排序
        std::multimap<int, VkPhysicalDevice> candidates;
        for (const auto& device : devices) {
            int score = rateDeviceSuitability(device);
            candidates.insert(std::make_pair(score, device));
        }
        // 选择最高分数的显卡
        if (candidates.rbegin()->first > 0) {
            physicalDevice = candidates.rbegin()->second;
        } 
        else {
            throw std::runtime_error("failed to find a suitable GPU!");
        }
    }

    struct QueueFamilyIndices {
        std::optional<uint32_t> graphicsFamily;
        bool isComplete() {
            return graphicsFamily.has_value();
        }
    };
    
    QueueFamilyIndices findQueueFamilies(VkPhysicalDevice device) {
        QueueFamilyIndices indices;
        uint32_t queueFamilyCount = 0;
        vkGetPhysicalDeviceQueueFamilyProperties(device, &queueFamilyCount, nullptr);
        std::vector<VkQueueFamilyProperties> queueFamilies(queueFamilyCount);
        vkGetPhysicalDeviceQueueFamilyProperties(device, &queueFamilyCount, queueFamilies.data());

        int i = 0;
        for (const auto& queueFamily : queueFamilies) {
            if (queueFamily.queueFlags & VK_QUEUE_GRAPHICS_BIT) {
                indices.graphicsFamily = i;
                break;
            }
            ++i;
        }

        return indices;
    }

    void createLogicalDevice() {
        // physicalDevice验证可用性时保证了queueFamilies不为空
        QueueFamilyIndices indices = findQueueFamilies(physicalDevice);

        // 队列族信息
        VkDeviceQueueCreateInfo queueCreateInfo{};
        queueCreateInfo.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
        queueCreateInfo.queueFamilyIndex = indices.graphicsFamily.value();
        queueCreateInfo.queueCount = 1;
        float queuePriority = 1.0f;
        queueCreateInfo.pQueuePriorities = &queuePriority;

        // 物理设备信息
        VkPhysicalDeviceFeatures deviceFeatures{};

        // 逻辑设备创建信息
        VkDeviceCreateInfo createInfo{};
        createInfo.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
        createInfo.pQueueCreateInfos = &queueCreateInfo;
        createInfo.queueCreateInfoCount = 1;
        createInfo.pEnabledFeatures = &deviceFeatures;
        createInfo.enabledExtensionCount = 0;

        // 新版 验证层 不再区分逻辑和物理，此处代码可以省略
        if (enableValidationLayers) {
            createInfo.enabledLayerCount = static_cast<uint32_t>(validationLayers.size());
            createInfo.ppEnabledLayerNames = validationLayers.data();
        } else {
            createInfo.enabledLayerCount = 0;
        }

        if (vkCreateDevice(physicalDevice, &createInfo, nullptr, &device) != VK_SUCCESS) {
            throw std::runtime_error("failed to create logical device!");
        }
        vkGetDeviceQueue(device, indices.graphicsFamily.value(), 0, &graphicsQueue);
    }


    void initWindow() {
        // 初始化 glfw 库
        glfwInit();
        // 告诉 glfw 不使用 OpenGL API
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        // 使用不可变更大小的窗口，简化编程
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        // 创建窗口
        window = glfwCreateWindow(WIDTH, HEIGHT, "Vulkan", nullptr, nullptr);
    }


    void initVulkan() {
        // 创建Vulkan实例
        createInstance();
        // 初始化调试器
        setupDebugMessenger();
        // 挑选显卡-物理设备
        pickPhysicalDevice();
        // 创建逻辑设备
        createLogicalDevice();
    }

    void mainLoop() {
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
        }
    }

    void cleanup() {
        // 逻辑设备清理
        vkDestroyDevice(device, nullptr);

        // 无需手动销毁物理设备physicalDevice

        // 清理验证层
        if(enableValidationLayers){
            DestroyDebugUtilsMessengerEXT(instance, debugMessenger, nullptr);
        }

        // 清理资源
        vkDestroyInstance(instance, nullptr);

        glfwDestroyWindow(window);

        glfwTerminate();
    }




    void createInstance(){
        // 如果开启验证层，检测SDK是否支持
        if (enableValidationLayers && !checkLayerSupport(validationLayers)) {
            throw std::runtime_error("validation layers requested, but not available!");
        }

        // 应用基本信息配置
        VkApplicationInfo appInfo{};
        appInfo.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
        appInfo.pApplicationName = "Hello Triangle";
        appInfo.applicationVersion = VK_MAKE_VERSION(1, 0, 0);
        appInfo.pEngineName = "No Engine";
        appInfo.engineVersion = VK_MAKE_VERSION(1, 0, 0);
        appInfo.apiVersion = VK_API_VERSION_1_0;

        // VK实例创建信息
        VkInstanceCreateInfo createInfo{};
        createInfo.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
        createInfo.pApplicationInfo = &appInfo;
        // 允许转译兼容
        createInfo.flags |= VK_INSTANCE_CREATE_ENUMERATE_PORTABILITY_BIT_KHR;

        // 添加调试
        VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo{};
        if(enableValidationLayers){
            populateDebugMessengerCreateInfo(debugCreateInfo);
            createInfo.pNext = &debugCreateInfo;
        }
        else{
            createInfo.pNext = nullptr;
        }

        // 添加需要的扩展
        auto extensions = getRequiredExtensions();
        if(extensions.empty()){
            createInfo.enabledExtensionCount = 0;
        }
        else if(checkExtensionsSupport(extensions)){
            createInfo.enabledExtensionCount = static_cast<uint32_t>(extensions.size());
            createInfo.ppEnabledExtensionNames = extensions.data();
        }
        else{
            throw std::runtime_error("extensions requested, but not available!");
        }

        // 添加需要的层
        auto layers = getRequiredLayers();
        if(layers.empty()){
            createInfo.enabledLayerCount = 0;
        }
        else if(checkLayerSupport(layers)){
            createInfo.enabledLayerCount = static_cast<uint32_t>(layers.size());
            createInfo.ppEnabledLayerNames = layers.data();
        }
        else{
            throw std::runtime_error("layers requested, but not available!");
        }


        // 尝试创建Vk实例
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

