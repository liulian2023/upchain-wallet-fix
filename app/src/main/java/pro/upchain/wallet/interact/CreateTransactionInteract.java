package pro.upchain.wallet.interact;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.MessagePair;
import pro.upchain.wallet.entity.SignaturePair;
import pro.upchain.wallet.entity.TransactionData;
import pro.upchain.wallet.repository.EthereumNetworkRepository;
import pro.upchain.wallet.repository.RepositoryFactory;
import pro.upchain.wallet.repository.TokenRepository;
import pro.upchain.wallet.utils.Hex;
import pro.upchain.wallet.utils.LogUtils;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */
public class CreateTransactionInteract {


    private final EthereumNetworkRepository networkRepository;


    public CreateTransactionInteract(
            EthereumNetworkRepository networkRepository) {
        this.networkRepository = networkRepository;

    }

    public Single<byte[]> sign(ETHWallet wallet, byte[] message, String pwd) {
        return getSignature(wallet, message, pwd);
    }

    // transfer ether
    public Single<String>  createEthTransaction(ETHWallet from,  String to, BigInteger amount, BigInteger gasPrice, BigInteger gasLimit, String password) {
        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> Single.fromCallable( () -> {
                    System.out.println("nonce   =  "+nonce);
                    Credentials credentials = WalletUtils.loadCredentials(password,  from.getKeystorePath());
                    RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, to, amount);
                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

                    String hexValue = Numeric.toHexString(signedMessage);
                    EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

                    return ethSendTransaction.getTransactionHash();

                } ).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    // transfer ERC20
    public Single<String>  createERC20Transfer(ETHWallet from,  String to, String contractAddress, BigInteger amount, BigInteger gasPrice, BigInteger gasLimit, String password) {
        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        String callFuncData = TokenRepository.createTokenTransferData(to, amount);

        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> Single.fromCallable( () -> {

                    Credentials credentials = WalletUtils.loadCredentials(password,  from.getKeystorePath());
                    RawTransaction rawTransaction = RawTransaction.createTransaction(
                            nonce, gasPrice, gasLimit, contractAddress, callFuncData);

                    LogUtils.d("rawTransaction:" + rawTransaction);

                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

                    String hexValue = Numeric.toHexString(signedMessage);
                    EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
                    return ethSendTransaction.getTransactionHash();

                } ).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    public Single<String> create(ETHWallet from, String to, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit,  String data, String pwd)
    {
        return createTransaction(from, to, subunitAmount, gasPrice, gasLimit, data, pwd)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Single<String> createContract(ETHWallet from, BigInteger gasPrice, BigInteger gasLimit, String data, String pwd) {
        return createTransaction(from, gasPrice, gasLimit, data, pwd)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Single<TransactionData> createWithSig(ETHWallet from, String to, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit, String data, String pwd)
    {
        return createTransactionWithSig(from, to, subunitAmount, gasPrice, gasLimit, data, pwd)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<TransactionData> createWithSig(ETHWallet from, BigInteger gasPrice, BigInteger gasLimit, String data, String pwd)
    {
        return createTransactionWithSig(from, gasPrice, gasLimit, data, pwd)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Single<TransactionData> createTransactionWithSig(ETHWallet from, String toAddress, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit, String data, String password) {

        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        TransactionData txData = new TransactionData();

        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> getRawTransaction(nonce, gasPrice, gasLimit,toAddress, subunitAmount,  data))
                .flatMap(rawTx -> signEncodeRawTransaction(rawTx, password, from, networkRepository.getDefaultNetwork().chainId))
                .flatMap(signedMessage -> Single.fromCallable( () -> {
                    txData.signature = Numeric.toHexString(signedMessage);
                    EthSendTransaction raw = web3j
                            .ethSendRawTransaction(Numeric.toHexString(signedMessage))
                            .send();
                    if (raw.hasError()) {
                        throw new Exception(raw.getError().getMessage());
                    }
                    txData.txHash = raw.getTransactionHash();
                    return txData;
                })).subscribeOn(Schedulers.io());
    }


    public Single<TransactionData> createTransactionWithSig(ETHWallet from, BigInteger gasPrice, BigInteger gasLimit, String data, String password) {

        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        TransactionData txData = new TransactionData();

        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> getRawTransaction(nonce, gasPrice, gasLimit, BigInteger.ZERO, data))
                .flatMap(rawTx -> signEncodeRawTransaction(rawTx, password, from, networkRepository.getDefaultNetwork().chainId))
                .flatMap(signedMessage -> Single.fromCallable( () -> {
                    txData.signature = Numeric.toHexString(signedMessage);
                    EthSendTransaction raw = web3j
                            .ethSendRawTransaction(Numeric.toHexString(signedMessage))
                            .send();
                    if (raw.hasError()) {
                        throw new Exception(raw.getError().getMessage());
                    }
                    txData.txHash = raw.getTransactionHash();
                    return txData;
                })).subscribeOn(Schedulers.io());
    }

    // https://github.com/web3j/web3j/issues/208
    // https://ethereum.stackexchange.com/questions/17708/solidity-ecrecover-and-web3j-sign-signmessage-are-not-compatible-is-it

    // message : TransactionEncoder.encode(rtx)   // may wrong

    public Single<byte[]> getSignature(ETHWallet wallet, byte[] message, String password) {
        return  Single.fromCallable(() -> {
            Credentials credentials = WalletUtils.loadCredentials(password, wallet.getKeystorePath());
            Sign.SignatureData signatureData = Sign.signMessage(
                    message, credentials.getEcKeyPair());

            List<RlpType> result = new ArrayList<>();
//            result.add(RlpString.create(message));

/*            if (signatureData != null) {
                result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getR())));
                result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getS())));
                result.add(RlpString.create(signatureData.getV()));
            }

            RlpList rlpList = new RlpList(result);
            return RlpEncoder.encode(rlpList);*/
            byte[] wrapBytes = new byte[65];
            if (signatureData != null) {
                byte[] dataR = signatureData.getR();
                byte[] dataS = signatureData.getS();
                byte dataV =  signatureData.getV();

                ;
                System.arraycopy(dataR, 0, wrapBytes, 0, dataR.length);
                System.arraycopy(dataS, 0, wrapBytes, 32, dataS.length);
                wrapBytes[64] = dataV;
            }
            return wrapBytes;
        });
    }

    public Single<String> createTransaction(ETHWallet from, String toAddress, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit, String data, String password) {
        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> getRawTransaction(nonce, gasPrice, gasLimit,toAddress, subunitAmount,  data))
                .flatMap(rawTx -> signEncodeRawTransaction(rawTx, password, from, networkRepository.getDefaultNetwork().chainId))
                .flatMap(signedMessage -> Single.fromCallable( () -> {
                    EthSendTransaction raw = web3j
                            .ethSendRawTransaction(Numeric.toHexString(signedMessage))
                            .send();
                    if (raw.hasError()) {
                        throw new Exception(raw.getError().getMessage());
                    }
                    return raw.getTransactionHash();
                })).subscribeOn(Schedulers.io());
    }


    // for DApp create contract transaction
    public Single<String> createTransaction(ETHWallet from, BigInteger gasPrice, BigInteger gasLimit, String data, String password) {

        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> getRawTransaction(nonce, gasPrice, gasLimit, BigInteger.ZERO, data))
                .flatMap(rawTx -> signEncodeRawTransaction(rawTx, password, from, networkRepository.getDefaultNetwork().chainId))
                .flatMap(signedMessage -> Single.fromCallable( () -> {
                    EthSendTransaction raw = web3j
                            .ethSendRawTransaction(Numeric.toHexString(signedMessage))
                            .send();
                    if (raw.hasError()) {
                        throw new Exception(raw.getError().getMessage());
                    }
                    return raw.getTransactionHash();
                })).subscribeOn(Schedulers.io());

    };


    // for DApp  create contract  transaction
    private Single<RawTransaction> getRawTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, BigInteger value, String data)
    {
        return Single.fromCallable(() ->
                RawTransaction.createContractTransaction(
                        nonce,
                        gasPrice,
                        gasLimit,
                        value,
                        data));
    }

