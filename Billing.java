import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

class Inventory {

    String line = "";

    HashMap<String, HashMap<String, HashMap<String, Float>>> createInventory()
    {
        HashMap<String, HashMap<String, HashMap<String, Float>>> inventory = new HashMap<>();

        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader("./input_data/Dataset.csv"));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] row = line.split(",");    // use comma as separator

                if (row[1].equals("Essential") || row[1].equals("Luxury") || row[1].equals("Misc"))
                {
                    HashMap<String, Float> qtyPrice = new HashMap<String, Float>() {{
                        put("quantity", Float.parseFloat(row[2]));
                        put("price", Float.parseFloat(row[3]));
                    }};
                    HashMap<String, HashMap<String, Float>> itemType = new HashMap<>();
                    itemType.put(row[0], qtyPrice);

                    if (inventory.containsKey(row[1])) 
                    {
                        HashMap<String, HashMap<String, Float>> temp;
                        temp = inventory.get(row[1]);
                        temp.put(row[0], qtyPrice);
                    } 
                    else 
                    {
                        inventory.put(row[1], itemType);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(inventory);
        return inventory;
  }
}

class Orders {

    public String line = "";
    public  HashMap<String, Integer>  orders = new HashMap<>();

    HashMap<String, Integer> createOrder()
    {
        // Reading all the card numbers from csv file
        HashSet<String> cardInfo = new HashSet<>();
        ReadCard readCard = new ReadCard();
        cardInfo = readCard.createCardDB();

        try {
            BufferedReader br = new BufferedReader(new FileReader("./input_data/Input3.csv"));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] item = line.split(",");    // use comma as separator

                if ((item[0].equals("Item")) == false) {
                    orders.put(item[0], Integer.parseInt(item[1]));


                    if (!cardInfo.contains(item[2])) {
                        try {
                            cardInfo.add(item[2]);
                            FileWriter fw = new FileWriter("./input_data/Cards.csv", true);
                            fw.write(item[2] + "\n");
                            fw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return  orders;
    }
}

class Product {

    String description;
    Float price;

    Product(String description)
    {
        this.description = description;
    }

    Float getPrice()
    {
        return  price;
    }
}

class ValidateCart {

    public ArrayList<Product> prod = new ArrayList<>();

    // Creating Hashmap for all invalid items
    public HashMap<String,Integer> invalidItems = new HashMap<>();

    void validateCartItems(HashMap<String, HashMap<String, HashMap<String, Float>>> inventory )
    {
        HashMap<String, Integer> orders = new HashMap<>();
        Orders ordersMap = new Orders();
        orders = ordersMap.createOrder();

        // Creating Hashmap for limiting the category items
        HashMap<String, Integer> maxOrder = new HashMap<>();
        maxOrder.put("Essential",3);
        maxOrder.put("Luxury",4);
        maxOrder.put("Misc",6);

        HashMap<String, Integer> orderCap = new HashMap<>();
        // HashMap<String,Float> total = new HashMap<>();

        for (Map.Entry<String, Integer> orderList : orders.entrySet())
        {
            String orderItemKey = new String(orderList.getKey());
            if ((inventory.get("Essential")).containsKey(orderItemKey) || (inventory.get("Misc")).containsKey(orderItemKey) || (inventory.get("Luxury")).containsKey(orderItemKey))
            {
                for (Map.Entry<String, HashMap<String, HashMap<String, Float>>> inventoryItems : inventory.entrySet())
                {
                    String inventoryItemKey = new String(orderList.getKey());
                    if (inventoryItems.getValue().containsKey(orderItemKey))
                    {
                        // Condition to check if quantity is not greater than stock and also to check the cap per category
                        if ((inventoryItems.getValue().get(orderItemKey).get("quantity") )>= (float)orderList.getValue() &&  orderList.getValue() <= maxOrder.get(inventoryItems.getKey()))
                        {
                            if (orderCap.containsKey(inventoryItemKey))
                            {
                                if  (orderCap.get(inventoryItemKey) <= maxOrder.get(inventoryItemKey))
                                {
                                    orderCap.put(inventoryItemKey,orderCap.get(inventoryItemKey)+1);

                                    //Following Composite Design Pattern
                                    Product p = new Product(orderItemKey);
                                    p.price = (orderList.getValue())*inventoryItems.getValue().get(orderItemKey).get("price");
                                    prod.add(p);
                                }
                                else
                                {
                                    invalidItems.put(orderItemKey, orderList.getValue());
                                }
                            }
                            else
                            {
                                orderCap.put(inventoryItemKey,1);
                                Product p = new Product(orderItemKey);
                                p.price = (orderList.getValue())*inventoryItems.getValue().get(orderItemKey).get("price");
                                prod.add(p);
                            }
                        }
                        else
                        {
                            invalidItems.put(orderItemKey, orderList.getValue());
                        }
                    }
                }
            }
            else
            {
                invalidItems.put(orderItemKey, orderList.getValue());
            }
        }
    }
}

class ReadCard {
    HashSet<String> cardInfo = new HashSet<>();
    String line = "";

    HashSet<String> createCardDB(){

        try {

            BufferedReader br = new BufferedReader(new FileReader("./input_data/Cards.csv"));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] item = line.split(",");    // use comma as separator

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

class EvaluateCart {

    void evaluate(HashMap<String,Integer> invalidItems, ArrayList<Product> prod)
    {

        if (invalidItems.size() != 0){
            //generate text file function

            try {
                FileWriter myWriter = new FileWriter("./Output/Invalid-items.txt");
                myWriter.write("Please correct quantities. \n");
                for (Map.Entry<String, Integer> entry : invalidItems.entrySet()) {
                    myWriter.write(entry.getKey() + " : " + entry.getValue() + "\n");
                }
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Float sum = 0.0f;
            try (FileWriter fout = new FileWriter("./Output/Output.csv"))
            {
                for (Product p : prod) {
                    fout.write(p.description + "," + p.price + "\n");
                    sum += p.price;
                }
                fout.write("Total" + ","+ sum);
                fout.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}






public class Billing {
    public static void main(String[] args) {
        // Creating Inventory
        HashMap<String, HashMap<String, HashMap<String, Float>>> inventory = new HashMap<>();
        Inventory invt = new Inventory();
        inventory = invt.createInventory();

        // Validating the cart items
        ValidateCart vc = new ValidateCart();
        vc.validateCartItems(inventory);

        EvaluateCart cart = new EvaluateCart();
        cart.evaluate(vc.invalidItems, vc.prod);

        }
    }