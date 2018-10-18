package xyz.olery.wallet.eth.listener;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;


/**
 * 观察 链上区块
 *
 * @ oleryu.xyz
 */
public class TransactionListener {

    public static void startup() throws Exception{
        Web3j web3j = Web3j.build(new HttpService("http://192.168.10.102:8545"));
        //获得到transactionHash后就可以到以太坊的网站上查询这笔交易的状态了
        web3j.transactionObservable().subscribe(tx -> {
            System.out.println(tx.getHash());
        });

    }
    public static void main(String[] args) throws Exception {
        startup();
    }


}
