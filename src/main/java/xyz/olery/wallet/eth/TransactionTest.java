package xyz.olery.wallet.eth;

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

import java.math.BigInteger;


public class TransactionTest {
    public static void sendtoTest() throws Exception{
        //设置需要的矿工费
        BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);
        BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

        //调用的是kovan测试环境，这里使用的是infura这个客户端
        //Web3j web3j = Web3j.build(new HttpService("https://kovan.infura.io/<your-token>"));
        Web3j web3j = Web3j.build(new HttpService("http://192.168.10.161:8545"));
        //转账人账户地址
        String ownAddress = "0xD1c82c71cC567d63Fd53D5B91dcAC6156E5B96B3";
        //被转人账户地址
        String toAddress = "0xa13a849592c07581795fe019c717207157c9f77e";
        //转账人私钥
        Credentials credentials = Credentials.create("xxxxxxxxxxxxx");
        //        Credentials credentials = WalletUtils.loadCredentials(
        //                "123",
        //                "src/main/resources/UTC--2018-03-01T05-53-37.043Z--d1c82c71cc567d63fd53d5b91dcac6156e5b96b3");

        //getNonce（这里的Nonce我也不是很明白，大概是交易的笔数吧）
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                ownAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        //创建交易，这里是转0.5个以太币
        BigInteger value = Convert.toWei("0.5", Convert.Unit.ETHER).toBigInteger();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, GAS_PRICE, GAS_LIMIT, toAddress, value);

        //签名Transaction，这里要对交易做签名
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        //发送交易
        EthSendTransaction ethSendTransaction =
                web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = ethSendTransaction.getTransactionHash();

        //获得到transactionHash后就可以到以太坊的网站上查询这笔交易的状态了
        System.out.println(transactionHash);
    }
    public static void main(String[] args) throws Exception {
        transByWalletFileTest();
    }

    public static void transByWalletFileTest() throws Exception{
        //
        //UTC--2018-09-19T15-34-04.030882499Z--955b687cd8a71c2ae64690ce9799065c9042c2fd

        String walleFilePath="D:\\home\\wallet\\coinbase\\UTC--2018-09-19T15-34-04.030882499Z--955b687cd8a71c2ae64690ce9799065c9042c2fd";
        String url = "http://192.168.10.161:8545";
        String addressTo = "0xa13a849592c07581795fe019c717207157c9f77e";
        String password="123456";

        sendto(password,walleFilePath);

    }


    public static void sendto(String password, String walleFilePath) throws Exception {
        //设置需要的矿工费
        BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);
        BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

        //调用的是kovan测试环境，这里使用的是infura这个客户端
        //Web3j web3j = Web3j.build(new HttpService("https://kovan.infura.io/<your-token>"));
        Web3j web3j = Web3j.build(new HttpService("http://192.168.10.161:8545"));

        //被转人账户地址
        String toAddress = "0xa13a849592c07581795fe019c717207157c9f77e";
        //转账人私钥
        Credentials credentials = WalletUtils.loadCredentials(password, walleFilePath);
        //转账人账户地址
        String ownAddress = credentials.getAddress();


        //getNonce（这里的Nonce我也不是很明白，大概是交易的笔数吧）
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                ownAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        //创建交易，这里是转0.5个以太币
        BigInteger value = Convert.toWei("0.0001", Convert.Unit.ETHER).toBigInteger();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, GAS_PRICE, GAS_LIMIT, toAddress, value);

        //签名Transaction，这里要对交易做签名
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        //发送交易
        EthSendTransaction ethSendTransaction =
                web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        String transactionHash = ethSendTransaction.getTransactionHash();

        //获得到transactionHash后就可以到以太坊的网站上查询这笔交易的状态了
        System.out.println(transactionHash);
    }
}
