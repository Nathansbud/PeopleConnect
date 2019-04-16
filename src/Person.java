import java.util.ArrayList;

public class Person {
    private String name;

    private ArrayList<Connection> connections = new ArrayList<>();
    private String[] connectionList;

    public Person() {

    }

    public Person(String _name, ArrayList<Connection> _connections) {
        name = _name;
        connections = _connections;
    }

    public Person(String _name, String[] _connectionList) {
        name = _name;
        connectionList = _connectionList;
    }

    public String getName() {
        return name;
    }
    public void setName(String _name) {
        name = _name;
    }

    public void setConnections(ArrayList<Connection> _connections) {
        connections = _connections;
    }
    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public void addConnection(Person p, Connection.Type t) {
        connections.add(new Connection(this, p, t));
    }
    public void changeConnectionType(Person p, Connection.Type t) {
        for(Connection c : connections) {
            if(c.getTo().equals(p)) {
                c.setType(t);
            }
        }
    }

    public void setConnectionList(String[] _connectionList) {
        connectionList = _connectionList;
    }
    public String[] getConnectionList() {
        return connectionList;
    }
}
