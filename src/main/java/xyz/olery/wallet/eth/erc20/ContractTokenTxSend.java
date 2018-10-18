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
 * ...
 * @oleryu.xyz
 */
public class ContractTokenTxSend {

   public static void main(String[] args) throws Exception {
       Web3j web3j = Web3j.build(new HttpService("http://192.168.10.168:8545"));

       String signData ="0xf8aa80850430e23400830186a0949ffc9f2913857a8c3442965a4c6a48c8eb47b53e80b844a9059cbb000000000000000000000000fabb82f3de8de110189f352e9a1c7fbd8b467312000000000000000000000000000000000000000000000000000000000000000a1ba03818b3f82bd228584308ae5e6b1f9bc60440b458ccd611503e068206445ae025a07236f71eaab90b576b95a4533400c18e2f337df8fe5fb1e6313fc78ab6f54ddd";
       String tx = transferToken(web3j,signData);
       System.out.println("交易流水号：" + tx);

   }

    public static String transferToken(Web3j web3j,String signData) {

        try {
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signData).sendAsync().get();
            String transactionHash = ethSendTransaction.getTransactionHash();
            return transactionHash;
        } catch(Exception e) {

        }
        return "";
    }
}
