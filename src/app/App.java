package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.io.IOException;
import java.io.Reader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class App {
    public static void main(String[] args) throws Exception {
        //Scanner gets the users public API key from 'publicKey.txt'
        Scanner in = new Scanner(new File("src\\app\\publicKey.txt"));
        String publicKey = in.next();
        in.close();

        //Simple test to get the top buy and sell prices of Superior Fragments
        String productID = "SUPERIOR_FRAGMENT";
        String url = "https://api.hypixel.net/skyblock/bazaar/product?key=" + publicKey + "&productId=" + productID;
        JSONObject json = readJsonFromUrl(url);
        json = json.getJSONObject("product_info");
        JSONArray buys = json.getJSONArray("buy_summary");//Array of buy orders
        JSONArray sells = json.getJSONArray("sell_summary");//Array of sell orders
        double topBuy = buys.getJSONObject(0).getDouble("pricePerUnit");
        double topSell = sells.getJSONObject(0).getDouble("pricePerUnit");
        System.out.println("Buy: " + topBuy + "  Sell: " + topSell);
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