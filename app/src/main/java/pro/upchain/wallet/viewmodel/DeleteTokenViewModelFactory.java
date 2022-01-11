package pro.upchain.wallet.viewmodel;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import androidx.annotation.NonNull;

import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.interact.DeleteTokenInteract;
import pro.upchain.wallet.interact.FetchWalletInteract;
import pro.upchain.wallet.repository.RepositoryFactory;


public class DeleteTokenViewModelFactory implements ViewModelProvider.Factory {

    private final DeleteTokenInteract addTokenInteract;
    private final FetchWalletInteract findDefaultWalletInteract;

    public DeleteTokenViewModelFactory() {
        RepositoryFactory rf = MyApplication.repositoryFactory();

        this.findDefaultWalletInteract = new FetchWalletInteract();
        this.addTokenInteract = new DeleteTokenInteract(findDefaultWalletInteract, rf.tokenRepository);;

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DeleteTokenViewModel(addTokenInteract, findDefaultWalletInteract);
    }
}
