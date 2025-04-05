export module PLZero;

import std;

/**
 * @namespace PLZero
 * @brief PL0编译器专用命名空间
 */
export namespace PLZero{

    constexpr int MAX_NUMBER_SIZE = 14;
    constexpr int MAX_IDENTIFIER_SIZE = 10;
    constexpr int MAX_OPERATOR_SIZE = 2;

    /**
     * @enum TokenType
     * @brief 标识符的类别
     */
    enum class TokenType{
        CALL, /**< call */
        PROCEDURE, /** procedure */
        CONST, /**< const */
        VAR, /**< var */
        BEGIN, /**< begin */
        END, /**< end */
        IF, /**< if */
        THEN, /**< then */
        WHILE, /**< while */
        DO, /**< do */
        ODD, /**< odd */
        ADD, /**< + */
        MINUS, /**< - */
        MULTI, /**< * */
        DIV, /**< / */
        ASSIGN, /** := */
        SHARP, /** # */
        EQUAL, /**< = */
        UNEQUAL, /**< <> */
        LESS, /**< < */
        GREATER, /**< > */
        LEQUAL, /**< <= */
        GEQUAL,/**< >= */
        COMMA, /**< , */
        POINT, /**< . */
        SEMICOLON, /**< ; */
        LBRACKET, /**< ( */
        RBRACKET, /**< ) */
        IDENTIFIER, /** 标识符 */
        NUMBER /**< 整数字面量 */
    };

    /**
     * @enum FuncType
     * @brief 目标程序的指令集
     */
    enum class FuncType{
        lit, /**< lit 0, a : load constant a */
        lod, /**< opr 1, a : load variable 1, a */
        sto, /**< sto 1, a : store variable 1, a */
        opr, /**< opr 0, a : execute operation a */
        cal, /**< cal 1, a : call procedure a at depth 1 a */
        inc, /**< inc 0, a : increment t-register by a */
        jmp, /**< jmp 0, a : jump to a */
        jpc /**< jpc 0, a : jump condition to a */
    };

    /**
     * @brief 存储单个单词信息
     */
    struct Token {
        /**
         * @brief 单词原始字符串名
         */
        std::string name;
        /**
         * @brief 单词类别
         */
        TokenType type;
        /**
         * @brief 单词存储的值
         */
        long long val;
        /**
         * @brief 单词对应的行号
         */
        long long line;
    };

    class Lexer{
    private:
        /**
         * @brief 标记当前词所在行数
         */
        long long line {0};
        /**
         * @brief 暂存输入字符
         */
        char sc {' '};

        /**
         * @brief 源文件输入流
         */
        std::ifstream ifs;

        /**
         * @brief 重置词法分析器
         */
        void reset();
    
    public:
        long long getLine();

        bool is_open();
        /**
         * @brief 开启文件
         */
        bool open(const std::string& str);

        /**
         * @brief 关闭文件，不必要
         */
        void close();

        /**
         * @brief 读取一个词
         */
        Token getToken();
    };

    /**
     * @brief 语法语义分析器 
     */
    class PlzCompiler{
    private:
        Lexer lexer {};
        std::ofstream ofs {};
    public:
        PlzCompiler(const std::string& in_file, const std::string& out_file);
        bool is_open();

        void compile();
        Token blockHandler(Token token);
        Token constHandler(Token token);
        Token varHandler(Token token);
        Token stmtHandler(Token token);
    };

}

module :private;

namespace PLZero{

    bool is_stmt_start_type(TokenType type){
        return type == TokenType::IDENTIFIER || type == TokenType::CALL ||
            type == TokenType::BEGIN || type == TokenType::IF || type == TokenType::WHILE;
    }

    std::string err_msg(long long line,const std::string& msg){
        std::string str = "错误：\n";
        str += std::string {"行号: "} + std::to_string(line) + "\n";
        str += msg;
        return str;
    }

    long long Lexer::getLine(){
        return line;
    }

    void Lexer::reset(){
        if(ifs.is_open()) ifs.close();
        line = 0;
        sc = ' ';
    }

    bool Lexer::is_open(){
        return ifs.is_open();
    }

    bool Lexer::open(const std::string& str){
        reset();
        ifs.open(str);
        if(ifs.is_open()) return true;
        else return false;
    }

    void Lexer::close(){
        if(ifs.is_open()) ifs.close();
    }

