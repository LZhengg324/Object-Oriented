import java.util.HashMap;

public class AllDefineFunc {
    private HashMap<Character, DefineFunc> allDefineFunc = new HashMap<>();

    public AllDefineFunc() {
    }

    public void putInFunct(char type, DefineFunc df) {
        allDefineFunc.put(type, df);
    }

    public int getSize() {
        return allDefineFunc.size();
    }

    public HashMap<Character, DefineFunc> getMap() {
        return allDefineFunc;
    }
}
