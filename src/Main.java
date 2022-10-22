import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        Tokenizer tk = new Tokenizer("input.txt");
        tk.ExtractTokens();
        tk.PrintRes("output.txt");
    }
}