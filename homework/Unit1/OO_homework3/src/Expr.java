import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

public class Expr implements Factor {
    private ArrayList<Term> terms;

    public Expr() {
        terms = new ArrayList<>();
    }

    public void addTerm(Term term) {
        terms.add(term);
    }

    public ArrayList<Term> getTerms() {
        return terms;
    }

    public static Expr mergeExpr(Expr expr1, Expr expr2) {
        if (expr1 == null) {
            return expr2;
        }
        if (expr2 == null) {
            return expr1;
        }
        Expr expr = new Expr();
        expr1.terms.forEach(expr::addTerm);
        expr2.terms.forEach(expr::addTerm);
        return expr;
    }

    public void termsAddOrSub() {
        ArrayList<TriFunc> triFuncs = new ArrayList<>();
        for (Term terms : getTerms()) {
            for (Factor factor : terms.getFactors()) {
                if (factor instanceof TriFunc) {
                    triFuncs.add((TriFunc) factor);
                } else if (factor instanceof Expr) {
                    Expr expr = (Expr)factor;
                    expr.termsAddOrSub();
                    for (Term terms1 : expr.getTerms()) {
                        for (Factor factor1 : terms1.getFactors()) {
                            triFuncs.add((TriFunc) factor1);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < triFuncs.size(); i++) {
            for (int j = i + 1; j < triFuncs.size(); j++) {
                TriFunc triFunc1 = triFuncs.get(i);
                TriFunc triFunc2 = triFuncs.get(j);

                if (triFunc1.addOrsubCheck(triFunc2)) {
                    triFunc1.getVar().setCoe(triFunc1.getVar().getCoe().add(
                            triFunc2.getVar().getCoe()));
                    triFuncs.remove(triFunc2);
                    j--;
                }
            }
        }

        this.terms.clear();
        for (TriFunc triFunc : triFuncs) {
            Term termReturn = new Term();
            termReturn.addFactor(triFunc);
            this.terms.add(termReturn);
        }
    }

    public void expandExpressionWithNumPow(int pow) {
        if (pow == 0) {
            terms.clear();
            Term term = new Term();
            Variable var = new Variable(BigInteger.ONE, BigInteger.ZERO,
                    BigInteger.ZERO, BigInteger.ZERO);
            TriFunc triFunc = new TriFunc(var);
            term.addFactor(triFunc);
            terms.add(term);
            return;
        }
        ArrayList<Factor> factors = new ArrayList<>();
        ArrayList<Factor> factorsReturn = new ArrayList<>();

        for (Term term : getTerms()) {
            factors.addAll(term.getFactors());
            factorsReturn.addAll(term.getFactors());
        }

        expand(factors, factorsReturn, pow);

        Term term = new Term();
        for (Factor factor : factorsReturn) {
            term.addFactor(factor);
        }
        this.terms.clear();
        this.terms.add(term);
        termsAddOrSub();
    }

    public void expand(ArrayList<Factor> factors, ArrayList<Factor> factorsReturn, int pow) {
        ArrayList<TriFunc> factorsTemp = new ArrayList<>();
        for (int i = 0; i < pow - 1; i++) {
            for (Factor f1 : factors) {
                for (Factor f2 : factorsReturn) {
                    if (f1 instanceof TriFunc && f2 instanceof TriFunc) {
                        BigInteger varCoe = ((TriFunc) f1).getVar().getCoe().multiply(
                                ((TriFunc) f2).getVar().getCoe());
                        BigInteger[] varPow = new BigInteger[3];
                        for (int j = 0; j < 3; j++) {
                            varPow[j] = ((TriFunc) f1).getVar().getPow(j).add(
                                    ((TriFunc) f2).getVar().getPow(j));
                        }

                        Variable var = new Variable(varCoe, varPow[0],
                                varPow[1], varPow[2]);
                        TriFunc triFunc = new TriFunc(var);
                        ArrayList<Tri> tris = triFunc.getTriFuncs();
                        ArrayList<Tri> tris1 = ((TriFunc) f1).getTriFuncs();
                        ArrayList<Tri> tris2 = ((TriFunc) f2).getTriFuncs();

                        if (tris1.size() == 0) {
                            for (Tri tri : tris2) {
                                tris.add(new Tri(tri.getName(), tri.getFactor(), tri.getPow()));
                            }
                        } else if (tris2.size() == 0) {
                            for (Tri tri : tris1) {
                                tris.add(new Tri(tri.getName(), tri.getFactor(), tri.getPow()));
                            }
                        } else {
                            for (Tri tri : tris1) {
                                tris.add(new Tri(tri.getName(), tri.getFactor(), tri.getPow()));
                            }
                            for (Tri tri2 : tris2) {
                                int mark = 1;
                                for (Tri tri1 : tris) {
                                    if (tri2.equals(tri1)) {
                                        if (tri2.multiplyEquals(tri1)) {
                                            tri1.setPow(tri1.getPow().add(tri2.getPow()));
                                            mark = 0;
                                            break;
                                        }
                                    }
                                }
                                if (mark == 1) {
                                    tris.add(new Tri(tri2.getName(),
                                            tri2.getFactor(), tri2.getPow()));
                                }
                            }
                        }
                        factorsTemp.add(triFunc);
                        termsAddOrSub();
                    }
                }
            }
            factorsReturn.clear();
            factorsReturn.addAll(factorsTemp);
            factorsTemp.clear();
        }
    }

    public Expr getDeepCopyExpr() {
        Expr expr = new Expr();
        for (Term t : this.terms) {
            Term term = new Term();
            for (Factor factor : t.getFactors()) {
                if (factor instanceof TriFunc) {
                    BigInteger varCoe = ((TriFunc) factor).getVar().getCoe();
                    BigInteger varPow0 = ((TriFunc) factor).getVar().getPow(0);
                    BigInteger varPow1 = ((TriFunc) factor).getVar().getPow(1);
                    BigInteger varPow2 = ((TriFunc) factor).getVar().getPow(2);
                    Variable var = new Variable(varCoe, varPow0, varPow1, varPow2);
                    TriFunc triFunc = new TriFunc(var);

                    ArrayList<Tri> tris = triFunc.getTriFuncs();
                    ArrayList<Tri> factorTris = ((TriFunc) factor).getTriFuncs();

                    for (Tri tri : factorTris) {
                        tris.add(tri.getDeepCopyTri());
                    }
                    term.addFactor(triFunc);
                } else {
                    ArrayList<TriFunc> temp = t.getExprInExpr((Expr)factor);
                    for (TriFunc triFunc : temp) {
                        term.addFactor(triFunc);
                    }
                }
            }
            expr.addTerm(term);
        }
        return expr;
    }

    public Expr doPut(ArrayList<Factor> putIn) {
        Expr expr = new Expr();
        /*深拷贝defineFunc的expr的途中带入parameters*/
        for (Term t : this.terms) {
            Term term = new Term();
            for (Factor factor : t.getFactors()) {
                if (factor instanceof TriFunc) {
                    term.addFactor(((TriFunc)factor).doPutTriFunc(putIn));
                    term.calFactmulFact();
                } else {
                    term.addFactor(((Expr)factor).doPut(putIn));
                    term.calFactmulFact();
                }
            }
            expr.addTerm(term);
        }
        return expr;
    }

    public ArrayList<TriFunc> getTriFuncfromExpr(Expr expr) {
        ArrayList<TriFunc> triReturn = new ArrayList<>();
        for (Term term : expr.getTerms()) {
            for (Factor factor : term.getFactors()) {
                if (factor instanceof TriFunc) {
                    triReturn.add(((TriFunc) factor).getDeepCopyTriFunc());
                } else if (factor instanceof Expr) {
                    triReturn.addAll(getTriFuncfromExpr((Expr)factor));
                }
            }
        }
        return triReturn;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Expr) {
            ArrayList<TriFunc> triFuncs1 = new ArrayList<>();
            Expr expr = (Expr)obj;
            for (Term terms : expr.getTerms()) {
                for (Factor factor : terms.getFactors()) {
                    if (factor instanceof TriFunc) {
                        triFuncs1.add((TriFunc) factor);
                    } else {
                        triFuncs1.addAll(getTriFuncfromExpr((Expr)factor));
                    }
                }
            }

            ArrayList<TriFunc> triFuncs2 = new ArrayList<>();
            for (Term terms : this.terms) {
                for (Factor factor : terms.getFactors()) {
                    if (factor instanceof TriFunc) {
                        triFuncs2.add((TriFunc) factor);
                    } else {
                        triFuncs2.addAll(getTriFuncfromExpr((Expr)factor));
                    }
                }
            }

            if (triFuncs1.size() == triFuncs2.size()) {
                for (TriFunc triFunc1 : triFuncs1) {
                    int mark = 0;
                    for (TriFunc triFunc2 : triFuncs2) {
                        if (triFunc1.equals(triFunc2)) { //改过，原为factormultiplycheck
                            mark = 1;
                            break;
                        }
                    }
                    if (mark == 0) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public String toString() {
        Iterator<Term> iter = terms.iterator();
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
