package xyz.olery.wallet.eth.erc20;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import xyz.olery.wallet.eth.Web3Util;
import xyz.olery.wallet.eth.account.HDWalletAccount;
import xyz.olery.wallet.eth.account.WalletInfo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * oleryu
 *
 * */
public class ContractTokenTxSign {

    static BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);
    static BigInteger GAS_LIMIT = BigInteger.valueOf(300_300_000);

    static byte chainId = (byte)15;

    public static void main(String[] args) throws Exception {
        Web3j web3j = Web3Util.web3j;

        String contractAddress = "0x9ffC9F2913857A8C3442965a4c6A48c8eb47B53E";

        String seedCode = "legend finger master ordinary soccer stomach predict alone drift foot piano address";
        String ethKeypath = "M/44H/60H/0H/0/0";
        String passphrase = "";

        HDWalletAccount walletAccount = new HDWalletAccount(seedCode,ethKeypath,passphrase);

        //地址形如："0x0E0595e85300Df7c264ba7E361372440EEFf7D36";
        String address = walletAccount.ethAddress();
        System.out.println("|LOCAL_ETH_ADDRESS|: " + address);

        //地址形如："0x4fee2588626adfc7839de2513077242db3b8a818";
        String targetAddress =  "0x346457a7aA5F825C6d8C0BC9F090F00c10769430";

        String privateKey = walletAccount.getPrivateKey().toString(16);
        System.out.println("|LOCAL_ADDRESS_PRIVATEKEY|: " + privateKey);

        long amount = 10;



        BigDecimal ethBalance = WalletInfo.getBalance(web3j, address);
        System.out.println("ETH_BALANCE: " + ethBalance);

        BigInteger myBalanceValue = ContractTokenBalance.getTokenBalance(web3j,address,contractAddress);
        System.out.println("MY_TOKEN_BALANCE_1: " + myBalanceValue);

        BigInteger targetBalanceValue = ContractTokenBalance.getTokenBalance(web3j,targetAddress,targetAddress);
        System.out.println("TARGET_TOKEN_BALANCE_2: " + targetBalanceValue);

        String hexvalue = signedTokenTransfer(web3j,
                address,
                privateKey,
                targetAddress,
                contractAddress,
                amount);

    }



    public static String signedTokenTransfer(Web3j web3j,
                                       String address,
                                       String privateKey,
                                       String targetAddress,
                                       String contractAddress,
                                       long amount) throws Exception {

        ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(privateKey, 16));
        Credentials credentials = Credentials.create(ecKeyPair);

        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                address, DefaultBlockParameterName.LATEST).sendAsync().get();

        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        //val 转账金额
        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(targetAddress), new Uint256(new BigInteger("10"))),
                Arrays.asList(new TypeReference<Type>() {
                }));

        String encodedFunction = FunctionEncoder.encode(function);

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce,
                Convert.toWei("18", Convert.Unit.GWEI).toBigInteger(),
                Convert.toWei("100000", Convert.Unit.WEI).toBigInteger(),
                contractAddress, encodedFunction);

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        return hexValue;

    }
}
