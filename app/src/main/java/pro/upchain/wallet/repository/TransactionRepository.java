package pro.upchain.wallet.repository;


import android.text.TextUtils;

import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.math.BigInteger;

import io.reactivex.Single;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.entity.Transaction;
import pro.upchain.wallet.service.BlockExplorerClientType;
import pro.upchain.wallet.utils.LogUtils;

import io.reactivex.Maybe;
import io.reactivex.Observable;

public class TransactionRepository implements TransactionRepositoryType {

    private final EthereumNetworkRepository networkRepository;
    private final TransactionLocalSource transactionLocalSource;
    private final BlockExplorerClientType blockExplorerClient;

    public TransactionRepository(
            EthereumNetworkRepository networkRepository,
            TransactionLocalSource inMemoryCache,
            TransactionLocalSource inDiskCache,
            BlockExplorerClientType blockExplorerClient) {
        this.networkRepository = networkRepository;
        this.blockExplorerClient = blockExplorerClient;
        this.transactionLocalSource = inMemoryCache;

        this.networkRepository.addOnChangeDefaultNetwork(this::onNetworkChanged);
    }

    @Override
    public Observable<Transaction[]> fetchTransaction(String walletAddr, String tokenAddr) {
        return Observable.create(e -> {
            Transaction[] transactions;
            if (TextUtils.isEmpty(tokenAddr)) {
                transactions = transactionLocalSource.fetchTransaction(walletAddr).blockingGet();
            } else {
                transactions = transactionLocalSource.fetchTransaction(walletAddr, tokenAddr).blockingGet();
            }

            if (transactions != null && transactions.length > 0) {
                e.onNext(transactions);
            }
            transactions = blockExplorerClient.fetchTransactions(walletAddr, tokenAddr).blockingFirst();
            EasyHttp.get("/api?module=account&action=tokentx&startblock=0&endblock=99999999&page=1&offset=1000&sort=asc")
                    .baseUrl("https://api-ropsten.etherscan.io")

                    .params("address",walletAddr)
                    .params("contractaddress",tokenAddr)
                    .params("apikey","RV14FRD3SB3HPMKS57PPX1D15C8DCP7A7R")
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onError(ApiException e) {
                            System.out.println(e);
                        }

                        @Override
                        public void onSuccess(String s) {

                            System.out.println("交易記錄:    "+s);                            System.out.println(s);
                        }
                    });
            EasyHttp.getInstance().setCertificates();
            transactionLocalSource.clear();
            if (TextUtils.isEmpty(tokenAddr)) {
                transactionLocalSource.putTransactions(walletAddr, transactions);
            } else {
                transactionLocalSource.putTransactions(walletAddr, tokenAddr, transactions);
            }
            e.onNext(transactions);
            e.onComplete();
        });
    }

    @Override
    public Maybe<Transaction> findTransaction(String walletAddr, String transactionHash) {
        return fetchTransaction(walletAddr, null)
                .firstElement()
                .flatMap(transactions -> {
                    for (Transaction transaction : transactions) {
                        if (transaction.hash.equals(transactionHash)) {
                            return Maybe.just(transaction);
                        }
                    }
                    return null;
                });
    }

    private void onNetworkChanged(NetworkInfo networkInfo) {
        transactionLocalSource.clear();
    }


    @Override
    public Single<String> createTransaction(ETHWallet from, BigInteger gasPrice, BigInteger gasLimit, String data, String password) {
        return null;
    }

    @Override
    public Single<String> createTransaction(ETHWallet from, String toAddress, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit, byte[] data, String password) {
        return null;
    }
}
