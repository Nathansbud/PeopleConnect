import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*; //Soon...

public class PeopleConnect {
    public static void main(String[] args) {
        ArrayList<Person> people = new ArrayList<>();

        File folder = new File("people" + File.separator);

        for(File f : folder.listFiles()) {
            if(!(f.getName().equals(".DS_Store") || f.getName().equals(".gitkeep"))) {
                try {
                    List<String> connects = Files.readAllLines(Paths.get(f.getPath()), StandardCharsets.UTF_8);
                    people.add(new Person(f.getName().substring(0, f.getName().lastIndexOf(".")), connects.toArray(new String[connects.size()])));
                } catch(IOException e) {
                    System.out.println("IOException caught at file read-in");
                }
            }
        }
        for(Person p : people) {
            for(String l : p.getConnectionList()) {
                String cType = l.substring(l.lastIndexOf("-")+1);
                String cName = l.substring(0, l.lastIndexOf("-"));
                Person connectTo = new Person();

                boolean passed = false;

                for(Person i : people) {
                    if(i.getName().equals(cName)) {
                        connectTo = i;
                        passed = true;
                    }
                }

                if(!passed) {
                    try {
                        File f = new File(folder + File.separator + cName + ".txt");
                        boolean toss = f.createNewFile();
                    }
                    catch(IOException e) {
                        System.out.println("Creating File exception");
                    }
                } else {
                    p.addConnection(connectTo, Connection.Type.valueOf(cType));
                }
            }
        }

        for(Person p : people) {
            System.out.println(p.getName() + ":");
            for(Connection c : p.getConnections()) {
                System.out.println(" - " + c.toString());
            }
        }
    }
}
