package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.io.IOException;
import java.io.Reader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class App {
    private static String publicKey;
    private static HashMap<String,ArrayList<Double>> products;
    public static void main(String[] args) throws Exception {
        //Scanner gets the users public API key from 'publicKey.txt'
        Scanner in = new Scanner(new File("src\\app\\publicKey.txt"));
        publicKey = in.next();
        in.close();

        products = new HashMap<String,ArrayList<Double>>();

        String url = "https://api.hypixel.net/skyblock/bazaar/products?key=" + publicKey;
        JSONObject productsJSON = readJsonFromUrl(url);
        JSONArray productsArray = productsJSON.getJSONArray("productIds");
        for(Object p : productsArray){
            products.put(p.toString(),new ArrayList<Double>());
        }

        while(true){
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String timeStamp = dateFormat.format(date);

            //Get the info on all the products and put it into the Hash Map
            int count = 0;//Counter that pauses the inquiry every 50 items
            for(String p : products.keySet()) {
                getProductDetails(p);
                if(count > 50){
                    Thread.sleep(15000);
                    count = 0;
                }
                Thread.sleep(200);
                count++;
            }

            for(String p : products.keySet()){
                File file = new File("data\\"+p+".csv");
                if(!file.exists()){file.createNewFile();}
                FileWriter out = new FileWriter(file, true);
                //out.write("Time,Buy,Sell\n");
                out.write(timeStamp+","+products.get(p).get(0)+","+products.get(p).get(1)+",\n");
                out.close();
            }
            System.out.println("New Entry: " + timeStamp);
            Thread.sleep(1000*60*9);
        }
    }

    private static void getProductDetails(String productID) throws JSONException, IOException {
        String url = "https://api.hypixel.net/skyblock/bazaar/product?key=" + publicKey + "&productId=" + productID;
        JSONObject json = readJsonFromUrl(url);
        json = json.getJSONObject("product_info");
        JSONArray buys = json.getJSONArray("buy_summary");//Array of buy orders
        JSONArray sells = json.getJSONArray("sell_summary");//Array of sell orders
        double topBuy = 0;
        double topSell = 0;

        if(!buys.isEmpty()){
            topBuy = buys.getJSONObject(0).getDouble("pricePerUnit");
        } else {//No Buy Offers
            topBuy = -1;
        }

        if(!sells.isEmpty()){
            topSell = sells.getJSONObject(0).getDouble("pricePerUnit");
        } else {//No Sell Offers
            topSell = -1;
        }
        topSell = Math.round(topSell * 100.0)/100.0;//Round to hundreths place
        topBuy = Math.round(topBuy * 100.0)/100.0;
        products.get(productID).add(topBuy);
        products.get(productID).add(topSell);
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
    
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}