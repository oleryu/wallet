package xyz.olery.wallet.eth.util;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.math.BigDecimal;

public class FileWalletUtil {
    public static void generateNewWalletFile(String password,String pathname) throws Exception {
        //File file = new File("D://home/wallet");
        File file = new File(pathname);
        //方法返回创建的钱包文件名
        WalletUtils.generateNewWalletFile(password, file, true);
    }

    //-------------------------------------------------------------------------------------------------------
    public static void transByWalletFileTest() {
        String walleFilePath="D:\\home\\wallet\\coinbase\\UTC--2018-09-19T15-34-04.030882499Z--955b687cd8a71c2ae64690ce9799065c9042c2fd";
        String url = "http://192.168.10.161:8545";
        String addressTo = "0xa13a849592c07581795fe019c717207157c9f77e";
        String password="123456";
        try {
            transByWalletFile(url,walleFilePath,password,addressTo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void transByWalletFile(String url,String walleFilePath,String password,String addressTo) throws Exception {
        Web3j web3j = Web3j.build(new HttpService(url));

        if (web3j == null) return;


        Credentials credentials = WalletUtils.loadCredentials(password, walleFilePath);

        if (credentials == null) return;
        //开始发送0.01 =eth到指定地址

        TransactionReceipt send = Transfer.sendFunds(web3j, credentials, addressTo, BigDecimal.ONE, Convert.Unit.FINNEY).send();

        System.out.println("Transaction complete:");
        System.out.println("trans hash=" + send.getTransactionHash());
        System.out.println("from :" + send.getFrom());
        System.out.println("to:" + send.getTo());
        System.out.println("gas used=" + send.getGasUsed());
        System.out.println("status: " + send.getStatus());
    }
}
