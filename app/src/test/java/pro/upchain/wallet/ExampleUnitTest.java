package pro.upchain.wallet;

import org.junit.Test;

import static org.junit.Assert.*;

import pro.upchain.wallet.utils.AESParamUtil;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
     String content="9CAEE8D2FF39838FAF8C6BE3FBA03E7EDB711CAFD7EBC91C044DCD4184572E4E7E56D07BF4D81A82C17F50F275F5215FE943AFB8E622F489BA734340E665D6BB32377AFB6273EDC8732AC8739E8FA97B&address=0xF60B00E2C185F8B26771eF823c116334f3b580B5&blockchainType=2";
        String decrypt = AESParamUtil.decrypt(content);
    }
}