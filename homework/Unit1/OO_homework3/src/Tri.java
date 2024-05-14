import java.math.BigInteger;
import java.util.ArrayList;

public class Tri {
    private String name;
    private Factor factor;
    private BigInteger pow;

    public Tri(String name, Factor factor,BigInteger pow) {
        this.name = name;
        this.factor = factor;
        this.pow = pow;
    }

    public BigInteger getPow() {
        return pow;
    }

    public void setPow(BigInteger pow) {
        this.pow = pow;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Factor getFactor() {
        return factor;
    }

    public Tri getDeepCopyTri() {
        String name = this.name;
        Factor factor1 = getDeepCopyFactor();
        BigInteger pow = getPow();
        Tri triReturn = new Tri(name, factor1, pow);
        return triReturn;
    }

    public Factor getDeepCopyFactor() {
        if (this.factor instanceof TriFunc) {
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

            return triFunc;
        } else {
            return ((Expr)this.factor).getDeepCopyExpr();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tri) {
            Tri tri = (Tri)o;
            if (this.name.equals(tri.getName())) {
                if (this.getFactor() instanceof Expr &&
                        tri.getFactor() instanceof Expr) {
                    Expr expr1 = (Expr) this.getFactor();
                    Expr expr2 = (Expr) tri.getFactor();
                    return expr1.equals(expr2);
                } else if (this.getFactor() instanceof TriFunc &&
                        tri.getFactor() instanceof TriFunc) {
                    TriFunc tri1 = (TriFunc) this.getFactor();
                    TriFunc tri2 = (TriFunc) tri.getFactor();
                    return tri1.equals(tri2);
                }
            }
        }
        return false;
    }

    public boolean multiplyEquals(Object o) {
        if (o instanceof Tri) {
            Tri tri = (Tri)o;
            if (this.name.equals(tri.getName())) {
                if (this.getFactor() instanceof Expr &&
                        tri.getFactor() instanceof Expr) {
                    Expr expr1 = (Expr) this.getFactor();
                    Expr expr2 = (Expr) tri.getFactor();
                    return expr1.equals(expr2);
                } else if (this.getFactor() instanceof TriFunc &&
                        tri.getFactor() instanceof TriFunc) {
                    TriFunc tri1 = (TriFunc) this.getFactor();
                    TriFunc tri2 = (TriFunc) tri.getFactor();
                    if (tri1.getTriFuncSize() == 0 && tri2.getTriFuncSize() == 0) {
                        return tri1.factorMultiplyCheck(tri2);
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (pow.compareTo(BigInteger.ZERO) != 0) {
            sb.append(name);
            sb.append("(");
            if (factor instanceof Expr) {
                sb.append("(");
                sb.append(factor);
                sb.append(")");
            } else {
                sb.append(factor);
            }
            sb.append(")");
            if (pow.compareTo(BigInteger.ONE) != 0) {
                sb.append("^").append(pow);
            }
        } else {
            sb.append("+1");
        }
        return sb.toString();
    }
}
