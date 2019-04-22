import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ddf.minim.*;
import processing.core.PApplet;

public class PeopleConnect extends PApplet {
    private static ArrayList<Person> people = new ArrayList<>();
    private static File folder = new File("people");
    private static Minim minim;
    private static AudioPlayer player;

    public void setup() {
        minim = new Minim(this);
        player = minim.loadFile("/Users/zackamiton/Music/iTunes/iTunes Media/Music/Secret Songs/shh#ffb6c1/04 Flamingo.mp3");
        player.play();
    }

    public void settings() {
        fullScreen();
    }

    public void draw() {
        background(0);
        ellipse(mouseX, mouseY, 30, 30);
    }

    public void keyPressed() {
        System.out.println("Owo");
    }

    public static void loadPeople() {
        for (File f : folder.listFiles()) {
            if (!(f.getName().equals(".DS_Store") || f.getName().equals(".gitkeep"))) {
                try {
                    people.add(new Person(f.getName().substring(0, f.getName().lastIndexOf(".")), new ArrayList<>(Files.readAllLines(Paths.get(f.getPath()), StandardCharsets.UTF_8))));
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
                            connectTo = match;
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
                                    BufferedWriter writer = new BufferedWriter(new FileWriter(folder + File.separator + cName + ".txt", true));
                                    writer.write(p.getName() + "-" + cType+"\n");
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

                        BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                        writer.write(p.getName()+"-"+cType+"\n");
                        writer.close();
                        List<String> lines = Files.readAllLines(Paths.get(f.getPath()), StandardCharsets.UTF_8);

                        Person np = new Person(cName, new ArrayList<>(lines));
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
                        p.addConnection(m, Connection.Type.valueOf(s.substring(s.lastIndexOf("-")+1)));
                        break;
                    }
                }
            }
        }

        for(Person p : people) {
            System.out.println(p.getName()+":");
            for(Connection c : p.getConnections()) {
                System.out.println("- "+ c.getTo().getName() + " (" + c.getType() + ")");
            }
        }

        PApplet.main("PeopleConnect", args);
    }
}
