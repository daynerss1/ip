public class Task {
    private String name;
    private boolean done;

    public Task(String name) {
        this.name = name;
        this.done = false;
    }

    public String mark() {
        System.out.println("Nice! I've marked this task as done:\n  ");
        this.done = true;
        return this.toString();

    }

    public String unmark() {
        System.out.println("OK, I've marked this task as not done yet:\n  ");
        this.done = false;
        return this.toString();
    }

    @Override
    public String toString() {
        String completion = this.done ? "X" : " ";
        return "[" + completion + "] " + this.name;
    }
}
