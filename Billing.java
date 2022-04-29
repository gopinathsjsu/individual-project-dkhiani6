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
    String splitBy = ",";

    HashMap<String, HashMap<String, HashMap<String, Float>>> makeInventory()
    {
        HashMap<String, HashMap<String, HashMap<String, Float>>> inventory = new HashMap<>();

        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader("./input_data/Dataset - Sheet1.csv"));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] item = line.split(splitBy);    // use comma as separator

                if (item[0].equals("Essential") || item[0].equals("Luxury") || item[0].equals("Misc"))
                {
                    HashMap<String, Float> quantityPrice = new HashMap<String, Float>() {{
                        put("quantity", Float.parseFloat(item[2]));
                        put("price", Float.parseFloat(item[3]));
                    }};
                    HashMap<String, HashMap<String, Float>> itemType = new HashMap<>();
                    itemType.put(item[1], quantityPrice);

                    if (inventory.containsKey(item[0])) {
                        HashMap<String, HashMap<String, Float>> temp;
                        temp = inventory.get(item[0]);
                        temp.put(item[1], quantityPrice);
                    } else {
                        inventory.put(item[0], itemType);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return inventory;
  }
}

class Orders {

    public String line = "";
    public String splitBy = ",";
    public  HashMap<String, Integer>  orders = new HashMap<>();

    HashMap<String, Integer> createOrder()
    {
        // Reading all the card numbers from csv file
        HashSet<String> cardInfo = new HashSet<>();
        ReadCard readCard = new ReadCard();
        cardInfo = readCard.createCardDB();

        try {
            BufferedReader br = new BufferedReader(new FileReader("./input_data/Input3 - Sheet1.csv"));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] item = line.split(splitBy);    // use comma as separator

                if ((item[0].equals("Item")) == false) {
                    orders.put(item[0], Integer.parseInt(item[1]));


                    if (!cardInfo.contains(item[2])) {
                        try {
                            cardInfo.add(item[2]);
                            FileWriter fw = new FileWriter("./input_data/Cards - Sheet1.csv", true);
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
        HashMap<String, Integer> maxCapacity = new HashMap<>();
        maxCapacity.put("Essential",3);
        maxCapacity.put("Luxury",5);
        maxCapacity.put("Misc",6);

        HashMap<String, Integer> orderCap = new HashMap<>();
        HashMap<String,Float> total = new HashMap<>();

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
                        if ((inventoryItems.getValue().get(orderItemKey).get("quantity") )>= (float)orderList.getValue() &&  orderList.getValue() <= maxCapacity.get(inventoryItems.getKey()))
                        {
                            if (orderCap.containsKey(inventoryItemKey))
                            {
                                if  (orderCap.get(inventoryItemKey) <= maxCapacity.get(inventoryItemKey))
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
    String splitBy = ",";

    HashSet<String> createCardDB(){

        try {

            BufferedReader br = new BufferedReader(new FileReader("./input_data/Cards - Sheet1.csv"));
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

class CartProcessing {

    void processCart(HashMap<String,Integer> invalidItems, ArrayList<Product> prod)
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
        inventory = invt.makeInventory();

        // Validating the cart items
        ValidateCart vc = new ValidateCart();
        vc.validateCartItems(inventory);

        CartProcessing cart = new CartProcessing();
        cart.processCart(vc.invalidItems, vc.prod);

        }
    }