public class AddOnRequest {
    private final String school;
    private final String orderedBookId;
    private int capacity;

    public AddOnRequest(String school, String bookId) {
        this.school = school;
        this.orderedBookId = bookId;
        this.capacity = 1;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getSchool() {
        return school;
    }

    public String getOrderedBookId() {
        return orderedBookId;
    }

    public int getCapacity() {
        return capacity;
    }
}
