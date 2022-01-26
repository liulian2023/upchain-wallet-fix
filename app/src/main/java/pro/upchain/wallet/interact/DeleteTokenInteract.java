package pro.upchain.wallet.interact;



import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import pro.upchain.wallet.repository.TokenRepositoryType;

public class DeleteTokenInteract {
    private final TokenRepositoryType tokenRepository;
    private final FetchWalletInteract  findDefaultWalletInteract;

    public DeleteTokenInteract(
            FetchWalletInteract findDefaultWalletInteract, TokenRepositoryType tokenRepository) {
        this.findDefaultWalletInteract = findDefaultWalletInteract;
        this.tokenRepository = tokenRepository;
    }

    public Completable delete(String address, String symbol, int decimals,String imgUrl,String name) {
        return findDefaultWalletInteract
                .findDefault()
                .flatMapCompletable(wallet -> tokenRepository
                        .deleteToken(wallet.address, address, symbol, decimals,imgUrl,name)
                        .observeOn(AndroidSchedulers.mainThread()));
    }
}
