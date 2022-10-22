import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    public static Map<String, String> Tokens = new HashMap<>();
    private static final String[]order = {"comment", "preprocessor directive", "string constant", "char constant", "keyword",
            "number", "process", "macros", "identifier", "punctuation", "operator",   "spaces" , "error"};
    private  String macros = "";
    private String text;
    public List<TokenPos> tokens = new ArrayList<>();

    private String LengthWord(int length){
        return " ".repeat(Math.max(0, length));
    }
    private void ProcessAndErrase(String comment, String type){
        if(Objects.equals(type, "process")){
            if(macros.length() > 0){
                macros = macros.substring(0, macros.length() - 1);
                Tokens.put("macros", macros);
            }
            return;
        }
        if(comment == null)
            return;
        Pattern p = Pattern.compile(comment);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String  matchText = m.group();
            int matchIndex = m.start();
            if(type == "number" || type == "identifier" || type == "macros" || type == "keyword"){
                char prev = text.charAt(matchIndex - 1);
                char after = text.charAt(matchIndex + matchText.length());
                String okay = "\n\s\t\r +-*/&<>=%|^:()[];,?{}";
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

    }

    Tokenizer(String source) throws IOException {
        text = ' ' + new String(Files.readAllBytes(Paths.get(source)) ) + ' ';
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
        Tokens.put("comment", "\\/\\*(|.|\n|\t|\r)+?\\*\\/|//.*[\\n\\t\\r]");
        Tokens.put("preprocessor directive", "#include *(<[^>]+>|\"[^\"]+\")" +
                "|#define ([A-z_]+[\\w\\d_]*)( ).+\\n*|#ifn{0,1}def (|.|\\n|\\r|\\t)+?#endif");
        Tokens.put("process", "");
    }

    public void ExtractTokens(){
        for(var t : order)
            ProcessAndErrase(Tokens.get(t), t);
        Collections.sort(tokens);
    }

    public void PrintRes(String output) throws IOException {
        FileWriter fileWriter = new FileWriter(output);
        for (TokenPos lexeme : tokens){
            if(lexeme.TokenType != "spaces"&&lexeme.TokenType != "сomment") {
                fileWriter.write("< "+lexeme.Token +" >"+ " : " + "< "+lexeme.TokenType +" >"+ "\n");
            }
        }
        fileWriter.close();
    }

}
