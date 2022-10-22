import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        Tokens.put("string constant", "(\"[^\"]*\")");
        Tokens.put("number", "(0x[A-Fa-f0-9]*)|([\\d]+[.]?[\\d]*)");
        Tokens.put("operator", "(\\+|\\-|\\*|\\/|>=|<=|<>|&&|<<|>>|<|>|==|=|&|%|!=|!|\\.|~|%|\\||\\^|:)");
        Tokens.put("char constant", "('[^']{0,1}')");
        Tokens.put("punctuation", "(\\(|\\)|\\[|\\]|;|,|\\?|\\{|\\})");
        Tokens.put("spaces", "[\n\t\r]");
        Tokens.put("identifier", "([A-z_]+[\\w\\d_]*)");
        Tokens.put("error", "[^\n\t\r\s]+");
        Tokens.put("keyword", "(and)|(auto)|(bool)|(break)|(case)|(catch)|(char)|(class)|(const)|(continue)" +
                "|(decltype)|(default)|(delete)|(do)|(double)|(dynamic_cast)|(else)|(enum)|(explicit)|(extern)" +
                "|(false)|(float)|(for)|(friend)|(goto)|(if)|(inline)|(int)|(long)|(main)|(mutable)|(namespace)" +
                "|(new)|(nullptr)|(operator)|(or)|(private)|(protected)|(public)|(register)|(reinterpret_cast)" +
                "|(return)|(short)|(signed)|(sizeof)|(static)|(static_cast)|(struct)|(switch)|(template)|(this)" +
                "|(throw)|(true)|(try)|(typedef)|(typeid)|(typename)|(union)|(unsigned)|(using)|(virtual)|(void)" +
                "|(volatile)|(while)");
        String[] order = {"string constant", "char constant", "keyword", "number", "macros", "identifier", "punctuation", "operator",   "spaces" , "error"};//

        String lineComment = "//.*[\n\t\r]";
        String realComment = "\\/\\*(|.|\n|\t|\r)+?\\*\\/";
        String include = "#include *(<[^>]+>|\"[^\"]+\")";
        String define = "(#define)( )([A-z_]+[\\w\\d_]*)( ).+\\n*";
        String ifdef = "(#if)n{0,1}(def)( )(|.|\\n|\\r|\\t)+?(#endif)";

        String text = ' ' + new String(Files.readAllBytes(Paths.get("input.txt")) ) + ' ';
        FileWriter fileWriter = new FileWriter("output.txt");

        text = ProcessAndErrase(text, realComment, "comment");
        text = ProcessAndErrase(text, lineComment, "comment");
        text = ProcessAndErrase(text, include, "preprocessor directive");
        text = ProcessAndErrase(text, define, "preprocessor directive");
        text = ProcessAndErrase(text, ifdef, "preprocessor directive");
        if(macros.length() > 0){
            macros = macros.substring(0, macros.length() - 1);
            Tokens.put("macros", macros);
        }

        for(var t : order)
            text = ProcessAndErrase(text, Tokens.get(t), t);

        Collections.sort(tokens);

        for (TokenPos lexeme : tokens){
            if(lexeme.TokenType != "spaces"&&lexeme.TokenType != "сomment") {
                fileWriter.write("< "+lexeme.Token +" >"+ " : " + "< "+lexeme.TokenType +" >"+ "\n");
            }
        }
        fileWriter.close();
    }


    public static List<TokenPos> tokens = new ArrayList<>();
    public static String LengthWord(int length){
        String result = "";
        for (int i=0; i< length; i++){
            result += " ";
        }
        return result;
    }

    public static String ProcessAndErrase(String text, String comment, String type){
        if(comment == null)
            return text;
        Pattern p = Pattern.compile(comment);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String  matchText = m.group();
            int matchIndex = m.start();
            if(type == "number" || type == "identifier" || type == "macros" || type == "keyword"){
                char prev = text.charAt(matchIndex - 1);
                char after = text.charAt(matchIndex + matchText.length());
                if(okay.indexOf(prev) == -1 || okay.indexOf(after) == -1)
                    continue;
            }
            else if(type == "preprocessor directive" && matchText.charAt(1) == 'd'){
                macros += matchText.split(" ")[1] + "|";
            }
            tokens.add(new TokenPos(matchIndex, matchText.trim(), type));
            text = text.substring(0, matchIndex) + LengthWord(matchText.length())
                    + text.substring(matchIndex + matchText.length());
        }
        return text;

    }
    public static Map<String, String> Tokens = new HashMap<>();
    private static String macros = "";
    private static String okay = "\n\s\t\r +-*/&<>=%|^:()[];,?{}";
}