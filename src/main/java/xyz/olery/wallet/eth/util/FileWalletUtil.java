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

/**
 * ...
 * @oleryu.xyz
 */
public class FileWalletUtil {
    public static void generateNewWalletFile(String password,String pathname) throws Exception {
        //File file = new File("D://home/wallet");
        File file = new File(pathname);
        //方法返回创建的钱包文件名
        WalletUtils.generateNewWalletFile(password, file, true);
    }

    //-------------------------------------------------------------------------------------------------------
    public static void transByWalletFileTest() {
        String walleFilePath="D:\\oleryu\\UTC--2018-11-30T14-35-32.998264601Z--41916899b1a4ab15e7359029872d63d86ad3d638";
        String url = "http://192.168.10.172:8545";
        String addressTo = "0x1976fdebfe3f4d971be8f8239eb8cb0a40d534c7";
        String password="12345678";
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
        BigDecimal v = BigDecimal.valueOf(50000);
        TransactionReceipt send = Transfer.sendFunds(web3j, credentials, addressTo, v , Convert.Unit.FINNEY).send();

        System.out.println("Transaction complete:");
        System.out.println("trans hash=" + send.getTransactionHash());
        System.out.println("from :" + send.getFrom());
        System.out.println("to:" + send.getTo());
        System.out.println("gas used=" + send.getGasUsed());
        System.out.println("status: " + send.getStatus());
    }

    public static void main(String[] args) {

        transByWalletFileTest();
    }
}
