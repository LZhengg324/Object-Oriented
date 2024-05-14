import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Term {
    private ArrayList<Factor> factors;

    public Term() {
        this.factors = new ArrayList<>();
    }

    public void addFactor(Factor factor) {
        factors.add(factor);
    }

    public void addAllFactor(ArrayList<TriFunc> t) {
        factors.addAll(t);
    }

    public ArrayList<Factor> getFactors() {
        return this.factors;
    }

    public static Term mergeTerm(Term term1, Term term2) {
        if (term1 == null) {
            return term2;
        }
        if (term2 == null) {
            return term1;
        }
        Term term = new Term();
        term1.factors.forEach(term::addFactor);
        term2.factors.forEach(term::addFactor);
        return term;
    }

    public Variable findVar(Factor f1) {
        for (Factor factor : factors) {
            if (f1 == factor) {
                continue;
            }
            if (factor instanceof Variable) {
                return (Variable) factor;
            }
        }
        return null;
    }

    public Expr findExpr(Factor f1) {
        for (Factor factor : factors) {
            if (f1 == factor) {
                continue;
            }
            if (factor instanceof Expr) {
                return (Expr)factor;
            }
        }
        return null;
    }

    public TriFunc findTriFunc(Factor f1) {
        for (Factor factor : factors) {
            if (f1 == factor) {
                continue;
            }
            if (factor instanceof TriFunc) {
                return (TriFunc) factor;
            }
        }
        return null;
    }

    public void calFactmulFact() {
        if (factors.size() == 1) {
            return;
        }
        Factor f1 = factors.get(factors.size() - 1);
        if (f1 instanceof TriFunc) {
            Factor f2 = findTriFunc(f1);
            if (f2 == null) {
                f2 = findExpr(f1);
                calTriFuncmulExpr((TriFunc) f1, (Expr) f2);
            } else {
                calTrifuncMulTrifunc((TriFunc) f1, (TriFunc) f2);
            }
        } else if (f1 instanceof Expr) {
            Factor f2 = findExpr(f1);
            if (f2 == null) {
                f2 = findTriFunc(f1);
                calTriFuncmulExpr((TriFunc) f2, (Expr) f1);
            } else {
                calExprmulExpr((Expr)f1, (Expr)f2);
            }
        }
    }

    //未完善
    private void calTrifuncMulTrifunc(TriFunc tri1, TriFunc tri2) {
        Variable var1 = tri1.getVar();
        Variable var2 = tri2.getVar();
        ArrayList<Tri> tris1 = tri1.getTriFuncs();
        ArrayList<Tri> tris2 = tri2.getTriFuncs();

        BigInteger varCoe = var1.getCoe().multiply(var2.getCoe());
        BigInteger[] varPows = new BigInteger[3];

        for (int i = 0; i < 3; i++) {
            varPows[i] = var1.getPow(i).add(var2.getPow(i));
        }

        Variable vtemp = new Variable(varCoe, varPows[0], varPows[1], varPows[2]);
        TriFunc triFunc = new TriFunc(vtemp);
        ArrayList<Tri> tris = triFunc.getTriFuncs();

        if (tris1.size() == 0) {
            tris.addAll(tris2);
        } else if (tris2.size() == 0) {
            tris.addAll(tris1);
        } else {
            tris.addAll(tris1);
            for (Tri triTemp2 : tris2) {
                int mark = 1;
                for (Tri triTemp1 : tris1) {
                    if (triTemp2.multiplyEquals(triTemp1)) {
                        triTemp1.setPow(triTemp1.getPow().add(triTemp2.getPow()));
                        mark = 0;
                        break;
                    }
                }
                if (mark == 1) {
                    tris.add(triTemp2);
                }
            }
        }

        factors.add(triFunc);
        factors.remove(tri1);
        factors.remove(tri2);
    }

    private void calExprmulExpr(Expr expr1, Expr expr2) {
        Expr exprTemp = new Expr();
        Term termTemp = new Term();

        /*先提取expr1中的每一个项和expr2中的每一个项相乘*/
        for (Term terms1 : expr1.getTerms()) {
            for (Factor fact1 : terms1.getFactors()) {
                if (fact1 instanceof TriFunc) {
                    TriFunc tri1 = (TriFunc) fact1;
                    Variable var1 = tri1.getVar();

                    for (Term terms2 : expr2.getTerms()) {
                        for (Factor fact2 : terms2.getFactors()) {
                            if (fact2 instanceof TriFunc) {
                                TriFunc tri2 = (TriFunc) fact2;
                                Variable var2 = tri2.getVar();

                                BigInteger[] varPow = new BigInteger[3];
                                Arrays.fill(varPow, BigInteger.ZERO);
                                BigInteger varCoe = var1.getCoe().multiply(var2.getCoe());

                                for (int i = 0; i < 3; i++) {
                                    varPow[i] = var1.getPow(i).add(var2.getPow(i));
                                }

                                Variable varTemp = new Variable(varCoe, varPow[0],
                                        varPow[1], varPow[2]);
                                TriFunc triFunc = new TriFunc(varTemp);
                                ArrayList<Tri> tris = triFunc.getTriFuncs();
                                ArrayList<Tri> tris1 = tri1.getTriFuncs();
                                ArrayList<Tri> tris2 = tri2.getTriFuncs();

                                calTris(tris1, tris2, tris);
                                termTemp.addFactor(triFunc);
                            } else if (fact2 instanceof Expr) {
                                calTriFuncmulExpr(tri1, (Expr) fact2);
                            }
                        }
                    }
                } else if (fact1 instanceof Expr) {
                    for (Term term : ((Expr) fact1).getTerms()) {
                        for (Factor factor : term.getFactors()) {
                            if (factor instanceof TriFunc) {
                                calTriFuncmulExpr((TriFunc)factor, expr2);
                            } else if (factor instanceof Expr) {
                                calExprmulExpr((Expr)factor, expr2);
                            }
                        }
                    }
                }
            }
        }
        exprTemp.addTerm(termTemp);
        factors.remove(expr1);
        factors.remove(expr2);
        factors.add(exprTemp);
    }

    private void calTris(ArrayList<Tri> tris1, ArrayList<Tri> tris2, ArrayList<Tri> tris) {
        for (Tri tri1 : tris1) {
            tris.add(tri1.getDeepCopyTri());
        }
        for (Tri tri1 : tris2) {
            int mark = 1;
            for (Tri tri2 : tris) {
                if (tri2.multiplyEquals(tri1)) {
                    tri2.setPow(tri2.getPow().add(tri1.getPow()));
                    mark = 0;
                    break;
                }
            }
            if (mark == 1) {
                tris.add(tri1);
            }
        }
    }

    public TriFunc calVar(TriFunc tri1, TriFunc tri2) {
        Variable var = tri1.getVar();
        BigInteger varCoe = var.getCoe().multiply(tri2.getVar().getCoe());
        BigInteger[] varPow = new BigInteger[3];

        for (int i = 0; i < 3; i++) {
            varPow[i] = var.getPow(i).add(tri2.getVar().getPow(i));
        }

        Variable varTemp = new Variable(varCoe, varPow[0],
                varPow[1], varPow[2]);
        return new TriFunc(varTemp);
    }

    private void calTriFuncmulExpr(TriFunc tri, Expr expr) {
        Expr expression = new Expr();

        for (Term terms : expr.getTerms()) {
            Term term = new Term();
            for (Factor factor : terms.getFactors()) {
                if (factor instanceof TriFunc) {
                    TriFunc triFunc = calVar(tri, (TriFunc)factor);

                    ArrayList<Tri> trisNew = triFunc.getTriFuncs();
                    ArrayList<Tri> triTriFuncs = tri.getTriFuncs();
                    ArrayList<Tri> factorTriFuncs = ((TriFunc) factor).getTriFuncs();

                    if (triTriFuncs.size() == 0) {
                        trisNew.addAll(factorTriFuncs);
                    } else if (factorTriFuncs.size() == 0) {
                        trisNew.addAll(triTriFuncs);
                    } else {
                        calTris(factorTriFuncs, triTriFuncs, trisNew);
                    }

                    term.addFactor(triFunc);
                } else {
                    ArrayList<TriFunc> temp = new ArrayList<>(getExprInExpr((Expr) factor));
                    for (TriFunc triFunc : temp) {

                        TriFunc triFuncTemp = calVar(tri, triFunc);

                        ArrayList<Tri> trisNew = triFuncTemp.getTriFuncs();
                        ArrayList<Tri> triTriFuncs = tri.getTriFuncs();
                        ArrayList<Tri> factorTriFuncs = triFunc.getTriFuncs();

                        if (triTriFuncs.size() == 0) {
                            for (Tri tri1 : factorTriFuncs) {
                                trisNew.add(tri1.getDeepCopyTri());
                            }
                        } else if (factorTriFuncs.size() == 0) {
                            for (Tri tri1 : triTriFuncs) {
                                trisNew.add(tri1.getDeepCopyTri());
                            }
                        } else {
                            calTris(factorTriFuncs, triTriFuncs, trisNew);
                        }
                        term.addFactor(triFuncTemp);
                    }
                }
            }
            expression.addTerm(term);
            expression.termsAddOrSub();
        }
        factors.remove(tri);
        factors.remove(expr);
        factors.add(expression);
    }

    public ArrayList<TriFunc> getExprInExpr(Expr expr) {
        ArrayList<TriFunc> returns = new ArrayList<>();
        for (Term term : expr.getTerms()) {
            for (Factor factor : term.getFactors()) {
                if (factor instanceof TriFunc) {
                    returns.add(((TriFunc) factor).getDeepCopyTriFunc());
                } else {
                    returns.addAll(getExprInExpr((Expr) factor));
                }
            }
        }
        return returns;
    }

    public String toString() {
        Iterator<Factor> iter = factors.iterator();
        StringBuilder sb = new StringBuilder();
        sb.append(iter.next().toString());
        if (iter.hasNext()) {
            sb.append(iter.next().toString());
            while (iter.hasNext()) {
                sb.append(iter.next().toString());
            }
        }
        return sb.toString();
    }
}
