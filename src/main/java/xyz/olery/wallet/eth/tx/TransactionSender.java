package xyz.olery.wallet.eth.tx;

import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import xyz.olery.wallet.eth.Web3Util;

import java.math.BigInteger;

import static xyz.olery.wallet.eth.Web3Util.web3j;

/**
 * ...
 * @oleryu.xyz
 */
public class TransactionSender {

    public static void main(String[] args) throws Exception {
        String hexValue = "0xf86d0585051f4d5c0083419ce0949dd0dfec61e84013ba89add69d1283759949044b8806f05b59d3b20000801ba0f5708a765a0af2d26f532ec69ee9f323fcd449b19625c859033f0246af3e703ca032354f6aab99699aa08efb670ab6ae7927d4c387d674d50c334d920e07bd1d9a";
        String transactionHash = sendto(Web3Util.web3j,hexValue);
        System.out.println(transactionHash);
    }

    public static String sendto( Web3j web3j,String hexValue) throws Exception {
        //发送交易
        EthSendTransaction ethSendTransaction =
                web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = ethSendTransaction.getTransactionHash();

        //获得到transactionHash后就可以到以太坊的网站上查询这笔交易的状态了

        return transactionHash;
    }
}
