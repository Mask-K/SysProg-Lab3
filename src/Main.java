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
        Tokens.put("error", "[^ї ]]*([А-Яа-ій-я\\w\\d\\;\"]*)");

        String[] order = {"string constant", "char constant", "number", "identifier", "punctuation", "operator",   "spaces", "error"};
        String lineComment = "//.*[\n\t\r]";
        String realComment = "\\/\\*(|.|\n|\t|\r)+?\\*\\/";

        String include = "(#include)( )*(<[^>]+>|\"[^\"]+\")";
        String define = "(#define)";

        String text1 = new String(Files.readAllBytes(Paths.get("input.txt")) );
        String text = "ї" + text1 + "ї";
        FileWriter fileWriter = new FileWriter("output.txt");

        text = ProcessAndErrase(text, realComment, "comment");
        text = ProcessAndErrase(text, lineComment, "comment");
        text = ProcessAndErrase(text, include, "preprocessor directive");

        for( String keyword: keyWords){
            boolean flag = true;
            while(flag){
                int index = text.indexOf(keyword);
                if (index != -1){
                    tokens.add(new TokenPos(index, text.substring(index, index + keyword.length()), "keyword"));
                    text = text.substring(0, index) + LengthWord(keyword.length())+ text.substring(index + keyword.length());
                } else {
                    flag = false;
                }
            }
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
            result += "ї";
        }
        return result;
    }

    public static String ProcessAndErrase(String text, String comment, String type){
        Pattern p = Pattern.compile(comment);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String  matchText = m.group();
            int matchIndex = m.start();
            char prev = text.charAt(matchIndex - 1);
            char after = text.charAt(matchIndex + matchText.length());
            if(type == "number" || type == "identifier"){
                if(Tokens.get("punctuation").indexOf(prev) == -1 && prev != ' '
                        || after != ' ' && Tokens.get("punctuation").indexOf(after) == -1)
                    continue;
            }
            tokens.add(new TokenPos(matchIndex, matchText.trim(), type));
            text = text.substring(0, matchIndex) + LengthWord(matchText.length())
                    + text.substring(matchIndex + matchText.length());
        }
        return text;

    }

    static ArrayList<String> keyWords = new ArrayList<>(Arrays.asList("and", "auto", "bool", "break",
            "case", "catch", "char", "class", "const", "continue", "decltype", "default", "delete", "do", "double",
            "dynamic_cast", "else", "enum", "explicit", "extern", "false", "float", "for", "friend", "goto", "if",
            "inline", "int", "long", "main", "mutable", "namespace", "new", "nullptr", "operator", "or", "private",
            "protected", "public", "register", "reinterpret_cast", "return", "short", "signed", "sizeof", "static",
            "static_cast", "struct", "switch", "template", "this", "throw", "true", "try", "typedef", "typeid",
            "typename", "union", "unsigned", "using", "virtual", "void", "volatile", "while"));
    public static Map<String, String> Tokens = new HashMap<>();

    private static String AddTokens(String text, Map.Entry<String, String> tokens) {
        String typeToken = tokens.getKey();
        Pattern regex = Pattern.compile(tokens.getValue());
        Matcher regexMatcher = regex.matcher(text);
        while (regexMatcher.find()) {
            String  matchText = regexMatcher.group();
            int matchIndex = regexMatcher.start();
            char prev = text.charAt(matchIndex - 1);
            char after = text.charAt(matchIndex + matchText.length());
            if(typeToken == "number" || typeToken == "identifier"){
                if(Tokens.get("punctuation").indexOf(prev) == -1 && prev != ' '
                        || after != ' ' && Tokens.get("punctuation").indexOf(after) == -1)
                    continue;
            }
            Main.tokens.add(new TokenPos(matchIndex, matchText.trim(), typeToken));
            text = text.substring(0, matchIndex)
                    + LengthWord(matchText.length())+ text.substring(matchIndex + matchText.length());
        }
        return text;
    }

}