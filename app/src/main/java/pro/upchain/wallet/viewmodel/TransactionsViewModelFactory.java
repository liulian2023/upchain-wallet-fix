package pro.upchain.wallet.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.interact.FetchTransactionsInteract;
import pro.upchain.wallet.interact.FetchWalletInteract;
import pro.upchain.wallet.repository.EthereumNetworkRepository;
import pro.upchain.wallet.repository.RepositoryFactory;


public class TransactionsViewModelFactory implements ViewModelProvider.Factory {

    private final EthereumNetworkRepository ethereumNetworkRepository;
    private final FetchWalletInteract findDefaultWalletInteract;
    private final FetchTransactionsInteract fetchTransactionsInteract;


    public TransactionsViewModelFactory() {

        RepositoryFactory rf = MyApplication.repositoryFactory();
        this.ethereumNetworkRepository = rf.ethereumNetworkRepository;
        this.findDefaultWalletInteract = new FetchWalletInteract();
        this.fetchTransactionsInteract = new FetchTransactionsInteract(rf.transactionRepository);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TransactionsViewModel(
                ethereumNetworkRepository,
                findDefaultWalletInteract,
                fetchTransactionsInteract);
    }
}
