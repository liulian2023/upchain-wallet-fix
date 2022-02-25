package pro.upchain.wallet.utils;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pro.upchain.wallet.C;

public class Web3jUtils {
    public static String emptyAddress = "0x0000000000000000000000000000000000000000";
    public   static BigDecimal getBalance(String walletAddress, String contractAddress, Web3j web3j) throws Exception {
        org.web3j.abi.datatypes.Function function = balanceOf(walletAddress);
        String responseValue = callSmartContractFunction(function, contractAddress, walletAddress,web3j);

        List<Type> response = FunctionReturnDecoder.decode(
                responseValue, function.getOutputParameters());
        if (response.size() == 1) {
            return new BigDecimal(((Uint256) response.get(0)).getValue());
        } else {
            return null;
        }
    }
    public static String getBalanceString(int decimals,BigDecimal balance,boolean sendingTokens,String contractAddress,Web3j web3j) {
        if(decimals == 0){
            if(sendingTokens){
                decimals = Web3jUtils.erc20Decimals(web3j,contractAddress,null).intValue();
            }else {
                decimals = C.ETHER_DECIMALS;
            }
        }
        String textBalance="";
        BigDecimal decimalDivisor = new BigDecimal(Math.pow(10, decimals));
        BigDecimal ethBalance = balance.divide(decimalDivisor);
        if (decimals > 4) {
            textBalance=   ethBalance.setScale(4, RoundingMode.CEILING).toPlainString();
        } else {
            textBalance = ethBalance.setScale(decimals, RoundingMode.CEILING).toPlainString();
        }
        return textBalance;
    }
    public static BigDecimal getETHBalance(Web3j web3j,String walletAddr) throws IOException {
       return new BigDecimal(web3j
                .ethGetBalance(walletAddr, DefaultBlockParameterName.LATEST)
                .send()
                .getBalance());
    }
    public static org.web3j.abi.datatypes.Function balanceOf(String owner) {
        return new org.web3j.abi.datatypes.Function(
                "balanceOf",
                Collections.singletonList(new org.web3j.abi.datatypes.Address(owner)),
                Collections.singletonList(new TypeReference<Uint256>() {}));
    }

    public static String callSmartContractFunction(
            org.web3j.abi.datatypes.Function function, String contractAddress, String walletAddress,Web3j web3j) throws Exception {
        String encodedFunction = FunctionEncoder.encode(function);

        org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(walletAddress, contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST)
                .sendAsync().get();

        return response.getValue();
    }

    /**
     * 查询erc20的精度
     *
     * @param
     * @param web3j
     * @param contract 合约地址
     * @return
     */
    public static BigInteger erc20Decimals(Web3j web3j, String contract,Web3jSuccess web3jSuccess)  {
        //ERC20代币合约方法
        Function function = new Function(
                "decimals",
                Arrays.asList(),
                Collections.singletonList(new TypeReference<Type>() {
                }));
        //创建RawTransaction交易对象
        String encodedFunction = FunctionEncoder.encode(function);
        String value = null;
        try {
            value = web3j.ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(null,
                    contract, encodedFunction), DefaultBlockParameterName.LATEST).send().getValue();
        } catch (IOException e) {
            LogUtils.e("erc20Decimals:{}",e);
        }
        System.out.println("erc20Decimals="+value);
        BigInteger bigInteger = new BigInteger(value.substring(2), 16);
        System.out.println("erc20Decimals="+bigInteger);
        if(web3jSuccess!=null){
            web3jSuccess.success(bigInteger);
        }
        SharePreferencesUtil.putInt(contract,bigInteger.intValue());
        return bigInteger;
    }

    /**
     * 查询代币符号
     *
     * @param web3j
     * @param contractAddress
     * @return
     */
    public static String getTokenSymbol(Web3j web3j, String contractAddress) {
        String methodName = "symbol";
        String symbol = null;
        String fromAddr = emptyAddress;
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            if(results.size()!=0){
                symbol = results.get(0).getValue().toString();
            }else {
                symbol = "";
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return symbol;
    }

    public interface  Web3jSuccess{
        void success(BigInteger decimals);
    }
}
