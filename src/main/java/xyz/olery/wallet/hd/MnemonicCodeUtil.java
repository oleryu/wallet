package xyz.olery.wallet.hd;


import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

import com.google.common.base.Preconditions;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;

/**
 * 生成随机助记词
 * oleryu@aliyun
 */
public class MnemonicCodeUtil {

    public static void main(String[] args) {
        System.out.println(getMnemonicCode());
    }
    //-----------------------------------------------------------------------------------
    public static String getMnemonicCode() {
        //spoon tackle camp vintage present soldier trap sample over team foil force
        String passphrase = "";
        SecureRandom secureRandom = new SecureRandom();
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed deterministicSeed = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        List<String> mnemonicCode = deterministicSeed.getMnemonicCode();

        return String.join(" ", mnemonicCode);
    }
    public static String getMnemonicCodeByDic() {
        SecureRandom secureRandom = new SecureRandom();

        try {
            MnemonicCode MnemonicCode
                    = new MnemonicCode(new FileInputStream("D://english.txt"),"ad90bf3beb7b0eb7e5acd74727dc0da96e0a280a258354e7293fb7e211ac03db");
            List mnemonicCode = MnemonicCode.toMnemonic(getEntropy(secureRandom, 128));

            return String.join(" ", mnemonicCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    //-----------------------------------------------------------------------------------
    private static byte[] getEntropy(SecureRandom random, int bits) {
        Preconditions.checkArgument(bits <= 512, "requested entropy size too large");
        byte[] seed = new byte[bits / 8];
        random.nextBytes(seed);
        return seed;
    }
}