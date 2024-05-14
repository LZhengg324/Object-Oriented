import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefineFunc {
    private char type;
    private ArrayList<Character> parameters = new ArrayList<>();
    private String str;
    private Expr expr;

    public DefineFunc() {
    }

    public void initialize(String str, AllDefineFunc allDefineFunc) {
        String ss = str.replaceAll("[ \\t]+", "");

        Pattern pattern = Pattern.compile("([fgh])\\(([xyz])(,[xyz])?(,[xyz])?\\)=");
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            setType(matcher.group(1).charAt(0));
            setXyz(matcher.group(2).charAt(0));
            ss = ss.replaceAll(String.valueOf(matcher.group(2).charAt(0)), "u");
            if (matcher.group(3) != null) {
                setXyz(matcher.group(3).charAt(1));
                ss = ss.replaceAll(String.valueOf(matcher.group(3).charAt(1)), "b");
                if (matcher.group(4) != null) {
                    setXyz(matcher.group(4).charAt(1));
                    ss = ss.replaceAll(String.valueOf(matcher.group(4).charAt(1)), "w");
                }
            }
            ss = ss.replaceAll("u", "x");
            ss = ss.replaceAll("b", "y");
            ss = ss.replaceAll("w", "z");
        }

        pattern = Pattern.compile("([fgh])\\(([xyz])(,[xyz])?(,[xyz])?\\)=");
        matcher = pattern.matcher(ss);
        if (matcher.find()) {
            ss = ss.replace(matcher.group(0), "");
            setStr(funcExprRemaker(ss));
        }
        Lexer lexer = new Lexer(this.str);
        Parser parser = new Parser(lexer, allDefineFunc);
        this.expr = parser.parseExpr();
    }

    public String funcExprRemaker(String str) {
        String s;
        s = str.replaceAll("[ \\t]+", "");
        s = s.replaceAll("\\-\\+|\\+\\-","-");
        s = s.replaceAll("\\-\\-|\\+\\+","+");
        s = s.replaceAll("\\-\\+|\\+\\-","-");
        s = s.replaceAll("\\-\\-|\\+\\+","+");

        s = s.replaceAll("\\*\\*","^");
        return s;
    }

    private void setType(char type) {
        this.type = type;
    }

    private void setXyz(char par) {
        parameters.add(par);
    }

    private void setStr(String str) {
        this.str = str;
    }

    public Expr getFuncExpr() {
        return expr;
    }
}
