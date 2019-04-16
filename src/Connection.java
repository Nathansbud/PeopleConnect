public class Connection {
    public enum Type {
        Friend(0),
        Romance(1),
        Family(2),
        Acquaintance(3)

        ;

        //Methods
        Type(int _value) {
            value = _value;
        }
        private final int value;
        public final int getValue() {
            return value;
        }
    }

    private Person to;
    private Person from;
    private Type type;

    public Connection(Person _from, Person _to, Type _type) {
        from = _from;
        to = _to;

        type = _type;

    }

    public Person getTo() {
        return to;
    }
    public void setTo(Person _to) {
        to = _to;
    }

    public Person getFrom() {
        return from;
    }
    public void setFrom(Person _from) {
        from = _from;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type _type) {
        type = _type;
    }

    @Override
    public String toString() {
        return to.getName() + " - " + type.toString();
    }
}
