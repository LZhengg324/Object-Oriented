public class OrderRequest {
    private final String studentId;
    private final String orderedBookId;
    private final char type;
    private boolean valid;

    public OrderRequest(String studentId, String orderedBookId, char type) {
        this.studentId = studentId;
        this.orderedBookId = orderedBookId;
        this.type = type;
        this.valid = true;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getOrderedBookId() {
        return orderedBookId;
    }

    public char getType() {
        return type;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
