import java.util.ArrayList;

import processing.core.PApplet;

public class Person extends PApplet {
    private PApplet sketch;

    private float x;
    private float y;
    private float r;

    private float storedX;
    private float storedY;

    static float centerX;
    static float centerY;

    private String name;
    private boolean selected;

    private ArrayList<Connection> connections = new ArrayList<>();
    private ArrayList<String> connectionList;

    public Person() {

    }

    public Person(PApplet _sketch, String _name, ArrayList<String> _connectionList) {
        sketch = _sketch;
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

    public void addConnection(Person p, Connection.Type t, boolean s) {
        connections.add(new Connection(this, p, t, s));
    }
    public void changeConnectionType(Person p, Connection.Type t) {
        for(Connection c : connections) {
            if(c.getTo().equals(p)) {
                c.setType(t);
            }
        }
    }

    public void setConnectionList(ArrayList<String> _connectionList) {
        connectionList = _connectionList;
    }
    public ArrayList<String> getConnectionList() {
        return connectionList;
    }

    public void setDimensions(float _x, float _y, float _r) {
        x = _x;
        y = _y;
        r = _r;
    }
    public void setPosition(float _x, float _y) {
        x = _x;
        y = _y;
    }

    public void setX(float _x) {
        x = _x;
    }
    public float getX() {
        return x;
    }

    public void setY(float _y) {
        y = _y;
    }
    public float getY() {
        return y;
    }
    public void setR(float _r) {
        r = _r;
    }
    public float getR() {
        return r;
    }

    public boolean isTouched() {
        return sketch.mouseX < x+r/2.0f && sketch.mouseX > x-r/2.0f && sketch.mouseY < y+r/2.0f && sketch.mouseY > y-r/2.0f;
    }

    public void setSelected(boolean _selected) {
        selected = _selected;
    }
    public boolean isSelected() {
        return selected;
    }

    public void drawConnections() {
        for(Connection c : connections) {
            sketch.stroke(unhex(c.getColor()));
            sketch.line(x, y, c.getTo().getX(), c.getTo().getY()); //Right now it's 2n lines for every n connections; need to fix this
        }
    }

    public void drawNode() {
        sketch.stroke(0);
        sketch.fill(255);
        sketch.ellipse(x, y, r, r);
        sketch.fill(0);
        sketch.textSize(8);
        sketch.text(String.valueOf(name.charAt(0)) + String.valueOf(name.charAt(name.lastIndexOf(" ")+1)), x - r/4, y);
        sketch.textSize(12);
    }

    public static void setCenterX(float _centerX) {
        centerX = _centerX;
    }
    public static void setCenterY(float _centerY) {
        centerY = _centerY;
    }

    public void setStoredX(float _storedX) {
        storedX = _storedX;
    }
    public void setStoredY(float _storedY) {
        storedY = _storedY;
    }
    public void setStoredPosition(float _x, float _y) {
        storedX = _x;
        storedY = _y;
    }

    public float getStoredX() {
        return storedX;
    }
    public float getStoredY() {
        return storedY;
    }
}
