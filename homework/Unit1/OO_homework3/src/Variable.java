import java.math.BigInteger;
import java.util.Objects;

public class Variable {
    private BigInteger coe;
    private BigInteger[] pow; //xyz

    public Variable(BigInteger coe, BigInteger pow1, BigInteger pow2, BigInteger pow3) {
        this.coe = coe;
        pow = new BigInteger[3];
        this.pow[0] = pow1;
        this.pow[1] = pow2;
        this.pow[2] = pow3;
    }

    public BigInteger getCoe() {
        return coe;
    }

    public void setCoe(BigInteger coe) {
        this.coe = coe;
    }

    public BigInteger getPow(int index) {
        return pow[index];
    }

    public void setPow(int i, BigInteger pow) {
        this.pow[i] = pow;
    }

    public boolean varCheck(Variable var) {
        if (getPow(0).equals(var.getPow(0))) {
            if (getPow(1).equals(var.getPow(1))) {
                return getPow(2).equals(var.getPow(2));
            }
        }
        return false;
    }

    public Variable getDeepCopyVariable() {
        BigInteger varCoe = this.coe;
        BigInteger[] varPow = new BigInteger[3];

        for (int i = 0; i < 3; i++) {
            varPow[i] = this.pow[i];
        }
        return new Variable(varCoe, varPow[0], varPow[1], varPow[2]);
    }

    public String toString() {
        if (coe.compareTo(BigInteger.ZERO) == 0) {
            return "+0";
        }

        StringBuilder sb = new StringBuilder();
        if (coe.compareTo(BigInteger.ZERO) > 0) {
            sb.append("+").append(coe);
        } else {
            sb.append(coe);
        }

        if (!pow[0].equals(BigInteger.ZERO) || !pow[1].equals(BigInteger.ZERO)
                || !pow[2].equals(BigInteger.ZERO)) {
            sb.append("*");
        }

        if (!Objects.equals(pow[0], BigInteger.ZERO)) {
            if (pow[0].compareTo(BigInteger.ONE) == 0) {
                sb.append("x");
            } else {
                sb.append("x^").append(pow[0]);
            }
            if (!pow[1].equals(BigInteger.ZERO) || !pow[2].equals(BigInteger.ZERO)) {
                sb.append("*");
            }
        }
        if (!Objects.equals(pow[1], BigInteger.ZERO)) {
            if (pow[1].compareTo(BigInteger.ONE) == 0) {
                sb.append("y");
            } else {
                sb.append("y^").append(pow[1]);
            }
            if (!pow[2].equals(BigInteger.ZERO)) {
                sb.append("*");
            }
        }
        if (!Objects.equals(pow[2], BigInteger.ZERO)) {
            if (pow[2].compareTo(BigInteger.ONE) == 0) {
                sb.append("z");
            } else {
                sb.append("z^").append(pow[2]);
            }
        }
        return sb.toString();
    }

    public boolean noxyz() {
        if (pow[0].compareTo(BigInteger.ZERO) == 0) {
            if (pow[1].compareTo(BigInteger.ZERO) == 0) {
                return pow[2].compareTo(BigInteger.ZERO) == 0;
            }
        }
        return false;
    }
}
