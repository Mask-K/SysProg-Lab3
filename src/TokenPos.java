public class TokenPos implements Comparable<TokenPos>{





    public int Position;
    public String Token;
    public String TokenType;

    TokenPos(int position, String token, String tokenType){
        this.Token =token;
        this.Position =position;
        this.TokenType =tokenType;
    }
    @Override
    public int compareTo(TokenPos f){
        return this.Position -f.Position;
    }
}


