import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

public class TriFunc implements Factor {
    private Variable var;
    private ArrayList<Tri> triFunc = new ArrayList<>();

    public TriFunc(Variable var) {
        this.var = var;
    }

    public void addTri(Tri tri) {
        triFunc.add(tri);
    }

    public Variable getVar() {
        return this.var;
    }

    public ArrayList<Tri> getTriFuncs() {
        return this.triFunc;
    }

    public int getTriFuncSize() {
        return triFunc.size();
    }

    public Factor doPutTriFunc(ArrayList<Factor> putIn) {
        Term term = new Term();
        BigInteger varCoe = this.getVar().getCoe();
        BigInteger[] varPow = new BigInteger[3];

        for (int i = 0; i < 3; i++) {
            varPow[i] = this.getVar().getPow(i);
        }

        for (int i = 0; i < 3; i++) {
            while (varPow[i].compareTo(BigInteger.ZERO) != 0 && putIn.size() > i) {
                if (putIn.get(i) instanceof TriFunc) {
                    term.addFactor(((TriFunc) putIn.get(i)).getDeepCopyTriFunc());
                    term.calFactmulFact();
                    varPow[i] = varPow[i].subtract(BigInteger.ONE);
                } else {
                    Expr expr1 = ((Expr)putIn.get(i)).getDeepCopyExpr();
                    term.addFactor(expr1);
                    term.calFactmulFact();
                    varPow[i] = varPow[i].subtract(BigInteger.ONE);
                }
            }
        }

        Variable var = new Variable(varCoe, varPow[0], varPow[1], varPow[2]);
        TriFunc triFunc = new TriFunc(var);

        ArrayList<Tri> tris = triFunc.getTriFuncs();
        ArrayList<Tri> factorTris = this.getTriFuncs();

        /*带入三角函数中需要替换的变量*/
        for (Tri tri : factorTris) {
            String nameTemp = tri.getName();
            BigInteger powTemp = tri.getPow();
            Factor factorTemp;
            if (tri.getFactor() instanceof TriFunc) {
                factorTemp = ((TriFunc) tri.getFactor()).doPutTriFunc(putIn);
            } else {
                factorTemp = ((Expr)tri.getFactor()).doPut(putIn);
            }
            tris.add(new Tri(nameTemp, factorTemp, powTemp));
        }
        term.addFactor(triFunc);
        term.calFactmulFact();
        return term.getFactors().get(0);
    }

    public boolean addOrsubCheck(TriFunc otherTriFunc) {
        if (this.var.varCheck(otherTriFunc.getVar())) {
            if (this.getTriFuncSize() == otherTriFunc.getTriFuncSize()) {
                for (Tri tri1 : this.triFunc) {
                    int mark = 0;
                    for (Tri tri2 : otherTriFunc.getTriFuncs()) {
                        if (tri1.equals(tri2)) {
                            if (tri1.getPow().compareTo(tri2.getPow()) == 0) {
                                mark = 1;
                                break;
                            }
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

    public boolean factorMultiplyCheck(TriFunc triFunc) {
        if (this.var.getCoe().equals(triFunc.var.getCoe())) {
            return this.var.varCheck(triFunc.var);
        }
        return false;
    }

    public boolean equals(TriFunc otherTriFunc) {
        if (this.var.getCoe().equals(otherTriFunc.getVar().getCoe())) {
            if (this.var.varCheck(otherTriFunc.getVar())) {
                if (this.getTriFuncSize() == otherTriFunc.getTriFuncSize()) {
                    for (Tri tri1 : this.triFunc) {
                        int mark = 0;
                        for (Tri tri2 : otherTriFunc.getTriFuncs()) {
                            if (tri1.equals(tri2)) {
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
        }
        return false;
    }

    public TriFunc getDeepCopyTriFunc() {
        Variable varReturn = this.var.getDeepCopyVariable();
        TriFunc triFuncReturn = new TriFunc(varReturn);
        ArrayList<Tri> trisReturn = triFuncReturn.getTriFuncs();
        for (Tri tri : this.triFunc) {
            trisReturn.add(tri.getDeepCopyTri());
        }
        return triFuncReturn;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (var.getCoe().compareTo(BigInteger.ZERO) == 0) {
            sb.append("+0");
            return sb.toString();
        }
        sb.append(var.toString());
        if (triFunc.size() != 0) {
            Iterator<Tri> iter = triFunc.iterator();
            sb.append("*");
            sb.append(iter.next().toString());
            while (iter.hasNext()) {
                sb.append("*");
                sb.append(iter.next().toString());
            }
        }
        return sb.toString();
    }
}
