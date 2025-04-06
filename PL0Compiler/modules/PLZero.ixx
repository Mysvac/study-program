export module PLZero;

import std;

/**
 * @namespace PLZero
 * @brief PL0编译器专用命名空间
 */
export namespace PLZero{

    /**
     * @brief 整型字面量最大长度
     */
    constexpr int MAX_NUMBER_SIZE = 14;

    /**
     * @brief 标识符最大长度
     */
    constexpr int MAX_IDENTIFIER_SIZE = 10;

    /**
     * @brief 非字母开头关键字最大长度
     */
    constexpr int MAX_OPERATOR_SIZE = 2;

    /**
     * @enum TokenType
     * @brief 标识符的类别
     */
    enum class TokenType{
        CALL, /**< call */
        PROCEDURE, /**< procedure */
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
        EQUAL, /**< = */
        UNEQUAL, /**< <> 和 # */
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
     * @enum ItemType
     * @brief 标识符的类别-符号表专用
     */
    enum class ItemType{
        PROCEDURE, /**< procedure 标识符 */
        CONST, /**< const 标识符 */
        VAR /**< var 标识符 */
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

    /**
     * @brief 词法分析器
     */
    class Lexer{
    private:
        /**
         * @brief 标记当前词所在行数
         */
        long long line {1};
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
        /**
         * @brief 获取当前所在行数
         */
        long long getLine();

        /**
         * @brief 检测文件是否成功开启
         */
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
     * @brief 符号表中的项
     */
    struct Item{
        /**
         * @brief 标识符名
         */
        std::string name;
        /**
         * @brief 标识符类型
         */
        ItemType type;
        /**
         * @brief 标识符所在的递归深度
         */
        long long level {0};
        /**
         * @brief 标识符相对地址 或 字面量
         */
        long long addr {0};
        /**
         * @brief 过程需要的栈区空间
         */
        long long size {0};
    };

    /**
     * @brief 语法语义分析与代码生成器 
     */
    class PlzCompiler{
    private:
        /**
         * @brief 暂存输出语句
         * @details 
         * jmp语句在stmt语句之前输出
         * 但是具体要调到哪里，要处理完stmt之后才知道
         * 只能使用缓存，暂存语句
         */
        struct tmp_string{
            std::string str;
            bool is_jmp {false};
            long long dis{0};
            tmp_string(std::string str_in): str(str_in){};
            tmp_string(std::string str_in, long long dis_in): str(str_in), dis(dis_in), is_jmp(true){};
        };
        /**
         * @brief 词法分析器 分词器
         */
        Lexer lexer {};
        /**
         * @brief 输出文件流
         */
        std::ofstream ofs {};
        /**
         * @brief 总缓冲区
         */
        std::stringstream totalBuffer{};
        /**
         * @brief 符号表
         */
        std::vector<Item> table;
        /**
         * @brief 暂存直接输出的数据
         */
        std::vector<tmp_string> out_buffer;
        /**
         * @brief 输出行数计数器
         */
        long long out_cnt {1};
        /**
         * @brief 主函数所在位置
         */
        long long main_line {1};
        /**
         * @brief 当前递归层次
         */
        long long level {0};
        /**
         * @brief 相对偏移量
         * @details 
         * 3 + 当前函数定义的局部变量个数
         * 默认值3
         * 因为每个函数，需要 3 个基础空间
         * 用于存放 返回指针 静态链 动态链
         */
        long long dx {3};
        /**
         * @brief 控制是否开启注释
         */
        bool add_comment {false};
    public:
        PlzCompiler() = default;
        /**
         * @brief 构造函数
         * @param in_file 输入文件（源文件）的路径
         * @param out_file 输出文件（目标文件）的路径
         */
        PlzCompiler(const std::string& in_file, const std::string& out_file);

        /**
         * @brief 设置是否开启注释
         */
        void set_comment(bool in_com);

        /**
         * @brief 检测是否成功开启文件读写
         */
        bool is_open();
        /**
         * @brief 打开输入输出文件
         */
        bool open(const std::string& in_file, const std::string& out_file);
        /**
         * @brief 关闭保存文件读写连接
         */
        void close();

