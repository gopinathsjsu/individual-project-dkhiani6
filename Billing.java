import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class Billing{
    public static void main (String [] args){
    //     HashMap <String, ArrayList<String>> inventory = new HashMap<String, ArrayList<String>> ();
    //     ArrayList <String> details = new ArrayList <String> ();
    //     details.add("Essentials");
    //     details.add("20");
    //     details.add("70");
    //     inventory.put("Milk",details);
    //     System.out.println(inventory);

    }
}


public class Order {

    public String line = "";
    public  HashMap<String, Integer>  orders = new HashMap<>();

    HashMap<String, Integer> createOrder()
    {
        
        HashSet<String> cards = new HashSet<>();
        ReadCard readCard = new ReadCard();
        cardsDB = readCard.createCardDB();

            BufferedReader br = new BufferedReader(new FileReader("Input3 - Sheet1.csv"));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] item = line.split(",");    

                if ((item[0].equals("Item")) == false) {
                    orders.put(item[0], Integer.parseInt(item[1]));

                    if (!cardsDB.contains(item[2])) {
                            cardsDB.add(item[2]);
                            FileWriter fw = new FileWriter("./Cards - Sheet1.csv", true);
                            fw.write(item[2] + "\n");
                            fw.close();
                    }
                }
            }
        return  orders;
    }
}


public class ReadCard {
    HashSet<String> cardInfo = new HashSet<>();
    String line = "";
    String splitBy = ",";

    HashSet<String> createCardDB(){

        try {

            BufferedReader br = new BufferedReader(new FileReader("Cards - Sheet1.csv"));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] item = line.split(splitBy);    // use comma as separator

                if ((item[0].equals("CardNumber"))==false){
                    cardInfo.add(item[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardInfo;
    }
}

