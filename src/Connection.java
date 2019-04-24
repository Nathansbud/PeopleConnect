public class Connection {
    public enum Type {
        Friend(0),
        Relationship(1),
        Ex(2),
        Family(3),
        Acquaintance(4),;

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

    public String getColor() {
        return getColor(type);
    }

    static String getColor(Type t) {
        switch(t) {
            case Family:
                return "ff0000ff";
            case Relationship:
                return "ffff0000";
            case Ex:
                return "ff00ffff";
            case Friend:
                return "ff00ff00";
            case Acquaintance:
                return "ff969696";
            default:
                return "ffffffff";
        }
    }
}
