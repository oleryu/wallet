package xyz.olery.wallet.eth.tx;

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
import xyz.olery.wallet.eth.Web3Util;
import xyz.olery.wallet.eth.account.HDWalletAccount;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * ...
 * @oleryu.xyz
 */
public class TransSignByHDWallet {
    //设置需要的矿工

    public static void main(String[] args) throws Exception {
        //创建交易，这里是转0.5个以太币
        //amount = 0.5  0.5 以太币
        BigInteger value = Convert.toWei("0.5", Convert.Unit.ETHER).toBigInteger();
        transByHDWalletTest(value);

    }

    public static void transByHDWalletTest(BigInteger value) throws Exception{
        String seedCode = "legend finger master ordinary soccer stomach predict alone drift foot piano address";
        String ethKeypath = "M/44H/60H/0H/0/0";
        String passphrase = "";

        HDWalletAccount walletAccount = new HDWalletAccount(seedCode,ethKeypath,passphrase);
        Web3j web3j = Web3Util.web3j;
        String toAddress = "0xb1aba410f569288102dd7a1f1527c40eadef7fb9";

        BigInteger gasPrice = Convert.toWei("18", Convert.Unit.GWEI).toBigInteger();
        BigInteger gasLimit = Convert.toWei("100000", Convert.Unit.WEI).toBigInteger();

        String transactionHash = signTx(web3j,walletAccount,toAddress,value,gasPrice,gasLimit);

    }


    public static String signTx(Web3j web3j,
                                HDWalletAccount walletAccount,
                                String toAddress,
                                BigInteger value,BigInteger gasPrice,BigInteger gasLimit) throws Exception {

        //被转人账户地址
        //String toAddress = "0xb1aba410f569288102dd7a1f1527c40eadef7fb9";
        //转账人私钥
        Credentials credentials = walletAccount.getCredentials();
        //转账人账户地址
        String ownAddress = credentials.getAddress();

        //getNonce（这里的Nonce我也不是很明白，大概是交易的笔数吧）
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                ownAddress, DefaultBlockParameterName.LATEST).sendAsync().get();

        //----------------------------------------
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();



        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, gasPrice, gasLimit, toAddress, value);


        //签名Transaction，这里要对交易做签名
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        System.out.println("|签名交易|：" + hexValue);
        //-------------------------------------------

        return hexValue;

    }
}
