package xyz.olery.wallet.btc.rpc.test2;

/**
 *
 * 参考：https://blog.csdn.net/wypeng2010/article/details/81357265
 */

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bouncycastle.util.encoders.Hex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.bitcoinj.core.UTXO;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.olery.wallet.btc.rpc.test.SendTxTest;
import xyz.olery.wallet.btc.rpc.test.UnSpentBTC;

public class SignTxTest {

    public  static String getResponse(String url) throws Exception {

        HttpClient httpclient = new DefaultHttpClient();


        HttpGet httpget = new HttpGet(new URI(url));

        HttpResponse response = httpclient.execute(httpget);

        StringBuffer sb = new StringBuffer();
        sb.append(response.getStatusLine()).append("\n\n");

        InputStream is = response.getEntity().getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String result = sb.toString();

        //
        int iStartWith = result.indexOf("[");
        result = "[" + result.substring(iStartWith+1);
        int iEndWith = result.lastIndexOf("]");
        result = result.substring(0,iEndWith+1);

        return result;

    }
    public static void main(String[] args) throws Exception {
        String from = "mrxTLJiUjXQFYUrp6EJdnBVXuRWnZU7Lw2";

        //datadir=2
        String to = "mxJQKzD1NUWwsgs2d6wYsQZxmiASSAtiZo";

        //String url = "http://192.168.10.104:3001/insight-api/addr/"+from+"/utxo";
        String url = "http://192.168.124.7:8085/wat-tx-api/btc/"+from+"/utxo";

        String unspents = getResponse(url);

        String seedCode = "verb evil oven oyster strong tube farm donkey twice family winner trouble";
        String signTxText = signTx(seedCode,unspents,from,to);

        System.out.println(signTxText);
        //SendTxTest.sendtx(signTxText);
    }

    public static String signTx(String seedCode,String unspents,String titleAddress,String addressTo) throws Exception {
        JSONArray utxoArray = new JSONArray(unspents.trim());
        List unSpentBTCList = new ArrayList();
        for (int i = 0; i < utxoArray.length(); i++) {
            JSONObject utxoObject = utxoArray.getJSONObject(i);
            int confirmations = utxoObject.getInt("confirmations");
            if (confirmations == 0) {
                continue;
            }
            String    address = utxoObject.getString("address");
            String   txid = utxoObject.getString("txid");
            long   vout = utxoObject.getLong("vout");
            String    scriptPubKey = utxoObject.getString("scriptPubKey");
            long     amount= utxoObject.getLong("amount");
            long    satoshis = utxoObject.getLong("satoshis");
            int    height = utxoObject.getInt("height");
            unSpentBTCList.add(new UnSpentBTC(txid, vout, satoshis, height, scriptPubKey));
        }
        String from = titleAddress;
        String to = addressTo;
        NetworkParameters params = RegTestParams.get();
        String privateKey =eckey(params,seedCode);
        long value = 100000l;
        long fee =150000l;
        String signDateString = signTxData(params,unSpentBTCList,from,to,privateKey,value,fee);
        //Log.i("dingding","获取 "+signDateString);
        return signDateString;
    }
    public static String getBalanceKey(String privateKey,String unspents,String titleAddress,String addressTo) throws Exception {
        JSONArray utxoArray = new JSONArray(unspents.trim());
        List unSpentBTCList = new ArrayList();
        for (int i = 0; i < utxoArray.length(); i++) {
            JSONObject utxoObject = utxoArray.getJSONObject(i);
            int confirmations = utxoObject.getInt("confirmations");
            if (confirmations == 0) {
                continue;
            }
            String    address = utxoObject.getString("address");
            String   txid = utxoObject.getString("txid");
            long   vout = utxoObject.getLong("vout");
            String    scriptPubKey = utxoObject.getString("scriptPubKey");
            long     amount= utxoObject.getLong("amount");
            long    satoshis = utxoObject.getLong("satoshis");
            int    height = utxoObject.getInt("height");

            unSpentBTCList.add(new UnSpentBTC(txid, vout, satoshis, height, scriptPubKey));
        }
        String from = titleAddress;
        String to = addressTo;
        NetworkParameters params = RegTestParams.get();
        //String privateKey =eckey(params,seedCode);
        long value = 100000000l;
        long fee =150000l;
        String signDateString = signTxData(params,unSpentBTCList,from,to,privateKey,value,fee);
      //  Log.i("dingding","获取 "+signDateString);
        return signDateString;
    }
    public static String eckey(NetworkParameters params,String seedCode) throws Exception {

        String passphrase = "";
        String strKeypath = "M/44H/0H/0H/0/0";
        DeterministicKey key = getDeterministicKey(seedCode,passphrase,strKeypath);
        BigInteger privKey = key.getPrivKey();

        ECKey ecKey = ECKey.fromPrivate(privKey);
        String publicKey = ecKey.getPublicKeyAsHex();
        String privateKey = ecKey.getPrivateKeyEncoded(params).toString();
        return  privateKey;
    }


    public static DeterministicKey getDeterministicKey(String seedCode,String passphrase,String strKeypath) throws Exception{
        // BitcoinJ
        DeterministicSeed seed = new DeterministicSeed(seedCode, null, "", 1409478661L);
        DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
        //"M/44H/0H/0H/0/0"
        //"M/44H/60H/0H/0/0"
        List<ChildNumber> keyPath = HDUtils.parsePath(strKeypath);
        DeterministicKey key = chain.getKeyByPath(keyPath, true);
        return key;
    }

    public static String signTxData(
            NetworkParameters params,
            List<UnSpentBTC> unSpentBTCList,
            String from,
            String to,
            String privateKey,
            long value, long fee) throws Exception {

        Transaction transaction = new Transaction(params);
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, privateKey);

        ECKey ecKey = dumpedPrivateKey.getKey();

        long totalMoney = 0;
        List<UTXO> utxos = new ArrayList<>();
        //遍历未花费列表，组装合适的item
        for (UnSpentBTC us : unSpentBTCList) {
            if (totalMoney >= (value + fee))
                break;
            UTXO utxo = new UTXO(Sha256Hash.wrap(us.getTxid()), us.getVout(), Coin.valueOf(us.getSatoshis()),
                    us.getHeight(), false, new Script(Hex.decode(us.getScriptPubKey())));
            utxos.add(utxo);
            totalMoney += us.getSatoshis();
        }

        transaction.addOutput(Coin.valueOf(value), Address.fromBase58(params, to));
        // transaction.

        //消费列表总金额 - 已经转账的金额 - 手续费 就等于需要返回给自己的金额了
        long balance = totalMoney - value - fee;
        //输出-转给自己
        if (balance > 0) {
            transaction.addOutput(Coin.valueOf(balance), Address.fromBase58(params, from));
        }

        //输入未消费列表项
        for (UTXO utxo : utxos) {
            TransactionOutPoint outPoint = new TransactionOutPoint(params, utxo.getIndex(), utxo.getHash());
            transaction.addSignedInput(outPoint, utxo.getScript(), ecKey, Transaction.SigHash.ALL, true);
        }
        return Hex.toHexString(transaction.bitcoinSerialize());
    }

}
