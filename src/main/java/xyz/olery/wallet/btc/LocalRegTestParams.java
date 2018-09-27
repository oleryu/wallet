package xyz.olery.wallet.btc;

import com.google.common.base.Preconditions;
import java.math.BigInteger;
import org.bitcoinj.core.Block;
import org.bitcoinj.params.TestNet2Params;

public class LocalRegTestParams extends TestNet2Params {
    private static final BigInteger MAX_TARGET = new BigInteger("7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
    private static Block genesis;
    private static LocalRegTestParams instance;

    public LocalRegTestParams() {
        this.interval = 2147483647;
        this.maxTarget = MAX_TARGET;
        this.subsidyDecreaseBlockCount = 150;
        this.port = 19010;
        this.id = "org.bitcoin.regtest";
        this.majorityEnforceBlockUpgrade = 750;
        this.majorityRejectBlockOutdated = 950;
        this.majorityWindow = 1000;
    }

    public boolean allowEmptyPeerChain() {
        return true;
    }

    public Block getGenesisBlock() {
        Class var1 = org.bitcoinj.params.RegTestParams.class;
        synchronized(org.bitcoinj.params.RegTestParams.class) {
            if (genesis == null) {
                genesis = super.getGenesisBlock();
                genesis.setNonce(2L);
                genesis.setDifficultyTarget(545259519L);
                genesis.setTime(1296688602L);
                Preconditions.checkState(genesis.getHashAsString().toLowerCase().equals("0f9188f13cb7b2c71f2a335e3a4fc328bf5beb436012afca590b1a11466e2206"));
            }

            return genesis;
        }
    }

    public static synchronized LocalRegTestParams get() {
        if (instance == null) {
            instance = new LocalRegTestParams();
        }

        return instance;
    }

    public String getPaymentProtocolId() {
        return "regtest";
    }
}