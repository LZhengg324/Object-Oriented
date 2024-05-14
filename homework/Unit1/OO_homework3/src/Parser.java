import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Parser {
    private final Lexer lexer;
    private HashMap<Character, DefineFunc> allDefineFunc = new HashMap<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Parser(Lexer lexer, AllDefineFunc afd) {
        this.lexer = lexer;
        this.allDefineFunc = afd.getMap();
    }

    public Expr parseExpr() {
        Expr expr = new Expr();
        expr.addTerm(parseTerm());

        while ("+-".contains(lexer.getCurtoken())) {
            expr.addTerm(parseTerm());
        }
        expr.termsAddOrSub();
        return expr;
    }

    public Term parseTerm() {
        Term term = new Term();
        term.addFactor(parseFactor());

        while (lexer.getCurtoken().equals("*")) {
            lexer.next();
            term.addFactor(parseFactor());
            term.calFactmulFact(); //计算项
        }
        return term;
    }

    public Factor parseFactor() {
        int mark = 1;
        if ("+-".contains(lexer.getCurtoken())) {
            if (lexer.getCurtoken().equals("-")) {
                mark = -1;
            }
            lexer.next();
        }

        if (lexer.getCurtoken().equals("(")) { //因子为表达式
            lexer.next();
            Expr expr = parseExpr();
            lexer.next();
            if (lexer.getCurtoken().equals("^")) { //表达式有指数
                lexer.next();
                while (!lexer.isNumber()) {
                    lexer.next();
                }
                int pow = Integer.parseInt(lexer.getCurtoken());
                lexer.next();
                //展开表达式因子TODO
                expr.expandExpressionWithNumPow(pow);
            }
            if (mark == -1) {
                for (Term term : expr.getTerms()) {
                    Variable var = new Variable(BigInteger.valueOf(-1), BigInteger.ZERO,
                            BigInteger.ZERO, BigInteger.ZERO);
                    term.addFactor(new TriFunc(var));
                    term.calFactmulFact();
                }
            }
            return expr;

        } else if (lexer.isxyz()) { //因子为xyz变量
            return parsexyz(mark);
        } else if (lexer.isNumber()) {    //因子为常数
            return parseNumber(mark);
        } else if (lexer.isTri()) {
            return parseTriFunc(mark);
        } else if (lexer.isFunct()) {
            return parseFuncts(mark);
        } else if (lexer.isDifferential()) {
            return parseDifferential(mark);
        } else {    //暂用
            Variable var = new Variable(BigInteger.ZERO, BigInteger.ZERO,
                    BigInteger.ZERO, BigInteger.ZERO);
            return new TriFunc(var);
        }
    }

    private Factor parseDifferential(int mark) {
        lexer.next();//跳过d
        char type = lexer.getCurtoken().charAt(0);
        Differential differential = new Differential(type);
        lexer.next();//跳过变量
        lexer.next();//跳过(
        Expr expr = parseExpr();
        lexer.next();//跳过)
        Expr exprReturn = differential.doDifferentialExpr(expr);
        if (mark == -1) {
            changesign(exprReturn);
        }
        return exprReturn;
    }

    private TriFunc parsexyz(int mark) {
        String varMark = lexer.getCurtoken();
        BigInteger[] varPow = new BigInteger[3];

        Arrays.fill(varPow, BigInteger.ZERO);
        lexer.next();

        if (lexer.getCurtoken().equals("^")) { //xyz因子指数不为1
            lexer.next();
            while (!lexer.isNumber()) {
                lexer.next();
            }
            for (int i = 0; i < varMark.length(); i++) {
                BigInteger temp = new BigInteger(lexer.getCurtoken());
                varPow["xyz".indexOf(varMark)] = temp;
            }
            lexer.next();
            Variable var = new Variable(BigInteger.valueOf(mark), varPow[0],
                    varPow[1], varPow[2]);
            return new TriFunc(var);
        } else {                               //xyz因子指数为1
            varPow["xyz".indexOf(varMark)] = BigInteger.ONE;
            Variable var = new Variable(BigInteger.valueOf(mark), varPow[0],
                    varPow[1], varPow[2]);
            return new TriFunc(var);
        }
    }

    private TriFunc parseNumber(int mark) {
        BigInteger num = new BigInteger(lexer.getCurtoken());
        lexer.next();

        if (lexer.getCurtoken().equals("^")) { //xyz因子指数不为1
            lexer.next();
            while (!lexer.isNumber()) {
                lexer.next();
            }
            num = num.pow(Integer.parseInt(lexer.getCurtoken()));
            lexer.next();
        }

        Variable var = new Variable(num.multiply(BigInteger.valueOf(mark)), BigInteger.ZERO,
                BigInteger.ZERO, BigInteger.ZERO);
        return new TriFunc(var);
    }

    private TriFunc parseTriFunc(int mark) {
        final String triName = lexer.getCurtoken();
        BigInteger pow = BigInteger.ONE;
        lexer.next();   //跳过sin|跳过cos
        lexer.next();   //跳过(
        Factor factor = parseFactor();
        lexer.next();   //跳过)
        if (lexer.getCurtoken().equals("^")) { //三角函数指数不为1
            lexer.next();
            while (!lexer.isNumber()) {
                lexer.next();
            }
            pow = BigInteger.valueOf(Integer.parseInt(lexer.getCurtoken()));
            lexer.next();
        }
        Tri tri = new Tri(triName, factor, pow);
        Variable var = new Variable(BigInteger.ONE.multiply(BigInteger.valueOf(mark)),
                BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);
        TriFunc triFunc = new TriFunc(var);
        triFunc.addTri(tri);
        return triFunc;
    }

    private Expr parseFuncts(int mark) {
        char type = lexer.getCurtoken().charAt(0);
        final DefineFunc func = allDefineFunc.get(type);
        lexer.next();   //跳过fgh
        lexer.next();   //跳过(
        ArrayList<Factor> putIn = new ArrayList<>();
        putIn.add(parseFactor());
        while (lexer.getCurtoken().equals(",")) {
            lexer.next();
            putIn.add(parseFactor());
        }
        lexer.next();   //跳过)
        Expr expr = func.getFuncExpr().doPut(putIn);
        expr.termsAddOrSub();
        if (mark == -1) {
            changesign(expr);
        }
        return expr;
    }

    private void changesign(Expr expr) {
        for (Term term : expr.getTerms()) {
            for (Factor factor : term.getFactors()) {
                if (factor instanceof TriFunc) {
                    Variable var = ((TriFunc)factor).getVar();
                    var.setCoe(var.getCoe().multiply(BigInteger.valueOf(-1)));
                } else if (factor instanceof Expr) {
                    changesign((Expr)factor);
                }
            }
        }
    }
}