        /**
         * @brief 开始编译
         * @details
         * Program  → Block . 
         */
        void compile();

        /**
         * @brief 块作用域处理函数
         * @details
         * Block  → [ConstDecl] [VarDecl][ProcDecl] Stmt
         */
        Token blockHandler(Token token, long long procedure_idx = -1);

        /**
         * @brief procedure处理函数
         * @details
         * ProcDecl  → procedure ident ; Block ; {procedure ident ; Block ;}
         */
        Token procedureHandler(Token token);

        /**
         * @brief 常量声明处理函数
         * @details
         * ConstDecl → const ConstDef {, ConstDef} ;
         * ConstDef  → ident = number 
         */
        Token constHandler(Token token);

        /**
         * @brief 变量声明处理函数
         * @details
         * VarDecl  → var ident {, ident} ; 
         */
        Token varHandler(Token token);

        /**
         * @brief 语句处理函数
         * @details
         * Stmt   → ident := Exp | call ident | begin Stmt {; Stmt} end |  if Cond then Stmt | while Cond do Stmt | ε
         */
        Token stmtHandler(Token token);

        /**
         * @brief 条件处理函数
         * @details
         * Cond  → odd Exp | Exp RelOp Exp 
         * RelOp  → = | <> | < | > | <= | >=
         */
        Token condHandler(Token token);

        /**
         * @brief 表达式处理函数
         * @details
         * Exp   → [+ | − ] Term {+ Term | − Term} 
         */
        Token expressHandler(Token token);

        /**
         * @brief 项处理函数
         * @details
         * Term  → Factor {∗ Factor | / Factor} 
         */
        Token termHandler(Token token);

        /**
         * @brief 因子处理函数
         * @details
         * Factor  → ident | number | ( Exp ) 
         */
        Token factorHandler(Token token);
    };

}

// 模块私有片段 用于写定义（实现）
module :private;

namespace PLZero{

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
        line = 1;
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
            {"#", TokenType::UNEQUAL},
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
                if(str.size() > MAX_OPERATOR_SIZE) throw std::runtime_error {err_msg(line, "未知算术符：" + str)};
            }
            while(keyword_map.find(str) != keyword_map.end()){
                // 使用 “最长可匹配算术符” 方法
                if(str == "."){ // 如果匹配到结束符号，直接退出
                    str += " "; // 因为最后会删减1位，所以这里加上1位空格
                    break;
                }
                ifs.get(sc);
                str += sc;
            }
            // 去除最后一位
            res.type = keyword_map.at(str.substr(0, str.size()-1));
            res.name = str.substr(0, str.size()-1);
        }

        return res;
    }

    void PlzCompiler::set_comment(bool in_comment){
        add_comment = in_comment;
    }

    PlzCompiler::PlzCompiler(const std::string& in_file, const std::string& out_file){
        lexer.open(in_file);
        ofs.open(out_file);
    }
    bool PlzCompiler::open(const std::string& in_file, const std::string& out_file){
        close();
        lexer.open(in_file);
        ofs.open(out_file);
        return lexer.is_open() && ofs.is_open();
    }
    bool PlzCompiler::is_open(){
        return lexer.is_open() && ofs.is_open();
    }
    void PlzCompiler::close(){
        if(ofs.is_open()) ofs.close();
        if(lexer.is_open()) lexer.close();
        table.clear();
        out_buffer.clear();
        totalBuffer.clear();
        main_line = 1;
        out_cnt = 1;
        level = 0;
        dx = 3;
    }

    void PlzCompiler::compile(){
        if(!is_open()) throw std::runtime_error {"未打开文件，无法编译。"};
        Token token = lexer.getToken();
        token = blockHandler(token);
        if(token.type != TokenType::POINT){
            throw std::runtime_error {err_msg(lexer.getLine(),"未知错误，未以 . 符号结束解析。")};
        }
        // 调用主函数
        std::println(ofs, "{:5} cal  {:3} {}", 1, 0, main_line);
        ofs << totalBuffer.rdbuf();
        totalBuffer.clear();
        ofs.close();
    }

