export module PLZero;

import std;

/**
 * @namespace PLZero
 * @brief PL0编译器专用命名空间
 */
export namespace PLZero{

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
        /**
         * @brief 单词是所在行的第几个词
         */
        long long index;
    };

    class Lexer{
    private:
        /**
         * @brief 标记当前词所在行数
         */
        long long line {0};

        /**
         * @brief 标记当前词所在列数（是当前行第几个单词）
         */
        long long index {0};

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

}

module :private;

namespace PLZero{
    void Lexer::reset(){
        if(ifs.is_open()) ifs.close();
        line = 0;
        index = 0;
        sc = ' ';
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
            if(sc == '\n'){
                ++line;
                index = 0;
            }
            ifs.get(sc);
        }
        Token res;
        res.line = line;
        res.index = index;
        std::string str;
        str += sc;
        // 根据首字符类型，进行不同的处理
        if(std::isdigit(sc)){
            while(ifs.get(sc)){
                if(std::isdigit(sc)){
                    str += sc;
                }
                else break;
            }
            res.type = TokenType::NUMBER;
            res.val = std::stoll(str);
        }
        else if(std::isalpha(sc)){
            while(ifs.get(sc)){
                if(std::isdigit(sc) || std::isalpha(sc)){
                    str += sc;
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
            while(keyword_map.find(str) == keyword_map.end()){
                ifs.get(sc);
                str += sc;
            }
            res.type = keyword_map.at(str);
            res.name = str;
            // 获取到.之后，不会再输入
            if(str != ".") ifs.get(sc);
        }
        ++index;
        return res;
    }

}

