package pro.upchain.wallet.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import pro.upchain.wallet.interact.DeleteTokenInteract;
import pro.upchain.wallet.interact.FetchWalletInteract;


public class DeleteTokenViewModel extends BaseViewModel {

    private final DeleteTokenInteract deleteTokenInteract;
    private final FetchWalletInteract findDefaultWalletInteract;

    private final MutableLiveData<Boolean> result = new MutableLiveData<>();

    DeleteTokenViewModel(
            DeleteTokenInteract deleteTokenInteract,
            FetchWalletInteract findDefaultWalletInteract
            ) {
        this.deleteTokenInteract = deleteTokenInteract;
        this.findDefaultWalletInteract = findDefaultWalletInteract;
    }

    public void delete(String address, String symbol, int decimals,String imgUrl) {
        deleteTokenInteract
                .delete(address, symbol, decimals,imgUrl)
                .subscribe(this::onDelete, this::onError);
    }

    private void onDelete() {
        progress.postValue(false);
        result.postValue(true);
    }

    public LiveData<Boolean> result() {
        return result;
    }

    public void showTokens(Context context) {


    }
}