    Token PlzCompiler::blockHandler(Token token, long long procedure_idx){
        // 注释用的数据，函数名和深度
        std::string block_name = "main";
        long long block_level = 0;
        if(!table.empty()){
            block_name = table.rbegin()->name;
            block_level = table.rbegin()->level;
        }

        // 常量声明处理
        if(token.type == TokenType::CONST){
            token = constHandler(token);
        }
        // 变量声明处理
        if(token.type == TokenType::VAR){
            token = varHandler(token);
        }
        // 函数（过程）声明处理
        while(token.type == TokenType::PROCEDURE){
            token = procedureHandler(token);
        }

        // 如果启用了add_comment则添加注释，用于区分不同的块
        if(add_comment) std::println(totalBuffer, "# -----注释 函数名: {}  层级: {} ------", block_name, block_level);
        // 输出语句，开辟栈空间
        std::println(totalBuffer, "{:5} int  {:3} {}", ++out_cnt, 0, dx);
        // 赋值size
        if(procedure_idx >=0) table[procedure_idx].addr = out_cnt;
        else main_line = out_cnt;

        // stmt语句块处理
        token = stmtHandler(token);


        // 处理缓存，输出语句到目标文件，主要是处理jmp语句的目标位置
        for(auto& it : out_buffer){
            ++out_cnt;
            if(it.is_jmp) std::println(totalBuffer, "{:5} {} {}", out_cnt, it.str, it.dis + out_cnt );
            else std::println(totalBuffer, "{:5} {}", out_cnt, it.str);
        }
        out_buffer.clear();

        // 输出语句，清除栈空间
        std::println(totalBuffer,"{:5} opr  {:3} {}", ++out_cnt, 0,  0);
        if(add_comment) std::println(totalBuffer, "# -----------------------------------", block_name, block_level);

        return token;
    }
    Token PlzCompiler::procedureHandler(Token token){
            // 新建静态标识符信息，准备加入符号表中
            Item item;
            item.type = ItemType::PROCEDURE; // 类型是process的标识符
            item.level = level; // 递归层次是level

            ++level; // 内部内容递归层次需要 +1
            long long tdx = dx; // 暂时存储上一层的栈区大小
            dx = 3; // 初始前3个位置，留给返回地址 静态链 动态链

            // 获取procedure 变量名
            token = lexer.getToken();
            if(token.type != TokenType::IDENTIFIER) throw std::runtime_error { err_msg(lexer.getLine(), "非法procedure标识符：" + token.name + "。") };

            // procedure 需要确保在stmt语句之前加入表中，不能留在后面加
            item.name = token.name;
            table.push_back(item);
            // 提前加了，但是缺少size参数，只能保留索引，后续赋值size
            int idx =table.size()-1;

            if(lexer.getToken().type != TokenType::SEMICOLON) throw std::runtime_error { err_msg(lexer.getLine(), "procedure标识符 " + token.name +" 后未接分号。") };
            token = lexer.getToken();
            token = blockHandler(token, idx);
            if(token.type != TokenType::SEMICOLON) throw std::runtime_error { err_msg(lexer.getLine(), "block作用域未以分号结尾") };
            token = lexer.getToken();

            // 恢复level 和 dx
            dx = tdx;
            --level;
            return token;
    }
    Token PlzCompiler::constHandler(Token token){
        while(true){
            // 新建静态标识符信息，准备加入符号表中
            Item item;
            item.type = ItemType::CONST; // 类型是const
            item.level = level; // 递归层次是level 常量不需要空间

            // 获取标识符名称
            token = lexer.getToken();
            if(token.type != TokenType::IDENTIFIER){
                throw std::runtime_error {err_msg(lexer.getLine(),"const声明中存在非法标识符：" + token.name)};
            }
            // 符号表中的名称和token名称相同
            item.name = token.name;

            // 获取等于号 =
            token = lexer.getToken();
            if(token.type != TokenType::EQUAL){
                throw std::runtime_error {err_msg(lexer.getLine(),"const声明中存在非法赋值运算符：" + token.name)};
            }
            // 获取整数字面量
            token = lexer.getToken();
            if(token.type != TokenType::NUMBER){
                throw std::runtime_error {err_msg(lexer.getLine(),"const声明中存在非法整数字面量：" + token.name)};
            }

            // 将常量存入表中
            item.addr = token.val; // 常量，地址位直接存放值
            table.push_back(item);
            // 编译期常量 直接替换 不具备栈空间，所以不需要生成目标语句

            token = lexer.getToken(); // 获取下个值， 要么是 , 要么是 ;
            if(token.type == TokenType::SEMICOLON) break;
            else if(token.type != TokenType::COMMA) throw std::runtime_error {err_msg(lexer.getLine(),"const声明中存在未知符号：" + token.name)};
        }
        // 跳过分号
        return lexer.getToken();
    }
    Token PlzCompiler::varHandler(Token token){
        while(true){
            // 新建静态标识符信息，准备加入符号表中
            Item item;
            item.type = ItemType::VAR; // 类型是const
            item.level = level; // 递归层次是level
            item.addr = dx;
            ++dx;

            token = lexer.getToken();
            if(token.type != TokenType::IDENTIFIER){
                throw std::runtime_error {err_msg(lexer.getLine(),"var声明中存在非法标识符：" + token.name)};
            }
            item.name = token.name;
            table.push_back(item);

            token = lexer.getToken();
            if(token.type == TokenType::SEMICOLON) break;
            else if(token.type != TokenType::COMMA) throw std::runtime_error {err_msg(lexer.getLine(),"const声明中存在未知符号：" + token.name)};
        }
        return lexer.getToken();
    }
    Token PlzCompiler::stmtHandler(Token token){
        switch (token.type){
        case TokenType::IDENTIFIER:
            {
                // 检查右侧是不是赋值运算
                if(lexer.getToken().type != TokenType::ASSIGN){
                    throw std::runtime_error {err_msg(lexer.getLine(),"变量 " + token.name + " 右侧非法变量赋值运算符")};
                }
                // 寻找对应的标识符
                long long t_level = level;
                auto it = table.rbegin();
                for(;it!=table.rend();++it){
                    // 寻找时，深度只能不断变低，防止找到 不是当前的祖先的procedure 定义的同名变量
                    // lev < it->level 说明 it 是其他非祖先procedure中定义的变量，应该跳过
                    if(t_level < it->level) continue;
                    if(t_level > it->level) t_level = it->level;

                    if(it->name == token.name){
                        // 找到同名标识符，检查类型是否是变量
                        if(it->type != ItemType::VAR) throw std::runtime_error {err_msg(lexer.getLine(),"标识符 " + token.name +" 最近的声明，并非变量，无法赋值。")};

                        // 找到正确标识符，则处理右边的表达式。表达式的值会存入栈顶
                        token = expressHandler(lexer.getToken());
                        // 暂存在缓冲器，因为可能涉及到跳转语句
                        out_buffer.push_back( tmp_string(std::format("sto  {:3} {}", level - it->level,  it->addr)) );
                        break;
                    }
                }
                if(it == table.rend()) throw std::runtime_error {err_msg(lexer.getLine(),"未找到标识符 "+ token.name +" 的声明。")};
            }
            break;
        case TokenType::CALL:
            {
                token = lexer.getToken();
                if(token.type != TokenType::IDENTIFIER){
                    throw std::runtime_error {err_msg(lexer.getLine(),"call 右侧不是合法标识符")};
                }
                // 寻找对应的标识符 基本和上面赋值运算相同
                long long t_level = level;
                auto it = table.rbegin();
                for(;it!=table.rend();++it){
                    // 寻找时，深度只能不断变低，防止找到 不是当前的祖先的procedure 定义的同名变量
                    // lev < it->level 说明 it 是其他非祖先procedure中定义的变量，应该跳过
                    if(t_level < it->level) continue;
                    if(t_level > it->level) t_level = it->level;

                    if(it->name == token.name){
                        // 找到同名标识符，检查类型是否是过程
                        if(it->type != ItemType::PROCEDURE) throw std::runtime_error {err_msg(lexer.getLine(),"标识符 " + token.name +" 最近的声明，并非过程，无法调用。")};
                        // 这里没有调用子程序，所以需要主动读一个词
                        token = lexer.getToken();
                        // 读栈顶，存入对应变量中
                        out_buffer.push_back( tmp_string(std::format("cal  {:3} {}", level - it->level,  it->addr)) );
                        break;
                    }
                }
                if(it == table.rend()) throw std::runtime_error {err_msg(lexer.getLine(),"未找到标识符 "+ token.name +" 的声明。")};
            }
            break;
        case TokenType::BEGIN:
            {
                token = lexer.getToken();
                token = stmtHandler(token);
                // 如果是分号，则后面应该还有 stmt
                while(token.type == TokenType::SEMICOLON){
                    token = stmtHandler(lexer.getToken());
                }
                // 必须以end结尾
                if(token.type != TokenType::END){
                    throw std::runtime_error {err_msg(lexer.getLine(),"begin块未匹配到对应的end。")};
                }
                token = lexer.getToken();
            }
            break;
        case TokenType::IF:
            {
                token = lexer.getToken();
                token = condHandler(token);
                if(token.type != TokenType::THEN){
                    throw std::runtime_error {err_msg(lexer.getLine(),"if语句未匹配到对应的then。")};
                }
                out_buffer.push_back( tmp_string(std::format("jpc  {:3}", 0)) );
                size_t idx = out_buffer.size()-1;
                token = lexer.getToken();
                token = stmtHandler(token);
                out_buffer[idx].is_jmp = true;
                out_buffer[idx].dis = out_buffer.size() - idx;
            }
            break;
        case TokenType::WHILE: 
            {
                token = lexer.getToken();
                size_t ret = out_buffer.size();
                token = condHandler(token);
                if(token.type != TokenType::DO){
                    throw std::runtime_error {err_msg(lexer.getLine(),"if语句未匹配到对应的then。")};
                }
                out_buffer.push_back( tmp_string(std::format("jpc  {:3}", 0)) );
                size_t idx = out_buffer.size()-1;
                token = lexer.getToken();
                token = stmtHandler(token);
                size_t len = out_buffer.size();
                out_buffer.push_back( tmp_string(std::format("jmp  {:3}", 0), ret - len) );

                out_buffer[idx].is_jmp = true;
                out_buffer[idx].dis = out_buffer.size() - idx; // 相对移动距离
            }
            break;
        }
        // 什么都不是，认为是空内容，直接会直接结束返回
        return token;
    }
    Token PlzCompiler::condHandler(Token token){
        if(token.type == TokenType::ODD){
            token =  expressHandler(lexer.getToken());
            out_buffer.push_back( tmp_string(std::format("opr  {:3} {}", 0,  6)) ); // 6是奇偶数判断
        }
        else{
            token =  expressHandler(token);
            switch (token.type)
            {
            case TokenType::EQUAL:
                token = expressHandler(lexer.getToken());
                out_buffer.push_back( tmp_string(std::format("opr  {:3} {}", 0,  8)) ); // 假设 8-13 分别是 = <> < > <= >=
                break;
            case TokenType::UNEQUAL:
                token = expressHandler(lexer.getToken());
                out_buffer.push_back( tmp_string(std::format("opr  {:3} {}", 0,  9)) );
                break;
            case TokenType::LESS:
                token = expressHandler(lexer.getToken());
                out_buffer.push_back( tmp_string(std::format("opr  {:3} {}", 0,  10)) );
                break;
            case TokenType::GREATER:
                token = expressHandler(lexer.getToken());
                out_buffer.push_back( tmp_string(std::format("opr  {:3} {}", 0,  11)) );
                break;
            case TokenType::LEQUAL:
                token = expressHandler(lexer.getToken());
                out_buffer.push_back( tmp_string(std::format("opr  {:3} {}", 0,  12)) );
                break;
            case TokenType::GEQUAL:
                token = expressHandler(lexer.getToken());
                out_buffer.push_back( tmp_string(std::format("opr  {:3} {}", 0,  13)) );
                break;
            default:
                throw std::runtime_error {err_msg(lexer.getLine(),"不合法的比较运算符。")};
                break;
            }
        }
        return token;
    }
    Token PlzCompiler::expressHandler(Token token){
        if(token.type == TokenType::ADD || token.type == TokenType::MINUS){
            bool is_minus = token.type == TokenType::MINUS;
            token = termHandler(lexer.getToken());
            if(is_minus){
                out_buffer.push_back( tmp_string(std::format("opr  {:3} {}", 0,  1)) ); // 1是栈顶取反
            }
        }
        else{
            token = termHandler(token);
        }
        while(token.type == TokenType::ADD || token.type == TokenType::MINUS){
            bool is_minus = token.type == TokenType::MINUS;
            token = termHandler(lexer.getToken());
            if(is_minus){
                out_buffer.push_back( tmp_string(std::format("opr  {:3} {}", 0,  3)) ); // 2-5 分别是 加减乘除
            }
            else{
                out_buffer.push_back( tmp_string(std::format("opr  {:3} {}", 0,  2)) ); // 2-5 分别是 加减乘除
            }
        }
        return token;
    }
    Token PlzCompiler::termHandler(Token token){
        token = factorHandler(token);
        while (token.type == TokenType::MULTI || token.type == TokenType::DIV){
            if(token.type == TokenType::MULTI){
                token = factorHandler(lexer.getToken());
                out_buffer.push_back( tmp_string(std::format("opr  {:3} {}", 0,  4)) ); // 2-5 分别是 加减乘除
            }
            else{
                token = factorHandler(lexer.getToken());
                out_buffer.push_back( tmp_string(std::format("opr  {:3} {}", 0,  5)) ); // 2-5 分别是 加减乘除
            }
        }
        return token;
    }
    Token PlzCompiler::factorHandler(Token token){
        switch (token.type)
        {
        case TokenType::IDENTIFIER:
            {
                long long t_level = level;
                auto it = table.rbegin();
                for(;it!=table.rend();++it){
                    // 寻找时，深度只能不断变低，防止找到 不是当前的祖先的procedure 定义的同名变量
                    // lev < it->level 说明 it 是其他非祖先procedure中定义的变量，应该跳过
                    if(t_level < it->level) continue;
                    if(t_level > it->level) t_level = it->level;

                    if(it->name == token.name){
                        // 找到同名标识符，检查类型是否是变量
                        if(it->type != ItemType::VAR && it->type != ItemType::CONST) throw std::runtime_error {err_msg(lexer.getLine(),"标识符 " + token.name +" 最近的声明，并非变量，无法读取。")};

                        // 这里没有调用子程序，所以需要主动读一个词
                        token = lexer.getToken();
                        // 变量 或者 常量，进行不同的输出
                        if(it->type == ItemType::VAR) out_buffer.push_back( tmp_string(std::format("lod  {:3} {}", level - it->level,  it->addr)) );
                        else out_buffer.push_back( tmp_string(std::format("lit  {:3} {}", 0,  it->addr)) );
                        break;
                    }
                }
                if(it == table.rend()) throw std::runtime_error {err_msg(lexer.getLine(),"未找到标识符 " + token.name +" 的声明。")};
            }
            break;
        case TokenType::NUMBER:
            {
                out_buffer.push_back( tmp_string(std::format("lit  {:3} {}", 0,  token.val)) );
                token = lexer.getToken();
            }
            break;
        case TokenType::LBRACKET:
            {
                long long t_ling = lexer.getLine();
                token = expressHandler(lexer.getToken());
                if(token.type != TokenType::RBRACKET){
                    throw std::runtime_error {err_msg(t_ling,"未找到对应的右括号")};
                }
                token = lexer.getToken();
            }
            break;
        default:
            throw std::runtime_error {err_msg(lexer.getLine(),"未知因子。")};
            break;
        }
        return token;
    }

}

