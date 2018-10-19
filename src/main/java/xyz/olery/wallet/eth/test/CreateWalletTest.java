package xyz.olery.wallet.eth.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.web3j.crypto.*;
import xyz.olery.wallet.eth.account.HDWalletAccount;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class CreateWalletTest {
    public static void createByKey() throws Exception {
        String seedCode = "weasel because party metal canal public vicious police edit able used horn";
        String ethKeypath = "M/44H/60H/0H/0/0";
        String passphrase = "";

        HDWalletAccount walletAccount = new HDWalletAccount(seedCode,ethKeypath,passphrase);
        System.out.println(walletAccount.getPrivateKey().toString(16));
        System.out.println(walletAccount.ethAddress());

        BigInteger privKey = new BigInteger("a836a549dd391e1ebfe91f1c61be24211b052cb033dbf9e0eeee70deaac685bd",16);
        // Web3j
        Credentials credentials = Credentials.create(privKey.toString(16));

        String address = credentials.getAddress();
        System.out.println(address);
    }

    public static void main(String[] args) throws Exception {
        String password = "123456";
        String content = "{\"address\":\"5087743894ef4dbbc5fb2807ad27b79453e0d5ea\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"b5c72243e5270907347ab727b6cdea9ec5a71a88835977590bbc7aa7d13b21e6\",\"cipherparams\":{\"iv\":\"98d7ee30fdfe2f429fb9e10dee8a14db\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"765fff0f25241d020119db69db0cb03e5c831e99858e49a99b75bbdf1051ef2f\"},\"mac\":\"648fa2753af555b278f7f204527c94ff2fcf3bbf823094435c97072209d0bfa6\"},\"id\":\"93043d7e-3aa8-4e9d-8dfc-cef66e7c3c14\",\"version\":3}";
        Credentials credentials = loadCredentials(password, content);

        String address = credentials.getAddress();
        String privKey = credentials.getEcKeyPair().getPrivateKey().toString(16);

        System.out.println(address);
        System.out.println(privKey);
    }

    public static Credentials loadCredentials(String password, String content) throws IOException, CipherException {
        ObjectMapper objectMapper = new ObjectMapper();
        WalletFile walletFile = (WalletFile)objectMapper.readValue(content, WalletFile.class);
        return Credentials.create(Wallet.decrypt(password, walletFile));
    }
}
