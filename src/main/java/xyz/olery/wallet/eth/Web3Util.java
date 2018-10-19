package xyz.olery.wallet.eth;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import xyz.olery.wallet.eth.account.HDWalletAccount;
import xyz.olery.wallet.eth.account.WalletInfo;
import xyz.olery.wallet.eth.erc20.ContractTokenBalance;
import xyz.olery.wallet.eth.erc20.ContractTokenTxSend;
import xyz.olery.wallet.eth.erc20.ContractTokenTxSign;
import xyz.olery.wallet.eth.tx.TransSignByHDWallet;
import xyz.olery.wallet.eth.tx.TransactionSender;
import xyz.olery.wallet.eth.util.FileWalletUtil;
import xyz.olery.wallet.eth.util.HDWalletUtil;
import xyz.olery.wallet.eth.util.TransactionUtil;
import xyz.olery.wallet.hd.MnemonicCodeUtil;
import xyz.olery.wallet.hd.MnemonicToKey;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class Web3Util {

    //调用的是kovan测试环境，这里使用的是infura这个客户端
    //Web3j web3j = Web3j.build(new HttpService("https://kovan.infura.io/<your-token>"));
    public static Web3j web3j = Web3j.build(new HttpService("http://192.168.10.168:8545"));

//    //18_000_000_000L
    public static BigInteger GAS_PRICE = BigInteger.valueOf(3_000_000_000L);//可取到
    public static BigInteger GAS_LIMIT = BigInteger.valueOf(300_300_000);//实际 21000

    //TestNet3Params
    //MainNetParams
    //RegTestParams
    public static NetworkParameters params = RegTestParams.get();


    public static void generateNewWalletFile() throws Exception {
        String pathname = "D://home/wallet";
        String password = "123456";
        FileWalletUtil.generateNewWalletFile(password,pathname);
    }

    public static void getWalletBlanceOf(HDWalletAccount walletAccount) throws Exception {
        //
        String address = walletAccount.ethAddress();

        HDWalletUtil.getEthBlanceOf(web3j,address);
    }

    public static void getEthBlanceOf(String address) throws Exception {

        HDWalletUtil.getEthBlanceOf(web3j,address);
    }

    public static void transByHDWalletWithReceipt(HDWalletAccount wallet,String addressTo,long amount) throws Exception {


        //----------------------------------------------------------------
        HDWalletUtil.transByHDWalletWithReceipt(web3j,wallet,addressTo,amount);
    }

    public static void getTokenBalance(Web3j web3j,String address,String contractAddress) throws Exception {

        BigInteger myTokenBalance1 = ContractTokenBalance.getTokenBalance(web3j,address,contractAddress);
        System.out.println("MY_TOKEN_BALANCE: " + myTokenBalance1);

        BigInteger myTokenBalance2 = ContractTokenBalance.balanceOf(web3j,address,contractAddress);
        System.out.println("MY_TOKEN_BALANCE: " + myTokenBalance2);
    }

    public static void hdWalletAddrerss() throws Exception {
        String seedCode = "dilemma aspect clog craft mercy record flavor child confirm arena hint catalog";
        String btcKeyath = "M/44H/0H/0H/0/0";
        String ethKeyath = "M/44H/60H/0H/0/0";

        String bip44Address = MnemonicToKey.btc44Address(seedCode,"",params,btcKeyath);
        String bip49Address = MnemonicToKey.btc49Address(seedCode,"",params,btcKeyath);

        System.out.println("BIP44 Address：" +bip44Address);
        System.out.println("BIP49 Address：" +bip49Address);

        String ethAddrss = MnemonicToKey.ethAddress(seedCode,"",ethKeyath);
        System.out.println("ETH Address: " + ethAddrss);
    }

    public static String signedTokenTransfer(Web3j web3j
            ,HDWalletAccount walletAccount,String contractAddress,String addressTo,BigInteger value) throws Exception {

        //地址形如："0x0E0595e85300Df7c264ba7E361372440EEFf7D36";
        String address = walletAccount.ethAddress();
        System.out.println("|LOCAL_ETH_ADDRESS|: " + address);
        System.out.println("|TARGET_ADDRESS|: " + addressTo);
        System.out.println("|CONTRACT_ADDRESS|: " + contractAddress);

        //地址形如："0x4fee2588626adfc7839de2513077242db3b8a818";

        String privateKey = walletAccount.getPrivateKey().toString(16);
        System.out.println("|LOCAL_ADDRESS_PRIVATEKEY|: " + privateKey);



        BigDecimal ethBalance = WalletInfo.getBalance(web3j, address);
        System.out.println("ETH_BALANCE: " + ethBalance);

        BigInteger myBalanceValue = ContractTokenBalance.getTokenBalance(web3j,address,contractAddress);
        System.out.println("MY_TOKEN_BALANCE_1: " + myBalanceValue);

        BigInteger targetBalanceValue = ContractTokenBalance.getTokenBalance(web3j,addressTo,contractAddress);
        System.out.println("TARGET_TOKEN_BALANCE_2: " + targetBalanceValue);

//        BigInteger gasPrice = Convert.toWei("18", Convert.Unit.GWEI).toBigInteger();
//        BigInteger gasLimit = Convert.toWei("100000", Convert.Unit.WEI).toBigInteger();
        BigInteger gasPrice = GAS_PRICE;
        BigInteger gasLimit = GAS_LIMIT;
        String hexvalue = ContractTokenTxSign.signedTokenTransfer(web3j,
                address,
                privateKey,
                addressTo,
                contractAddress,
                value,gasPrice,gasLimit);

        System.out.println(hexvalue);
        return hexvalue;

    }

    public static String transferToken(String signData) throws Exception {
        Web3j web3j = Web3Util.web3j;

        String tx = ContractTokenTxSend.transferToken(web3j,signData);
        System.out.println("交易流水号：" + tx);
        return tx;

    }


    public static String transSignedByHDWallet(HDWalletAccount walletAccount,String toAddress,BigInteger value) throws Exception{

//        BigInteger gasPrice = Convert.toWei("18", Convert.Unit.GWEI).toBigInteger();
//        BigInteger gasLimit = Convert.toWei("100000", Convert.Unit.WEI).toBigInteger();
        BigInteger gasPrice = GAS_PRICE;
        BigInteger gasLimit = GAS_LIMIT;
        String hexValue = TransSignByHDWallet.signTx(web3j,walletAccount,toAddress,value,gasPrice,gasLimit);
        return hexValue;

    }

    /**
     * Estimate GasLimit
     * @throws Exception
     */
    public static void testTokenTransactionGasLimit(
            Web3j web3j
            ,HDWalletAccount wallet
            ,String contractAddress
            ,String toAddr) throws Exception {

        String fromAddr = wallet.ethAddress();
        System.out.println(fromAddr);
        long amount = 10;


        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddr, DefaultBlockParameterName.LATEST).sendAsync().get();

        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        BigInteger gasPrice = BigInteger.valueOf(22_000_000_000L);


        BigInteger gasLimit = TransactionUtil.getTokenTransactionGasLimit(web3j,
                fromAddr,
                nonce,
                gasPrice,
                toAddr,
                contractAddress,
                amount);

        System.out.println(gasLimit);

    }

    public static BigInteger testTransactionGasLimit(Web3j web3j
            ,HDWalletAccount wallet
            ,String toAddr,BigInteger value) throws Exception{

        String fromAddr = wallet.ethAddress();
        System.out.println(fromAddr);
        long amount = 10;


        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddr, DefaultBlockParameterName.LATEST).sendAsync().get();

        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        BigInteger gasPrice = BigInteger.valueOf(22_000_000_000L);

        BigInteger gasLimit =  TransactionUtil.getTransactionGasLimit(web3j,
                fromAddr,
                nonce,
                gasPrice,
                toAddr,
                value);
        System.out.println(gasLimit);
        return gasLimit;
    }

    public static void main(String[] args) throws Exception {
        /**
         |seedCode|: obscure balcony extend need govern fog item monitor moment fire depth when
         |ethAddress|: 0xe6471ba0615fe9dcf2246e0cc6aa77378d2b6582
         *
         * eth 测试地址2 0xf52F3cA3E5FB9A7b95f5b93dd9812b335Bddf20A
         * 0x0bE7428574EF444ba3ce5f2EBa46D1bF58B1508F
         */
        //----------------------------------------------------------------
        //30000  OLE  100 ETH
        String seedCode = "weasel because party metal canal public vicious police edit able used horn";
        String ethKeypath = "M/44H/60H/0H/0/0";
        String passphrase = "";

        HDWalletAccount walletAccount = new HDWalletAccount(seedCode,ethKeypath,passphrase);

        String contractAddress = "0xBa7c2cd5332f6AB4e84a7220f9e1716d7EDEdd89";
        String addressTo = "0x7c7bB9e22BDDF4c2ee60e17caD1A66D7b3c0419D";
//        testTokenTransactionGasLimit(web3j,walletAccount,contractAddress,addressTo);
        testTransactionGasLimit(web3j
                ,walletAccount
                ,addressTo,BigInteger.valueOf(5));

//        generateNewWalletFile();

//        getWalletBlanceOf(walletAccount);
//----------------------------------------------------------------------------------------
        //ETH 转账

        //with receipt
//        transByHDWalletWithReceipt(walletAccount,addressTo,amount);
        //创建交易，这里是转0.5个以太币
        //amount = 0.5  0.5 以太币

        BigInteger value = Convert.toWei("5", Convert.Unit.ETHER).toBigInteger();
        String hexValue = transSignedByHDWallet(walletAccount,addressTo,value);
        String transactionHash = TransactionSender.sendto( web3j,hexValue);
        System.out.println(transactionHash);

//----------------------------------------------------------------------------------------
//        String address = "0xb0077fb3c1d4de09dcb79cadc9f7fd25422918d9";
//        getTokenBalance(web3j,address,contractAddress);
//----------------------------------------------------------------------------------------

//----------------------------------------------------------------------------------------
//        hdWalletAddrerss();
//----------------------------------------------------------------------------------------
//        代币转账
        //交易签名
//        String signData = signedTokenTransfer(web3j,
//                walletAccount,contractAddress,addressTo,BigInteger.valueOf(99));
        //交易转账
        //String signData ="0xf8aa80850430e23400830186a0940be7428574ef444ba3ce5f2eba46d1bf58b1508f80b844a9059cbb000000000000000000000000f52f3ca3e5fb9a7b95f5b93dd9812b335bddf20a000000000000000000000000000000000000000000000000000000000000000a1ca0c397250de09ac7fe521a766473cab8bd823e1808deb84f5c365e42db5aa7a5fba00b66567c1cdcd9e7c6bd1a67768c50d8817946db508dac4b27e9fd64bc85c12b";
//        transferToken(signData);
//----------------------------------------------------------------------------------------
        //取上一笔，交易 最小值
        //查询当前区块 GWEI
        //https://ethstats.net/
        BigInteger gasPrice = web3j.ethGasPrice().sendAsync().get().getGasPrice();
        System.out.println(gasPrice);
    }
}
