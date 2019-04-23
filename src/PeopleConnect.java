import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class PeopleConnect extends PApplet {
    private static ArrayList<Person> people = new ArrayList<>();
    private static File folder = new File("people");
    private static PeopleConnect ms = new PeopleConnect();
    private float MAJOR_RADIUS = 380.0f;
    private float MINOR_RADIUS = 20.0f;

    private boolean showLegend = true;
    private boolean selectedToCenter = true;

    private Person selected = null;



    public void setup() {
        for(int i = 0; i < people.size(); i++) {
            people.get(i).setDimensions((float)(width/2.0f - (MAJOR_RADIUS+MINOR_RADIUS)*Math.cos(i*2*Math.PI/people.size()+Math.PI/2)), (float)(height/2.0f - (MAJOR_RADIUS+MINOR_RADIUS)*Math.sin(i*2*Math.PI/people.size() + Math.PI/2)), MINOR_RADIUS);
        }
        Person.setCenterX(width/2.0f);
        Person.setCenterY(height/2.0f);
    }

    public void settings() {
        fullScreen();
    }

    public void draw() {
        background(0);

        if (selected == null) {
            for (Person p : people) {
                p.drawConnections();
            }
        } else {
            selected.drawConnections();
        }

        for (Person p : people) {
            p.drawNode();
        }

        for (Person p : people) {
            if (p.isTouched()) {
                stroke(0, 0, 255);
                fill(255);
                rect(0, 0, 200, 40 + 15 * p.getConnections().size());
                stroke(0);
                fill(0);
                text("Name: " + p.getName(), 10, 15);
                if (p.getConnections().size() > 0) {
                    text("Connections: ", 10, 30);
                    for (int i = 0; i < p.getConnections().size(); i++) {
                        strokeWeight(2);
                        stroke(unhex(p.getConnections().get(i).getColor()));
                        line(15, 40 + 15 * i, 25, 40 + 15 * i);
                        text(p.getConnections().get(i).getTo().getName(), 30, 45 + 15 * i);
                    }
                }
            }
        }

        if (showLegend && Connection.Type.values().length > 0) {
            fill(255);
            stroke(0, 0, 255);
            rect(width - 200, 0, 200, 20 + 15 * Connection.Type.values().length);
            fill(0);
            for (int i = 0; i < Connection.Type.values().length; i++) {
                stroke(unhex(Connection.getColor(Connection.Type.values()[i])));
                line(width - 190, 15 + 15 * i, width - 140, 15 + 15 * i);
                text(Connection.Type.values()[i].toString(), width - 135, 20 + 15 * i);
            }
        }
        strokeWeight(1);
    }

    public void mouseClicked() {
        for(Person p : people) {
            if(p.isTouched()) {
                if(p != selected) {
                    if(selected != null && selectedToCenter) {
                        selected.setPosition(selected.getStoredX(), selected.getStoredY());
                    }
                    selected = p;
                    p.setSelected(true);
                    if(selectedToCenter) {
                        p.setStoredPosition(p.getX(), p.getY());
                        p.setPosition(Person.centerX, Person.centerY);
                    }
                } else {
                    selected = null;
                    p.setSelected(false);
                    if(selectedToCenter) {
                        p.setPosition(p.getStoredX(), p.getStoredY());
                    }
                }
            }
        }
    }

    public static void loadPeople() {
        for (File f : folder.listFiles()) {
            if (!(f.getName().equals(".DS_Store") || f.getName().equals(".gitkeep"))) {
                try {
                    people.add(new Person(ms, f.getName().substring(0, f.getName().lastIndexOf(".")), new ArrayList<String>(Files.readAllLines(Paths.get(f.getPath()), StandardCharsets.UTF_8))));
                } catch (IOException e) {
                    System.out.println("IOException on connect file read");
                }
            }
        } //Should load in every "existing" person

        for(int n = 0; n < people.size(); n++) { //for instead of foreach due to modification of people
            Person p = people.get(n);
            for(String s : p.getConnectionList()) {
                String cType = s.substring(s.lastIndexOf("-")+1);
                String cName = s.substring(0, s.lastIndexOf("-"));
                Person connectTo = new Person();

                boolean exists = false;

                for(Person match : people) {
                    if(match.getName().equals(cName)) {
                        exists = true;
                        if(match.getConnectionList().contains(p.getName()+"-"+cType)) {
                            connectTo = match; //check re-writing stuff
                        } else {
                            boolean found = false;

                            for(String matchType : match.getConnectionList()) {
                                if(matchType.substring(0, matchType.lastIndexOf("-")).equals(p.getName())) {
                                    found = true;
                                    System.out.println("Not sure how to handle this yet");
                                }
                            }
                            if(!found) {
                                try {
                                    PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(folder + File.separator + cName + ".txt", true)));
                                    writer.write("\n" + p.getName() + "-" + cType);
                                    writer.close();
                                } catch(IOException e) {
                                    System.out.println("IOException @ write relationship step");
                                }
                            }
                        }
                    }
                }

                if(!exists) {
                    try {
                        File f = new File(folder + File.separator + cName + ".txt");
                        boolean toss = f.createNewFile();

                        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(f)));
                        writer.write(p.getName()+"-"+cType);
                        writer.close();
                        List<String> lines = Files.readAllLines(Paths.get(f.getPath()), StandardCharsets.UTF_8);

                        Person np = new Person(ms, cName, new ArrayList<>(lines));
                        people.add(np);
                    }
                    catch(IOException e) {
                        System.out.println("Creating File exception");
                    }
                }
            }
        }
        for(Person p : people) {
            try {
                p.setConnectionList(new ArrayList<>(Files.readAllLines(Paths.get(folder + File.separator + p.getName() + ".txt"), StandardCharsets.UTF_8)));
            } catch(IOException e) {
                System.out.println("OWO");
            }
        }
    }

    public static void main(String[] args) {
        loadPeople();
        for(Person p : people) {
            for(String s : p.getConnectionList()) {
                for(Person m : people) {
                    if(s.substring(0, s.lastIndexOf("-")).equals(m.getName())) {
                        try {
                            p.addConnection(m, Connection.Type.valueOf(s.substring(s.lastIndexOf("-")+1)));
                        } catch(IllegalArgumentException e) {
                            System.out.println("Non-existent relationship type between " + p.getName() + " and " + m.getName());
                        }
                        break;
                    }
                }
            }
        }

//        for(Person p : people) {
//            System.out.println(p.getName()+":");
//            for(Connection c : p.getConnections()) {
//                System.out.println("- "+ c.getTo().getName() + " (" + c.getType() + ")");
//            }
//        }

        String[] processingArgs = {"PeopleConnect"};
        PApplet.runSketch(processingArgs, ms);
    }
}