    Token Lexer::getToken(){
        // 关键字列表 共28个关键字
        const static std::map<std::string, TokenType> keyword_map = {
            {"call", TokenType::CALL },
            {"procedure" , TokenType::PROCEDURE},
            {"const", TokenType::CONST},
            {"var", TokenType::VAR},
            {"begin", TokenType::BEGIN},
            {"end", TokenType::END},
            {"if", TokenType::IF},
            {"then", TokenType::THEN},
            {"while", TokenType::WHILE},
            {"do", TokenType::DO},
            {"odd", TokenType::ODD},
            {"+", TokenType::ADD},
            {"-", TokenType::MINUS},
            {"*", TokenType::MULTI},
            {"/", TokenType::DIV},
            {":=", TokenType::ASSIGN},
            {"#", TokenType::SHARP},
            {"=", TokenType::EQUAL},
            {"<>", TokenType::UNEQUAL},
            {"<", TokenType::LESS},
            {">", TokenType::GREATER},
            {"<=", TokenType::LEQUAL},
            {">=", TokenType::GEQUAL},
            {",", TokenType::COMMA},
            {".", TokenType::POINT},
            {";", TokenType::SEMICOLON},
            {"(", TokenType::LBRACKET},
            {")", TokenType::RBRACKET}
        };
        // 跳过空格字符 获取到第一个有效字符
        while( std::isspace(sc) ){
            if(sc == '\n') ++line;
            ifs.get(sc);
            if(ifs.fail()) throw std::runtime_error {err_msg(line, "不完整的程序，未正常中止。")};
        }

        Token res;
        res.line = line;
        std::string str;
        str += sc;
        // 根据首字符类型，进行不同的处理
        if(std::isdigit(sc)){
            // 首字符是数字 必然是 整数字面量
            while(true){
                ifs.get(sc);
                if(ifs.fail()) throw err_msg(line, "不完整的程序，未正常中止。");
                if(std::isdigit(sc)){
                    str += sc;
                    if(str.size() > MAX_NUMBER_SIZE){
                        throw  std::runtime_error {err_msg(line, "数值长度溢出，最多 " + std::to_string(MAX_NUMBER_SIZE) + " 位。")};
                    }
                }
                else break;
            }
            res.type = TokenType::NUMBER;
            res.val = std::stoll(str);
        }
        else if(std::isalpha(sc)){
            // 首字符是字母 可能是标识符，也可能是关键字
            while(true){
                ifs.get(sc);
                if(ifs.fail()) throw err_msg(line, "不完整的程序，未正常中止。");
                if(std::isdigit(sc) || std::isalpha(sc)){
                    str += sc;
                    if(str.size() > MAX_IDENTIFIER_SIZE){
                        throw  std::runtime_error {err_msg(line, "标识符长度溢出，最多 " + std::to_string(MAX_IDENTIFIER_SIZE) + " 位。")};
                    }
                }
                else break;
            }
            if(keyword_map.find(str) != keyword_map.end()){
                res.type = keyword_map.at(str);
            }
            else res.type = TokenType::IDENTIFIER;
            res.name = str;
        }
        else{
            // 特殊非空格符号，必然是 算术运算符 或 范围标点
            while(keyword_map.find(str) == keyword_map.end()){
                ifs.get(sc);
                if(ifs.fail()) throw std::runtime_error {err_msg(line, "不完整的程序，未正常中止。")};
                str += sc;
                if(str.size() > MAX_OPERATOR_SIZE) throw std::runtime_error {err_msg(line, "未知算术符。")};
            }
            res.type = keyword_map.at(str);
            res.name = str;
            // 获取到.之后，不会再输入
            if(str != ".") ifs.get(sc);
        }

        return res;
    }

    PlzCompiler::PlzCompiler(const std::string& in_file, const std::string& out_file){
        lexer.open(in_file);
        ofs.open(out_file);
    }

    bool PlzCompiler::is_open(){
        return lexer.is_open() && ofs.is_open();
    }

    void PlzCompiler::compile(){
        Token token = lexer.getToken();
        token = blockHandler(token);
        if(token.type != TokenType::POINT){
            throw std::runtime_error {err_msg(lexer.getLine(),"未知错误，未以 . 符号结束解析。")};
        }
    }

    Token PlzCompiler::blockHandler(Token token){
        if(token.type == TokenType::CONST){
            token = constHandler(token);
        }
        if(token.type == TokenType::VAR){
            token = varHandler(token);
        }
        while(token.type == TokenType::PROCEDURE){
            // 获取procedure 变量名
            token = lexer.getToken();
            if(token.type != TokenType::IDENTIFIER) throw std::runtime_error { err_msg(lexer.getLine(), "非法procedure标识符。") };
            // TODO: 处理变量名
            if(lexer.getToken().type != TokenType::SEMICOLON) throw std::runtime_error { err_msg(lexer.getLine(), "procedure标识符后未接分号。") };
            token = lexer.getToken();
            token = blockHandler(token);
        }
        token = stmtHandler(token);
        return token;
    }
    Token PlzCompiler::constHandler(Token token){
        return token;
    }
    Token PlzCompiler::varHandler(Token token){
        return token;
    }
    Token PlzCompiler::stmtHandler(Token token){
        if(!is_stmt_start_type(token.type)) return token;
        // TODO: 处理各种stmt情况
        return token;
    }

}

