package xyz.olery.wallet.eth.account;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.math.BigInteger;
import java.util.List;

/**
 *  oleryu 2018/10/17
 */
public class MyWalletAccount {

    private String walleFilePath;
    private String passphrase;
    private Credentials credentials;


    public Credentials getCredentials() {
        return credentials;
    }

    public MyWalletAccount(String walleFilePath, String passphrase) {
        this.walleFilePath = walleFilePath;
        this.passphrase = passphrase;

        //转账人私钥
        try {
            credentials = WalletUtils.loadCredentials(passphrase, walleFilePath);
        } catch(Exception e) {

        }

    }

    public BigInteger getPrivateKey() throws Exception {

        BigInteger privateKey = credentials.getEcKeyPair().getPrivateKey();

        return privateKey;
    }

    public String ethAddress() throws Exception {

        return credentials.getAddress();

    }

}