    private Single<RawTransaction> getRawTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to , BigInteger value, String data)
    {
        return Single.fromCallable(() ->
                RawTransaction.createTransaction(
                        nonce,
                        gasPrice,
                        gasLimit,
                        to,
                        value,
                        data));
    }

    private  Single<byte[]> signEncodeRawTransaction(RawTransaction rtx, String password, ETHWallet wallet, int chainId) {

        return Single.fromCallable(() -> {
            Credentials credentials = WalletUtils.loadCredentials(password, wallet.getKeystorePath());
            byte[] signedMessage = TransactionEncoder.signMessage(rtx, credentials);
            return signedMessage;
        });
    }


    /**

     * erc20代币转账

     *

     * @param from 转账地址

     * @param to 收款地址

     * @param value 转账金额

     * @param privateKey 转账这私钥

     * @param contractAddress 代币合约地址

     * @return 交易哈希

     * @throws

     * @throws InterruptedException

     * @throw

     */

    public static String transferERC20Token(String from, String to, BigInteger value, String privateKey, String contractAddress) throws ExecutionException, InterruptedException, IOException {
        RepositoryFactory rf = MyApplication.repositoryFactory();


        Web3j web3j = Web3j.build(new HttpService(  rf.ethereumNetworkRepository.getDefaultNetwork().rpcServerUrl));

//加载转账所需的凭证，用私钥

        Credentials credentials = Credentials.create(privateKey);

//获取nonce，交易笔数

        BigInteger nonce;

        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send();

        if (ethGetTransactionCount == null) {

            return null;

        }

        nonce = ethGetTransactionCount.getTransactionCount();

//gasPrice和gasLimit 都可以手动设置

        BigInteger gasPrice;

        EthGasPrice ethGasPrice = null;
        try {
            ethGasPrice = web3j.ethGasPrice().sendAsync().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (ethGasPrice == null) {

            return null;

        }

        gasPrice = ethGasPrice.getGasPrice();
//        如果交易失败 很可能是手续费的设置问题
        BigInteger gasLimit = BigInteger.valueOf(30000L);

//        BigInteger gasLimit = BigInteger.valueOf(60000L);

//ERC20代币合约方法
        List<TypeReference<?>> returnTypes = Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
        });
        Function function = new Function(

                "transfer",

                Arrays.asList(new Address(to), new Uint256(value)),

                returnTypes);

//创建RawTransaction交易对象

        String encodedFunction = FunctionEncoder.encode(function);

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit,

                contractAddress, encodedFunction);

//签名Transaction

        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

        String hexValue = Numeric.toHexString(signMessage);

//发送交易

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();

        String hash = ethSendTransaction.getTransactionHash();

        if (hash != null) {

            return hash;

        }

        return null;

    }

}
