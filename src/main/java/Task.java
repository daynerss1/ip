public class Task {
    private String name;
    private boolean done;

    public Task(String name) {
        this.name = name;
        this.done = false;
    }

    public void mark() {
        System.out.println("Nice! I've marked this task as done:");
        this.done = true;
        System.out.println(this.toString());

    }

    public void unmark() {
        System.out.println("OK, I've marked this task as not done yet:");
        this.done = false;
        System.out.println(this.toString());
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
