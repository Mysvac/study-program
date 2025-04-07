import std;
import PLZero;

using namespace PLZero;

int main(int argc, char* argv[]) {
    // 存储源文件和目标文件路径
    std::string input_file;

    ///////////////////////////////////////////////////////////////////
    //// 读取命令行参数（源文件和目标文件）。
    for(int i = 1; i<argc; ++i){
        if(!std::strcmp(argv[i], "-i")){
            // -i 表示 input 输入文件
            if( ++i >= argc) break;
            input_file = argv[i];
        }
        else if(input_file.empty()) input_file = argv[i]; // 支持省略-i 直接写输入文件
    }
    if(input_file.empty()){
        std::cout << "请输入源文件路径：" << std::flush;
        std::cin >> input_file;
        return 1;
    }
    ///////////////////////////////////////////////////////////////////
    // 关键字列表 共28个关键字
    const static std::map<TokenType, std::string> type_map = {
        {TokenType::CALL , "基本字" },
        { TokenType::PROCEDURE, "基本字"},
        {TokenType::CONST,"基本字"},
        {TokenType::VAR,"基本字"},
        {TokenType::BEGIN,"基本字"},
        {TokenType::END,"基本字"},
        {TokenType::IF,"基本字"},
        {TokenType::THEN,"基本字"},
        {TokenType::WHILE,"基本字"},
        {TokenType::DO,"基本字"},
        {TokenType::ODD,"基本字"},
        {TokenType::ADD,"算术符"},
        {TokenType::MINUS,"算术符"},
        {TokenType::MULTI,"算术符"},
        {TokenType::DIV,"算术符"},
        {TokenType::ASSIGN,"算术符"},
        {TokenType::UNEQUAL,"算术符"},
        {TokenType::EQUAL,"算术符"},
        {TokenType::UNEQUAL,"算术符"},
        {TokenType::LESS,"算术符"},
        {TokenType::GREATER,"算术符"},
        {TokenType::LEQUAL,"算术符"},
        {TokenType::GEQUAL,"算术符"},
        {TokenType::COMMA,"界符"},
        {TokenType::POINT,"界符"},
        {TokenType::SEMICOLON,"界符"},
        {TokenType::LBRACKET,"界符"},
        {TokenType::RBRACKET,"界符"},
        {TokenType::IDENTIFIER,"标识符"},
        {TokenType::NUMBER,"常数 - 整数字面量"}
    };

    Lexer lexer;
    lexer.open(input_file);
    if(!lexer.is_open()){
        std::cerr << "无法正常打开文件：" << input_file << std::endl;
        return 1;
    }

    try{
        Token token;
        do{
            token = lexer.getToken();
            std::cout << "line: " << lexer.getLine() << " : " << (token.type == TokenType::NUMBER ? std::to_string(token.val) : token.name) << " : " << type_map.at(token.type) << std::endl;
        }
        while(token.type != TokenType::POINT);
    }
    catch(const std::exception& e){
        std::cerr << e.what() << std::endl;
        return 1;
    }

    std::cout << "----------结束----------" << std::endl;


}


