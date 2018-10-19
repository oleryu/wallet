package xyz.olery.wallet.eth.erc20;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import xyz.olery.wallet.eth.Web3Util;
import xyz.olery.wallet.eth.account.HDWalletAccount;
import xyz.olery.wallet.eth.account.WalletInfo;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * ...
 * @oleryu.xyz
 */
public class ContractTokenTransfer {

    static byte chainId = (byte)15;


    public static void main(String[] args) throws Exception {
        String contractAddress = "0x9ffC9F2913857A8C3442965a4c6A48c8eb47B53E";

        String seedCode = "legend finger master ordinary soccer stomach predict alone drift foot piano address";
        String ethKeypath = "M/44H/60H/0H/0/0";
        String passphrase = "";

        HDWalletAccount walletAccount = new HDWalletAccount(seedCode,ethKeypath,passphrase);

        //地址形如："0x0E0595e85300Df7c264ba7E361372440EEFf7D36";
        String address = walletAccount.ethAddress();
        System.out.println("|LOCAL_ETH_ADDRESS|: " + address);

        //地址形如："0x4fee2588626adfc7839de2513077242db3b8a818";
        String targetAddress =  "0xfabb82f3de8de110189f352e9a1c7fbd8b467312";

        String privateKey = walletAccount.getPrivateKey().toString(16);
        System.out.println("|LOCAL_ADDRESS_PRIVATEKEY|: " + address);

        long amount = 10;

        Web3j web3j = Web3Util.web3j;

        BigDecimal ethBalance = WalletInfo.getBalance(web3j, address);
        System.out.println("ETH_BALANCE: " + ethBalance);

        BigInteger myBalanceValue = ContractTokenBalance.getTokenBalance(web3j,address,contractAddress);
        System.out.println("MY_TOKEN_BALANCE_1: " + myBalanceValue);

        BigInteger targetBalanceValue = ContractTokenBalance.getTokenBalance(web3j,targetAddress,contractAddress);
        System.out.println("TARGET_TOKEN_BALANCE_2: " + targetBalanceValue);

        BigInteger gasPrice = Convert.toWei("18", Convert.Unit.GWEI).toBigInteger();
        BigInteger gasLimit = Convert.toWei("100000", Convert.Unit.WEI).toBigInteger();
        String hexvalue = ContractTokenTxSign.signedTokenTransfer(web3j,
                address,
                privateKey,
                targetAddress,
                contractAddress,
                BigInteger.valueOf(amount),gasPrice,gasLimit);

        String tx = ContractTokenTxSend.transferToken(web3j,hexvalue);
        System.out.println("交易流水号：" + tx);
    }














}
