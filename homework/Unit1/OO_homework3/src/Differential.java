import java.math.BigInteger;
import java.util.ArrayList;

public class Differential {
    private char type;

    public Differential(char type) {
        this.type = type;
    }

    public char getType() {
        return this.type;
    }

    public Factor doDifferentialFactor(Factor factor) {
        if (factor instanceof TriFunc) {
            return doDifferentialTriFunc((TriFunc)factor);
        } else {
            return doDifferentialExpr((Expr)factor);
        }
    }

    public Expr doDifferentialTriFunc(TriFunc triFunc) {
        Expr exprReturn = new Expr();
        Term termReturn = new Term();
        if (triFunc.getTriFuncSize() == 0) {
            termReturn.addFactor(doDifferentialVar(triFunc));
            termReturn.calFactmulFact();
            exprReturn.addTerm(termReturn);
        } else if (triFunc.getTriFuncSize() == 1 &&
                triFunc.getVar().noxyz()) {

            TriFunc triFuncTemp = triFunc.getDeepCopyTriFunc();
            Tri tri = triFuncTemp.getTriFuncs().get(0);
            Variable triVar = triFuncTemp.getVar();

            if (tri.getPow().compareTo(BigInteger.ZERO) >= 0) {
                triVar.setCoe(triVar.getCoe().multiply(tri.getPow()));
                tri.setPow(tri.getPow().subtract(BigInteger.ONE));

                if (tri.getPow().compareTo(BigInteger.ZERO) < 0) {
                    tri.setPow(BigInteger.ZERO);
                }

                termReturn.addFactor(triFuncTemp);
                termReturn.calFactmulFact();
            }

            Tri triTemp = tri.getDeepCopyTri();
            triTemp.setPow(BigInteger.ONE);
            Variable var = new Variable(BigInteger.ONE, BigInteger.ZERO,
                    BigInteger.ZERO, BigInteger.ZERO);
            TriFunc triFuncTemp2 = new TriFunc(var);
            triFuncTemp2.addTri(triTemp);

            if (triTemp.getName().equals("sin")) {
                triTemp.setName("cos");
            } else {
                triTemp.setName("sin");
                var.setCoe(var.getCoe().multiply(BigInteger.valueOf(-1)));
            }

            termReturn.addFactor(triFuncTemp2);
            termReturn.calFactmulFact();
            termReturn.addFactor(doDifferentialFactor(triTemp.getFactor()));
            termReturn.calFactmulFact();
            exprReturn.addTerm(termReturn);
        }
        return exprReturn;
    }

    public Expr doDifferentialExpr(Expr expr) {
        Expr exprReturn = new Expr();
        for (Term term : expr.getTerms()) {
            for (Factor factor : term.getFactors()) {
                if (factor instanceof TriFunc) {
                    /*微分TriFunc*/
                    ArrayList<TriFunc> triFuncs = new ArrayList<>(getTriFunc((TriFunc) factor));

                    for (int i = 0; i < triFuncs.size(); i++) {
                        Term termReturn = new Term();
                        for (int j = 0; j < triFuncs.size(); j++) {
                            if (i != j) {
                                termReturn.addFactor(triFuncs.get(j));
                                termReturn.calFactmulFact();
                            } else {
                                termReturn.addFactor(doDifferentialTriFunc(triFuncs.get(i)));
                                termReturn.calFactmulFact();
                            }
                        }
                        exprReturn.addTerm(termReturn);
                    }

                } else if (factor instanceof Expr) {
                    /*微分Expr*/
                    exprReturn = doDifferentialExpr((Expr)factor);    //若为Expr，重复调用doDifferential
                }
            }
        }
        return exprReturn;
    }

    private ArrayList<TriFunc> getTriFunc(TriFunc triFunc) {    //将一个TriFunc分解成多多个
        ArrayList<TriFunc> returnArray = new ArrayList<>();
        TriFunc triFuncTemp1 = new TriFunc(triFunc.getVar().getDeepCopyVariable());
        returnArray.add(triFuncTemp1);
        for (Tri tri : triFunc.getTriFuncs()) {
            Variable v = new Variable(BigInteger.ONE, BigInteger.ZERO,
                    BigInteger.ZERO, BigInteger.ZERO);
            TriFunc t = new TriFunc(v);
            t.addTri(tri.getDeepCopyTri());
            returnArray.add(t);
        }
        return returnArray;
    }

    public TriFunc doDifferentialVar(TriFunc triFunc) {
        Variable var = triFunc.getVar();

        /*微分*/
        BigInteger varCoe = var.getCoe().multiply(var.getPow(type - 'x'));
        BigInteger[] varPow = new BigInteger[3];

        for (int i = 0; i < 3; i++) {
            varPow[i] = var.getPow(i);
        }

        /*偏微的指数-1*/
        if (varPow[type - 'x'].compareTo(BigInteger.ZERO) != 0) {   //偏微变量的指数不为0
            varPow[type - 'x'] = varPow[type - 'x'].subtract(BigInteger.ONE);
        } else {    //偏微变量的指数为0
            varPow[type - 'x'] = BigInteger.ZERO;
        }

        Variable varNew = new Variable(varCoe, varPow[0], varPow[1], varPow[2]);
        return new TriFunc(varNew);
    }
}