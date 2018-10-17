package xyz.olery.wallet.eth.erc20;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import xyz.olery.wallet.eth.account.WalletAccount;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 *  oleryu 2018/10/17
 *
 *  详见：https://blog.csdn.net/fangdengfu123/article/details/82181091
 */
public class TransactionUtil {
    public static BigInteger getNonce(Web3j web3j, String addr) {
        try {
            EthGetTransactionCount getNonce = web3j.ethGetTransactionCount(addr, DefaultBlockParameterName.PENDING).send();

            if (getNonce == null){
                throw new RuntimeException("net error");
            }
            return getNonce.getTransactionCount();
        } catch (IOException e) {
            throw new RuntimeException("net error");
        }
    }

    public static BigInteger getGasLimitByTransaction(Web3j web3j, Transaction transaction) {
        try {
            EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(transaction).send();
            if (ethEstimateGas.hasError()){
                throw new RuntimeException(ethEstimateGas.getError().getMessage());
            }
            return ethEstimateGas.getAmountUsed();
        } catch (IOException e) {
            throw new RuntimeException("net error");
        }
    }

    public static BigInteger getTransactionGasLimit(Web3j web3j,
            String fromAddr,
            BigInteger nonce,
            BigInteger gasPrice,
            String toAddr,
            BigInteger value) {
        // 构造eth交易
        Transaction transaction = Transaction.createEtherTransaction(
                fromAddr,
                nonce,
                gasPrice,
                null,
                toAddr,
                value);
        return getGasLimitByTransaction(web3j,transaction);
    }

    public static BigInteger getTokenTransactionGasLimit(Web3j web3j,
                                                         String fromAddr,
                                                         BigInteger nonce,
                                                         BigInteger gasPrice,
                                                         String toAddr,
                                                         String contractAddr,
                                                         long amount) {
        // 构建方法调用信息
        String method = "transfer";

        // 构建输入参数
        List<Type> inputArgs = new ArrayList<>();
        inputArgs.add(new Address(toAddr));
        inputArgs.add(new Uint256(BigDecimal.valueOf(amount).multiply(BigDecimal.TEN.pow(18)).toBigInteger()));

        // 合约返回值容器
        List<TypeReference<?>> outputArgs = new ArrayList<>();

        String funcABI = FunctionEncoder.encode(new Function(method, inputArgs, outputArgs));
        //构造合约调用交易
        Transaction transaction = Transaction.createFunctionCallTransaction(
                fromAddr,
                nonce,
                gasPrice,
                null,
                contractAddr,
                funcABI);
        return getGasLimitByTransaction(web3j,transaction);
    }

    /**
     * Estimate GasLimit
     * @param args     -
     * @throws Exception
     */
    public static void testTokenTransactionGasLimit(String[] args) throws Exception {
        String seedCode = "legend finger master ordinary soccer stomach predict alone drift foot piano address";
        String ethKeypath = "M/44H/60H/0H/0/0";
        String passphrase = "";



        Web3j web3j = Web3j.build(new HttpService("http://192.168.10.168:8545"));
        String contractAddress = "0x9ffC9F2913857A8C3442965a4c6A48c8eb47B53E";
        String toAddr =  "0xfabb82f3de8de110189f352e9a1c7fbd8b467312";

        WalletAccount account = new WalletAccount(seedCode,ethKeypath,passphrase);


        String fromAddr = account.ethAddress(seedCode,passphrase,ethKeypath);
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

    public static void main(String[] args) throws Exception {

        testTokenTransactionGasLimit(args);



    }

}
