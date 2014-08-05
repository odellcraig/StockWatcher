package com.google.gwt.sample.stockwatcher.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.apache.commons.io.IOUtils;

import com.google.gwt.sample.stockwatcher.client.DelistedException;
import com.google.gwt.sample.stockwatcher.client.StockPrice;
import com.google.gwt.sample.stockwatcher.client.StockPriceService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class StockPriceServiceImpl extends RemoteServiceServlet implements StockPriceService {

    private static final double MAX_PRICE = 100.0; // $100.00
    private static final double MAX_PRICE_CHANGE = 0.02; // +/- 2%

    @Override
    public StockPrice[] getPrices(String[] symbols) throws DelistedException {

        StockPrice[] prices = new StockPrice[symbols.length];
        if (symbols.length == 0) {
            return prices;
        }

        InputStream input = null;
        try {
            String q = "DHR+GOOG+AAPL";
            input = new URL("http://finance.yahoo.com/d/quotes.csv?s=" + q + "&f=nsl1op&e=.csv").openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(convertStreamToString(input));

        // http://finance.yahoo.com/d/quotes.csv?s=AAPL+GOOG+MSFT?f=nb
        Random rnd = new Random();
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i].equals("ERR")) {
                throw new DelistedException("ERR");
            }
            double price = rnd.nextDouble() * MAX_PRICE;
            double change = price * MAX_PRICE_CHANGE * (rnd.nextDouble() * 2f - 1f);

            prices[i] = new StockPrice(symbols[i], price, change);
        }

        return prices;
    }
    
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
