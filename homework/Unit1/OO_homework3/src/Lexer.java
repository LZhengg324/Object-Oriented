public class Lexer {
    private final String xyz = "xyz";
    private final String symbol = "()+-*^,";
    private final String functs = "fgh";
    private final String differential = "d";
    private final String input;
    private String curtoken; //当前所指字符
    private int pos = 0; //当前字符串位置

    public Lexer(String input) {
        this.input = input;
        this.next();
    }

    public void next() {
        if (pos == input.length()) {
            return;
        }

        char c = input.charAt(pos);
        if (c - '0' >= 0 && c - '0' <= 9) {
            curtoken = getnumber();
        } else if (symbol.indexOf(c) != -1) {
            curtoken = String.valueOf(c);
            pos++;
        } else if (xyz.indexOf(c) != -1) {
            curtoken = String.valueOf(c);
            pos++;
        } else if (input.charAt(pos) == 's' || input.charAt(pos) == 'c') {
            curtoken = getTri();
        } else if (functs.indexOf(c) != -1) {
            curtoken = String.valueOf(c);
            pos++;
        } else if (differential.indexOf(c) != -1) {
            curtoken = String.valueOf(c);
            pos++;
        }
    }

    private String getTri() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(input.charAt(pos++));
        } while (pos < input.length() && ("sin".indexOf(input.charAt(pos)) != -1 ||
                "cos".indexOf(input.charAt(pos)) != -1));

        return sb.toString();
    }

    public boolean isTri() {
        return "sin".equals(curtoken) || "cos".equals(curtoken);
    }

    public String getnumber() {
        StringBuffer sb = new StringBuffer();

        do {
            sb.append(input.charAt(pos++));
        } while (pos < input.length() && Character.isDigit(input.charAt(pos)));

        return sb.toString();
    }

    public boolean isNumber() {
        for (int i = 0; i < curtoken.length(); i++) {
            if (!Character.isDigit(curtoken.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public String getCurtoken() {
        return curtoken;
    }

    public boolean isxyz() {
        return xyz.contains(this.curtoken);
    }

    public boolean isFunct() {
        return functs.contains(this.curtoken);
    }

    public boolean isDifferential() {
        return differential.contains(this.curtoken);
    }
}
