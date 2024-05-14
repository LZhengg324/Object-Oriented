public class OrderRequest {
    //private final String school;
    //private final String studentId;
    private final Student student;
    private final String orderedBookId;
    private final char type;
    private boolean valid;

    public OrderRequest(Student student, String orderedBookId, char type) {
        //this.school = school;
        //this.studentId = studentId;
        this.student = student;
        this.orderedBookId = orderedBookId;
        this.type = type;
        this.valid = false;
    }

    public Student getStudent() {
        return student;
    }

    public String getSchool() {
        return student.getSchoolId();
    }

    public String getStudentId() {
        return student.getStudentId();
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
