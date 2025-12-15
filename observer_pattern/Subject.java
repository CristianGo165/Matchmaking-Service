package observer_pattern;

public interface Subject {
    void add(Observer o);
    void remove(Observer o);
    void alert();
}
