package xyz.olery.wallet.eth.util;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import xyz.olery.wallet.eth.account.HDWalletAccount;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class HDWalletUtil {
    //-------------------------------------------------------------------------------------------------------
    private static void loadWallet() throws Exception {
        String walleFilePath="D:\\home\\wallet\\coinbase\\UTC--2018-09-19T15-34-04.030882499Z--955b687cd8a71c2ae64690ce9799065c9042c2fd";
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
    //-------------------------------------------------------------------------------------------------------

    /***********查询指定地址的余额***********/
    /**
     *
     * @param address  0x346457a7aA5F825C6d8C0BC9F090F00c10769430
     * @return  balance of ether
     * @throws IOException
     */
    public static String getEthBlanceOf(Web3j web3j,String address) throws IOException {
        //等待查询余额的地址
        //String address = "0x346457a7aA5F825C6d8C0BC9F090F00c10769430";

        //第二个参数：区块的参数，建议选最新区块
        EthGetBalance balance = web3j.ethGetBalance(address, DefaultBlockParameter.valueOf("latest")).send();

        System.out.println("blanceWEI" + balance.getBalance().toString());
        //格式转化 wei-ether
        String blanceETH = Convert.fromWei(balance.getBalance().toString(),
                Convert.Unit.ETHER).toPlainString().concat(" ether");

        System.out.println("blanceETH:" + blanceETH);

        return blanceETH;
    }
    //-------------------------------------------------------------------------------------------------------
    /*******连接以太坊客户端**************/
    private static void conectETHclient(Web3j web3j) throws IOException {
        //连接方式1：使用infura 提供的客户端
        //连接方式2：使用本地客户端
        //web3j = Web3j.build(new HttpService("127.0.0.1:7545"));
        //测试是否连接成功
        String web3ClientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();

        System.out.println("web3ClientVersion:" + web3ClientVersion);
    }


    //-------------------------------------------------------------------------------------------------------

    /**
     *
     * @param web3j
     * @param wallet
     * @param addressTo  0x44C24b82f783a11510B0Af11AC09A221FF7eb1E9
     * @throws Exception
     */
    public static void transByHDWalletWithReceipt(
            Web3j web3j,HDWalletAccount wallet,String addressTo,long amount) throws Exception {

        Credentials credentials = wallet.getCredentials();

        transByCredentialsWithReceipt(web3j,credentials,addressTo, amount);
    }

    private static void transByCredentialsWithReceipt(Web3j web3j ,Credentials credentials,String addressTo,long amount) throws Exception {
        if (credentials == null) return;
        //开始发送0.01 =eth到指定地址

        TransactionReceipt send = Transfer.sendFunds(web3j,
                credentials, addressTo,
                BigDecimal.valueOf(amount), Convert.Unit.FINNEY).send();

        System.out.println("Transaction complete:");
        System.out.println("trans hash=" + send.getTransactionHash());
        System.out.println("from :" + send.getFrom());
        System.out.println("to:" + send.getTo());
        System.out.println("gas used=" + send.getGasUsed());
        System.out.println("status: " + send.getStatus());
    }
    //-------------------------------------------------------------------------------------------------------
}
