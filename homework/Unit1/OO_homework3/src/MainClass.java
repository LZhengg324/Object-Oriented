import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AllDefineFunc allDefineFunc = new AllDefineFunc();

        String n = scanner.nextLine();
        int numFunc = Integer.parseInt(n);
        for (int i = 0; i < numFunc; i++) {
            String str = scanner.nextLine();
            str = str.replaceAll("[ \\t]+", "");
            char type = str.charAt(0);
            DefineFunc df = new DefineFunc();
            df.initialize(str, allDefineFunc);
            allDefineFunc.putInFunct(type, df);
        }

        String input = scanner.nextLine();

        input = input.replaceAll("[ \\t]+", "");

        input = remaker(input);

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer, allDefineFunc);

        Expr expr = parser.parseExpr();
        String str = expr.toString();

        if (str.equals("")) {
            str = "0";
        }

        str = str.replaceAll("\\^", "\\*\\*");
        str = str.replaceAll("\\+1\\*", "\\+");
        str = str.replaceAll("\\*\\+", "\\*");
        str = str.replaceAll("\\(\\+", "\\(");
        if ("+".indexOf(str.charAt(0)) != -1) {
            str = str.substring(1);
        }
        System.out.println(str);
    }

    public static String remaker(String str) {
        String s;
        s = str.replaceAll("\\-\\+|\\+\\-","-");
        s = s.replaceAll("\\-\\-|\\+\\+","+");
        s = s.replaceAll("\\-\\+|\\+\\-","-");
        s = s.replaceAll("\\-\\-|\\+\\+","+");

        s = s.replaceAll("\\*\\*","^");
        //s = s.replaceAll("\\-","-1*");

        return s;
    }
}
