package pro.upchain.wallet.web3;

import pro.upchain.wallet.web3.entity.Web3Transaction;

public interface OnRequestAccountListener {
        void onRequestAccount( String url,Long id);
}
