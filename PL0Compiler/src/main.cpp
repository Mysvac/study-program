import std;
import PLZero;

auto main(int argc, char* argv[]) ->int {
    bool have_input = false;
    bool have_output = false;

    // 存储源文件和目标文件路径
    std::string input_file;
    std::string output_file;

    // 读取源文件和目标文件路径
    for(int i = 1; i<argc; ++i){
        if(std::string {argv[i]} == "-i"){
            ++i;
            if(i >= argc) return 1;
            have_input = true;
            input_file = argv[i];
        }
        else if(std::string {argv[i]} == "-o"){
            ++i;
            if(i >= argc) return 1;
            have_output = true;
            output_file = argv[i];
        }
        else{
            if(!have_input){
                have_input = true;
                input_file = argv[i];
            }
            else{
                have_output = true;
                output_file = argv[i];
            }
        }
        if(have_input && have_output) break;
    }
    if(have_input && !have_output){
        output_file = "output.txt";
    }
    else return 1;

    // 开启词法分析器
    PLZero::Lexer lexer;
    lexer.open(input_file);

    while(true){
        PLZero::Token token = lexer.getToken();
        if(token.type == PLZero::TokenType::NUMBER) std::println("{}", token.val);
        else std::println("{}", token.name);
        if(token.type==PLZero::TokenType::POINT) break;
    }
}
