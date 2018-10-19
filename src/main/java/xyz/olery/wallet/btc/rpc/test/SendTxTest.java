package xyz.olery.wallet.btc.rpc.test;

/**
 *
 * 参考：https://blog.csdn.net/wypeng2010/article/details/81357265
 */

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.bitcoinj.core.*;
import org.bitcoinj.params.RegTestParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendTxTest {




    public static void main(String[] args) throws Exception {

//        NetworkParameters params = MainNetParams.get();
        NetworkParameters params = RegTestParams.get();
        String seedCode = "tuna biology crawl bone bread chalk light there pattern borrow afraid inherit";

        /**
         [{
         "address": "mgcqjgfYxh4cVEzMqwDiUr8XRx1t8DhrGJ",
         "txid": "6ef4eadcff7f8fb628f4e9952e928b1954de31050a7163fc17bbcbee9bb56436",
         "vout": 0,
         "scriptPubKey": "76a9140c1525aaa4fb519301e7a477b64a932b20f7ff2f88ac",
         "amount": 31,
         "satoshis": 3100000000,
         "height": 552,
         "confirmations": 20
         }, {
         "address": "mgcqjgfYxh4cVEzMqwDiUr8XRx1t8DhrGJ",
         "txid": "b49d9d8a787670f589f5062313f2248cc4d28d2180dacdc3bd3579af3de6dbf9",
         "vout": 1,
         "scriptPubKey": "76a9140c1525aaa4fb519301e7a477b64a932b20f7ff2f88ac",
         "amount": 31,
         "satoshis": 3100000000,
         "height": 532,
         "confirmations": 40
         }, {
         "address": "mgcqjgfYxh4cVEzMqwDiUr8XRx1t8DhrGJ",
         "txid": "c41f4f8c7eaefcdde573d56a8ffbc888432bc770d97f0bab7b1bc8e311064c51",
         "vout": 0,
         "scriptPubKey": "76a9140c1525aaa4fb519301e7a477b64a932b20f7ff2f88ac",
         "amount": 10,
         "satoshis": 1000000000,
         "height": 502,
         "confirmations": 70
         }]
         */
        String url = "http://192.168.10.122:3001/insight-api/tx/send";
        SendTxTest sendTxTest = new SendTxTest();
        String txSignedTxt = "01000000020b3b45950efeba955647c3d186126e74ff418f497cc3622c14014a8655ac59b2010000006a47304402205d098e2bc72128ac6e4be438f57cc6731ab2f1987f5be5ed98f5298b8a1e7d8202207ec85bd79d4fbd4365b8ea93ed0c3c653ea9d75ff3c39195b4c9fde680665379812102d263bf65cc2b6c58c72c5998047d07c8d16162ce0e4a5df404e157eeac96819bffffffffa5a58ce00e5e28bf8c07e76e03dd6ab2e171b9d583fc9cf43d9e85f29a5aa0be010000006b483045022100b4f4b71c5c55a5859bd1ab9f83d06fb11ff20c21ffbc4ee13ee23d0d31f7e00d02205a2f78246cfa8d5107e5158ddd51f35532263a266dfc1165881e1363903eac19812102d263bf65cc2b6c58c72c5998047d07c8d16162ce0e4a5df404e157eeac96819bffffffff02008c8647000000001976a9148c7b1435f01f792680a8e8244dc6980a6861137b88ac1059df11000000001976a914ce2252959ab2d7e5f6aa4d0de1972d8e87dd817888ac00000000";
        HashMap<String,String> inputArgs = new HashMap<String,String>();
        inputArgs.put("rawtx", txSignedTxt);
        String result = sendTxTest.txSend(url,inputArgs);

        System.out.println(result);
    }


    public String txSend(String URL, HashMap<String,String> args) throws Exception {

        HttpClient httpclient = new DefaultHttpClient();


        HttpPost httppost = new HttpPost(new URI(URL));
        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httppost.setHeader("charset", "utf-8");
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");

        if(args != null)    {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            for(String key : args.keySet())   {
                urlParameters.add(new BasicNameValuePair(key, args.get(key)));
            }
            httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
        }

        HttpResponse response = httpclient.execute(httppost);

        StringBuffer sb = new StringBuffer();
        sb.append(response.getStatusLine()).append("\n\n");

        InputStream is = response.getEntity().getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        String result = sb.toString();
//        Log.d("WebUtil", "POST result via Tor:" + result);
        int idx = result.indexOf("{");
        if(idx != -1)    {
            return result.substring(idx);
        }
        else    {
            return result;
        }

    }
}
