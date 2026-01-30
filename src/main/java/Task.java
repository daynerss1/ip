public class Task {
    private String name;
    private boolean done;

    public Task(String name) {
        this.name = name;
        this.done = false;
    }

    public void mark() {
        this.done = true;
    }

    public void unmark() {
        this.done = false;
    }

    public boolean isDone() {
        return this.done;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        String completion = this.done ? "X" : " ";
        return "[" + completion + "] " + this.name;
    }
}
