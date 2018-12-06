package xyz.olery.wallet.eth.tx;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import xyz.olery.wallet.eth.Web3Util;
import xyz.olery.wallet.eth.account.MyWalletAccount;

import java.math.BigInteger;

/**
 * ...
 * @oleryu.xyz
 */
public class TransSignByWalletFile {
    //设置需要的矿工费
    static BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);
    static BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

    public static void main(String[] args) throws Exception {
        transByWalletFileTest();

    }

    public static void transByWalletFileTest() throws Exception{
        //
        String walleFilePath="D:\\home\\wallet\\coinbase\\UTC--2018-10-26T10-00-17.595874902Z--875de45b9b74a9de75680327aa89f8b266d9c74a";
//        String url = "http://192.168.124.6:8545";
        String addressTo = "0x9dd0dfec61e84013ba89add69d1283759949044b";
        String password="12345678";

        signTx(password,walleFilePath,addressTo);
    }


    public static void signTx(String password, String walleFilePath,String addressTo) throws Exception {
        Web3j web3j = Web3Util.web3j;


        MyWalletAccount myWalletAccount = new MyWalletAccount(walleFilePath,password);
        //被转人账户地址
        //String toAddress = "0xb1aba410f569288102dd7a1f1527c40eadef7fb9";
        //转账人账户地址
        String ownAddress = myWalletAccount.ethAddress();

        //getNonce（这里的Nonce我也不是很明白，大概是交易的笔数吧）
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                ownAddress, DefaultBlockParameterName.LATEST).sendAsync().get();

        //----------------------------------------
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        //创建交易，这里是转0.5个以太币
        BigInteger value = Convert.toWei("0.5", Convert.Unit.ETHER).toBigInteger();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, GAS_PRICE, GAS_LIMIT, addressTo, value);

        //签名Transaction，这里要对交易做签名
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, myWalletAccount.getCredentials());
        String hexValue = Numeric.toHexString(signedMessage);

        System.out.println("|签名交易|：" + hexValue);

    }
}
