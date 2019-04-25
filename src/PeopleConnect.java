import java.io.*;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class PeopleConnect extends PApplet {
    private static ArrayList<Person> people = new ArrayList<>();
    private static File folder = new File("people");
    private static PeopleConnect ms = new PeopleConnect(); //Used for PApplet to work
    private static float MAJOR_RADIUS; //"Inner circle" that nodes are centered around
    private static float MINOR_RADIUS; //Node radius

    private static boolean showLegend = true; //Top right "legend" box boolean
    private static boolean selectedToCenter = true; //Should nodes move to center on selection?

    private static Person selected = null; //Used for selection or new relationship creating
    private static Person linkTo = null; //Used for new relationship creating

    private static Button connectionButtons[] = new Button[2]; //Buttons for creating connections

    private static Button selectedButton = null; //Loads in button on click
    private static boolean selectState = false; //Boolean for connection-making mode
    private static String title = "PeopleConnect"; //Text centered at the top

    public void setup() {
        MAJOR_RADIUS = width/3.8f;
        MINOR_RADIUS = width/72.0f;

        for(int i = 0; i < people.size(); i++) {
            //Basic trig, all nodes have angle given by π/2 + 2π/NodeCount, to start centered vertically and rotate around "major" inner circle
            people.get(i).setDimensions((float)(width/2.0f - (MAJOR_RADIUS+MINOR_RADIUS)*Math.cos(i*2*Math.PI/people.size()+Math.PI/2)), (float)(height/2.0f - (MAJOR_RADIUS+MINOR_RADIUS)*Math.sin(i*2*Math.PI/people.size() + Math.PI/2)), MINOR_RADIUS);
        }

        Person.setCenterX(width/2.0f);
        Person.setCenterY(height/2.0f);

        connectionButtons[0] = new Button(ms, "2-Way Connect", width - width/14.4f, height - 2*height/30.0f, width/36.0f, height/45.0f, 6);
        connectionButtons[1] = new Button(ms, "1-Way Connect", width - width/14.4f, height - height/30.0f, width/36.0f, height/45.0f, 6);
    }

    public void settings() {
        fullScreen();
    }

    public void draw() {
        background(0);
        fill(255);
        textSize(16);
        text(title, width/2.0f - 0.5f*textWidth(title), height/30.0f); //Title shows current program state

        if (selected == null) {
            for (Person p : people) {
                p.drawConnections();
            }
        } else {
            selected.drawConnections(); //Only show chosen connections
        }


        if(selectState) {
            if(selected != null && linkTo == null) {
                stroke(255);
                line(selected.getX(), selected.getY(), mouseX, mouseY); //Draw linking line
            } else if(selected != null && linkTo != null) {
                stroke(255);
                line(selected.getX(), selected.getY(), linkTo.getX(), linkTo.getY());
            }
        }

        for (Person p : people) {
            p.drawNode();
        }

        for(Button b : connectionButtons) {
            b.draw();
        }

        strokeWeight(2);
        for (Person p : people) {
            if (p.isTouched()) {
                stroke(0, 0, 255);
                fill(255);
                rect(0, 0, 200, 40 + 15 * p.getConnections().size());
                stroke(0);
                fill(0);
                text("Name: " + p.getName(), width/144.0f, height/60.0f);
                if (p.getConnections().size() > 0) {
                    text("Connections: ", width/144.0f, height/60.0f*2);
                    for (int i = 0; i < p.getConnections().size(); i++) {
                        stroke(unhex(p.getConnections().get(i).getColor()));
                        line(width/96.0f, height/22.5f + height/60.0f * i, width/57.6f, height/22.5f + height/60.0f * i);
                        text(p.getConnections().get(i).getTo().getName(), 2*width/96.0f, height/20.0f + height/60.0f * i);
                    }
                }
            }
        }

        if (showLegend && Connection.Type.values().length > 0) {
            fill(255);
            stroke(0, 0, 255);
            rect(width - width/7.2f, 0, width/7.2f, height/45.0f + height/60.0f * Connection.Type.values().length);
            fill(0);
            for (int i = 0; i < Connection.Type.values().length; i++) {
                stroke(unhex(Connection.getColor(Connection.Type.values()[i])));
                line(width - width/144.0f*19, height/60.0f + height/60.0f * i, width - width/144.0f*14, height/60.0f + height/60.0f * i);
                text(Connection.Type.values()[i].toString(), width - width/10.7f, height/45.0f + height/60.0f * i);
            }
        }
        strokeWeight(1);
    }

    public void mouseClicked() {
        checkSelected();
    }

    public static void checkSelected() {
        for(Button b : connectionButtons) {
            if(b.isTouched()) {
                for(Button de : connectionButtons) {
                    if(!de.equals(b)) {
                        de.setSelected(false);
                    }
                }
                b.setSelected(!b.isSelected());
                selectState = b.isSelected();
                if(selected != null) {
                    selected.setPosition(selected.getStoredX(), selected.getStoredY());
                    selected = null;
                }
                selectedButton = b;
                title = b.getText();
            }
        }

        for(Person p : people) {
            if(p.isTouched() && !selectState) {
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
                    title = "Selected: " + selected.getName();
                } else {
                    selected = null;
                    p.setSelected(false);
                    if(selectedToCenter) {
                        p.setPosition(p.getStoredX(), p.getStoredY());
                    }
                    title = "PeopleConnect";
                }
            } else if(p.isTouched() && selectState) { //Handle Connection button
                if(selected == null) {
                    selected = p;
                    selected.setSelected(true);
                    title = selectedButton.getText() + " - From: " + selected.getName();
                } else if(selected != null && selected != p) {
                    linkTo = p;
                    linkTo.setSelected(true);
                    title = selectedButton.getText() + " - From: " + selected.getName() + " - To: " + linkTo.getName();

                    selected.addConnection(linkTo, Connection.Type.Friend, true);
                    if(selectedButton.equals(connectionButtons[0])) {
                        linkTo.addConnection(selected, Connection.Type.Friend, true);
                        try {
                            PrintWriter f = new PrintWriter(new BufferedWriter(new FileWriter(folder + File.separator + selected.getName() + ".txt", true)));
                            f.println(linkTo.getName() + "-1-Friend");
                            f.close();
                            f = new PrintWriter(new BufferedWriter(new FileWriter(folder + File.separator + linkTo.getName() + ".txt", true)));
                            f.println(selected.getName() + "-1-Friend");
                            f.close();
                        } catch(IOException e) {
                            System.out.println("FileWrite failed on 2-way connect add!");
                        }
                    } else if(selectedButton.equals(connectionButtons[1])) {
                        try {
                           PrintWriter f = new PrintWriter(new BufferedWriter(new FileWriter(folder + File.separator + selected.getName() + ".txt", true)));
                           f.println(linkTo.getName() + "-0-Friend");
                           f.close();
                       } catch(IOException e) {
                           System.out.println("FileWriter failed on 1-way connect add!");
                       }
                    }
                    selected.setSelected(false);
                    linkTo.setSelected(false);

                    selected = null;
                    linkTo = null;
                    selectedButton = null;

                    selectState = false;
                    for(Button b : connectionButtons) {
                        b.setSelected(false);
                    }
                    title = "PeopleConnect";
                } else if(selected == p) {
                    selected.setSelected(false);
                    selected = null;
                    title = selectedButton.getText();
                } else if(linkTo == p) {
                    linkTo.setSelected(false);
                    linkTo = null;
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
        }

        for(int n = 0; n < people.size(); n++) {
            Person p = people.get(n);
            for(String s : p.getConnectionList()) {
                if (s.length() > 0) {
                    String cName = s.substring(0, s.indexOf("-"));
                    String cShared = s.substring(s.indexOf("-") + 1, s.lastIndexOf("-"));
                    String cType = s.substring(s.lastIndexOf("-") + 1);
                    boolean exists = false;

                    for (Person match : people) {
                        if (match.getName().equals(cName)) {
                            exists = true;
                            if (cShared.equals("1")) {
                                if (match.getConnectionList().contains(p.getName() + "-" + cShared + "-" + cType)) {
                                    break;
                                } else {
                                    try {
                                        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(folder + File.separator + cName + ".txt", true)));
                                        writer.println(p.getName() + "-" + cShared + "-" + cType);
                                        writer.close();
                                    } catch (IOException e) {
                                        System.out.println("IOException @ write relationship step");
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }
                    if (!exists) {
                        try {
                            File f = new File(folder + File.separator + cName + ".txt");
                            boolean toss = f.createNewFile();

                            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
                            if (cShared.equals("1")) {
                                writer.println(p.getName() + "-" + cShared + "-" + cType);
                            }
                            writer.close();
                            List<String> lines = Files.readAllLines(Paths.get(f.getPath()), StandardCharsets.UTF_8);
                            Person np = new Person(ms, cName, new ArrayList<>(lines));
                            people.add(np);
                        } catch (IOException e) {
                            System.out.println("Creating File exception");
                        }
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
                if(s.length() > 0) {
                    for (Person m : people) { //double loop (horrible, horrible) for object refs
                        if (s.substring(0, s.indexOf("-")).equals(m.getName())) {
                            try {
                                p.addConnection(m, Connection.Type.valueOf(s.substring(s.lastIndexOf("-") + 1)), (s.charAt(s.indexOf("-") + 1) == 1));
                                break;
                            } catch (IllegalArgumentException e) {
                                System.out.println("Non-existent relationship type between " + p.getName() + " and " + m.getName());
                            }
                            break;
                        }
                    }
                }
            }
        }

        String[] processingArgs = {"PeopleConnect"};
        PApplet.runSketch(processingArgs, ms);
    }
}
