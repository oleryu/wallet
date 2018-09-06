package com.hengpu.wallet;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Web3jTest {
    /***********查询指定地址的余额***********/
    private static void getBlanceOf() throws IOException {
        Web3j web3j = Web3j.build(
                new HttpService("http://192.168.227.138:8545"));// TODO: 2018/4/10 token更改为自己的
        if (web3j == null) return;
        //0x4e4cfc552ac14c22d770e4a39b6bcb58b12b4fff
        String address = "0x4e4cfc552ac14c22d770e4a39b6bcb58b12b4fff";//等待查询余额的地址
        //第二个参数：区块的参数，建议选最新区块
        EthGetBalance balance = web3j.ethGetBalance(address, DefaultBlockParameter.valueOf("latest")).send();
        //格式转化 wei-ether
        String blanceETH = Convert.fromWei(balance.getBalance().toString(),
                Convert.Unit.ETHER).toPlainString().concat(" ether");

        System.out.println("blanceETH:" + blanceETH);
    }


    //加载钱包文件
    //加载钱包的过程需要提供钱包文件和密码
    /********加载钱包文件**********/
    private static void loadWallet() throws Exception {
        String walleFilePath="D:\\home\\wallet\\UTC--2018-08-27T15-11-29.897000000Z--93e4c38302fa88e037b0f5024ed81d8418cffd2a.json";
        String passWord="123456";
        Credentials credentials = WalletUtils.loadCredentials(passWord, walleFilePath);
        String address = credentials.getAddress();
        BigInteger publicKey = credentials.getEcKeyPair().getPublicKey();
        BigInteger privateKey = credentials.getEcKeyPair().getPrivateKey();
        /*通过工具类 WalletUtols的函数 loadCredentials()，
        会返回一个对象Credentials,这个对象即包含了钱包文件的所有信息，包括地址、秘钥对。*/
        System.out.println("address: " + address);
        System.out.println("publicKey: " + publicKey);
        System.out.println("privateKey: " + privateKey);
    }

    /*******连接以太坊客户端**************/
    private static void conectETHclient() throws IOException {
        //连接方式1：使用infura 提供的客户端

        Web3j web3j = Web3j.build(
                new HttpService("http://192.168.227.138:8545"));// TODO: 2018/4/10 token更改为自己的
        //连接方式2：使用本地客户端
        //web3j = Web3j.build(new HttpService("127.0.0.1:7545"));
        //测试是否连接成功
        String web3ClientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();

        System.out.println("web3ClientVersion:" + web3ClientVersion);

    }

    private static void transto() throws Exception {
        Web3j web3j = Web3j.build(
                new HttpService("http://192.168.227.138:8545"));

        if (web3j == null) return;

        String walleFilePath="D:\\home\\wallet\\from\\UTC--2018-08-27T17-36-40.873604982Z--4e4cfc552ac14c22d770e4a39b6bcb58b12b4fff";

        String passWord="123456";
        Credentials credentials = WalletUtils.loadCredentials(passWord, walleFilePath);

        if (credentials == null) return;
        //开始发送0.01 =eth到指定地址
        String address_to = "0xc69b1cf38392511f6b1716ded8eb36b57a2e4b5c";
        TransactionReceipt send = Transfer.sendFunds(web3j, credentials, address_to, BigDecimal.ONE, Convert.Unit.FINNEY).send();

        System.out.println("Transaction complete:");
        System.out.println("trans hash=" + send.getTransactionHash());
        System.out.println("from :" + send.getFrom());
        System.out.println("to:" + send.getTo());
        System.out.println("gas used=" + send.getGasUsed());
        System.out.println("status: " + send.getStatus());
    }

    public static void main(String[] args) throws Exception {
//        File file = new File("D://home/wallet");
////        //方法返回创建的钱包文件名
////        //WalletUtils.generateNewWalletFile("123456", file, true);
////
//        Credentials credentials
//                = WalletUtils.loadCredentials("123456","D:\\home\\wallet\\UTC--2018-08-27T15-11-29.897000000Z--93e4c38302fa88e037b0f5024ed81d8418cffd2a.json" );
//        String returnAddress = credentials.getAddress();
////        //0x93e4c38302fa88e037b0f5024ed81d8418cffd2a
////        //0x41F1dcbC0794BAD5e94c6881E7c04e4F98908a87
//        System.out.println(returnAddress);

        //loadWallet();

        //conectETHclient();

        //getBlanceOf();

//        getBlanceOf();
        //loadWallet();
        transto();




    }
}
