import std;
import PLZero;


int main(int argc, char* argv[]) {
    // 存储源文件和目标文件路径
    std::string input_file;
    std::string output_file;

    ///////////////////////////////////////////////////////////////////
    //// 读取命令行参数（源文件和目标文件）。
    for(int i = 1; i<argc; ++i){
        if(!std::strcmp(argv[i], "-i")){
            // -i 表示 input 输入文件
            if( ++i >= argc) break;
            input_file = argv[i];
        }
        else if(!std::strcmp(argv[i], "-o")){
            // -o 表示 output 输出文件
            if( ++i >= argc) break;
            output_file = argv[i];
        }
        else if(input_file.empty()) input_file = argv[i]; // 支持省略-i 直接写输入文件
        else if(output_file.empty()) output_file = argv[i]; // 支持省略-o 直接写输出文件
    }
    if(input_file.empty()){
        std::cerr << "请使用 -i 指定源文件， 使用 -o 指定目标文件（可省略）。" << std::endl;
        return 1;
    }
    // 输出文件可以省略，默认为output.txt
    if(output_file.empty()) output_file = "output.txt";
    ///////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////
    //// 开启文件读取流
    PLZero::PlzCompiler compiler(input_file,output_file);
    if(!compiler.is_open()){
        std::cerr << "无法打开文件." << std::endl;
        return 1;
    }
    ///////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////
    //// 语法语义分析 与 目标代码生产
    try{
        compiler.compile();
    }
    catch(const std::exception& e){
        compiler.close();
        ///////////////////////////////////////////////////////////////////
        //// 错误处理，输出错误报告
        try{
            // 删除生成失败的文件
            std::filesystem::remove(output_file);
        }
        catch(...){}
        // 打印错误报告
        std::cerr << "====================================================" << std::endl;
        std::cerr << "编译失败：" << std::endl;
        std::cerr << e.what() << std::endl;
        std::cerr << "====================================================" << std::endl;
        return 1;
        ///////////////////////////////////////////////////////////////////
    }
    ///////////////////////////////////////////////////////////////////
    
    // close()自带关闭检查，不会重复关闭
    compiler.close();
    std::cout << "====================================================" << std::endl;
    std::cout << "编译成功！" << std::endl;
    std::cout << "目标文件：" << output_file << std::endl;
    std::cout << "====================================================" << std::endl;
    
}


