package xyz.olery.wallet.eth.util;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import xyz.olery.wallet.eth.account.HDWalletAccount;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
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
        //val 转账金额
        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(toAddr), new Uint256(amount)),
                Arrays.asList(new TypeReference<Type>() {
                }));

        String funcABI = FunctionEncoder.encode(function);

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


}
