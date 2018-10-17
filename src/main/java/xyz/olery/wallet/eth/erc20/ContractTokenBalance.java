package xyz.olery.wallet.eth.erc20;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
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
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * oleryu
 * 2018/10/17
 * 查询余额
 *
 * 详见：https://blog.csdn.net/fangdengfu123/article/details/82181091  转账失败
 *       https://www.jianshu.com/p/8ae984e6bafc                        转账成功
 * */
public class ContractTokenBalance {
    public static void main(String[] args) throws Exception {
        Web3j web3j = Web3j.build(new HttpService("http://192.168.10.168:8545"));

        String address = "0xd3d3142baa2a88dcbda1d8f93de8e0f70fef12a3";
        String contractAddress= "0x9ffC9F2913857A8C3442965a4c6A48c8eb47B53E";

        BigInteger myTokenBalance1 = getTokenBalance(web3j,address,contractAddress);
        System.out.println("MY_TOKEN_BALANCE: " + myTokenBalance1);

        BigInteger myTokenBalance2 = balanceOf(web3j,address,contractAddress);
        System.out.println("MY_TOKEN_BALANCE: " + myTokenBalance2);
    }

    public static BigInteger getTokenBalance(Web3j web3j,String fromAddress, String contractAddress) {

        String methodName = "balanceOf";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        Address address = new Address(fromAddress);
        inputParameters.add(address);

        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);

        EthCall ethCall;
        BigInteger balanceValue = BigInteger.ZERO;
        try {
            web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());

            if(results != null && results.size()>0) {
                balanceValue = (BigInteger) results.get(0).getValue();
            } else {
                balanceValue = BigInteger.valueOf(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return balanceValue;
    }

    public static  BigInteger balanceOf(Web3j web3j,String address, String contractAddress) throws Exception {

        String txdata = "0x70a08231000000000000000000000000" + address.substring(2);

        BigInteger balanceValue = BigInteger.ZERO;
        EthCall ethCall = web3j.ethCall(Transaction.createEthCallTransaction(
                address,
                contractAddress,
                txdata)
                , DefaultBlockParameterName.PENDING).send();

        //ethCall.getValue() -> 0x00000000000000000000000000000000000000000000000000000000000f7314 -> 1012500
        String result = ethCall.getValue();
        BigInteger value = BigInteger.valueOf(0);
        if(result.startsWith("0x") && result.length()>2) {
            value = new BigInteger(result.substring(2),16);
        }
        return value;
    }


}
