import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

/**
 * Main class for this project.
 */
public class Whist {

    public static void main(String[] args) throws IOException {
        Properties whistPropertie = new Properties();
        FileReader inStream = null;
        try {
            inStream = new FileReader("original" +
                    ".properties");
            whistPropertie.load(inStream);
        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }
        //To show information at the beginning.

        GameInformation.nbPlayers = Integer.parseInt(whistPropertie.getProperty("nbPlayers"));
        System.out.println("The number of Players: " + GameInformation.nbPlayers);

        GameInformation.nbStartCards = Integer.parseInt(whistPropertie.getProperty("nbStartCards"));
        System.out.println("Player starts with "+ GameInformation.nbStartCards + " cards");

        GameInformation.winningScore = Integer.parseInt(whistPropertie.getProperty("winningScore"));
        System.out.println("Winning Score is " + GameInformation.winningScore);

        GameInformation.Interactive_Player = Integer.parseInt(whistPropertie.getProperty("Interactive_Player"));
        System.out.println("The number of Interactive Player: " + GameInformation.Interactive_Player);

        GameInformation.Legal_NPC = Integer.parseInt(whistPropertie.getProperty("Legal_NPC"));
        System.out.println("The number of Random NPC: " + GameInformation.Legal_NPC);

        GameInformation.Smart_NPC = Integer.parseInt(whistPropertie.getProperty("Smart_NPC"));
        System.out.println("The number of Smart NPC: " + GameInformation.Smart_NPC);

        GameInformation.Seed = Integer.parseInt(whistPropertie.getProperty("Seed"));

        if(GameInformation.Seed == 30006){
            System.out.println("Play with fixed Seed: " + GameInformation.Seed);

            GameInformation.random = new Random((long) GameInformation.Seed);
        }
        else {
            System.out.println("No Seed");
        }
        // System.out.println("Working Directory = " + System.getProperty("user.dir"));
        new GameManagement();
    }


}
