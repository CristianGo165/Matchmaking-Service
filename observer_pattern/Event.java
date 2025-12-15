package observer_pattern;

import java.util.ArrayList;
import java.util.List;

public class Event implements Subject{
        private final List<Observer> observers = new ArrayList<>();
        @Override
        public void add(Observer o) {
            observers.add(o);
        }
        @Override
        public void remove(Observer o) {
            observers.remove(o);
        }
        @Override
        public void alert(){
            for(Observer o : observers){
                o.update();
            }
        }
    }
