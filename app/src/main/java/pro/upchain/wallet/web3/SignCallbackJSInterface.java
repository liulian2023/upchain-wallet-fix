package pro.upchain.wallet.web3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.logging.Handler;

import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.utils.Hex;
import pro.upchain.wallet.utils.WalletDaoUtils;
import pro.upchain.wallet.utils.dapp2.DAppMethod;
import pro.upchain.wallet.web3.entity.Address;
import pro.upchain.wallet.web3.entity.Message;
import pro.upchain.wallet.web3.entity.TypedData;
import pro.upchain.wallet.web3.entity.Web3Transaction;


public class SignCallbackJSInterface {

    private final WebView webView;
    @NonNull
    private final OnRequestAccountListener onRequestAccountListener;
    @NonNull
    private final OnSignTransactionListener onSignTransactionListener;
    @NonNull
    private final OnSignMessageListener onSignMessageListener;
    @NonNull
    private final OnSignPersonalMessageListener onSignPersonalMessageListener;
    @NonNull
    private final OnSignTypedMessageListener onSignTypedMessageListener;


    public SignCallbackJSInterface(
            WebView webView,
            @NonNull  OnRequestAccountListener onRequestAccountListener,
            @NonNull OnSignTransactionListener onSignTransactionListener,
            @NonNull OnSignMessageListener onSignMessageListener,
            @NonNull OnSignPersonalMessageListener onSignPersonalMessageListener,
            @NonNull OnSignTypedMessageListener onSignTypedMessageListener) {
        this.webView = webView;
        this.onRequestAccountListener = onRequestAccountListener;
        this.onSignTransactionListener = onSignTransactionListener;
        this.onSignMessageListener = onSignMessageListener;
        this.onSignPersonalMessageListener = onSignPersonalMessageListener;
        this.onSignTypedMessageListener = onSignTypedMessageListener;
    }
    @JavascriptInterface
    public void postMessage(String json){
        JSONObject jsonObject = JSONObject.parseObject(json);
        String name = jsonObject.getString("name");
        Long id = jsonObject.getLong("id");
        DAppMethod dAppMethod = DAppMethod.fromValue(name);
        Context context = webView.getContext();
        switch (dAppMethod){
            case REQUESTACCOUNTS:
                webView.post(() -> onRequestAccountListener.onRequestAccount(getUrl(),id));
                break;
            case SIGNMESSAGE:
                webView.post(() -> onSignMessageListener.onSignMessage(new Message<>(getJsonData(jsonObject), getUrl(), id)));
                break;
            case SIGNPERSONALMESSAGE:
                webView.post(() -> onSignPersonalMessageListener.onSignPersonalMessage(new Message<>(getJsonData(jsonObject), getUrl(), id)));
                break;
            case SIGNTYPEDMESSAGE:
                webView.post(() -> onSignTypedMessageListener.onSignTypedMessage(new Message<>(getJsonData(jsonObject), getUrl(), id),getJsonRaw(jsonObject)));
                break;
            case SIGNTRANSACTION:
                JSONObject transactionObj = jsonObject.getJSONObject("object");
                if(transactionObj.containsKey("data")){
                    /**
                     * 授权 或者 合约转账
                     */
                    String data = transactionObj.getString("data");
                    boolean isApprove = data.startsWith("0x095ea7b3");
                    if(isApprove){
                        /**
                         *  授权
                         */
                        String to = transactionObj.getString("to");
                        String amountStr = data.substring(74);
                        BigInteger amount = new BigInteger(amountStr, 16);
                        Web3Transaction transaction = new Web3Transaction(
                                TextUtils.isEmpty(to) ? Address.EMPTY : new Address(to),
                                null,
                                amount,
                                Hex.hexToBigInteger("", BigInteger.ZERO),
                                Hex.hexToBigInteger("", BigInteger.ZERO),
                                Hex.hexToLong("", -1),
                                data,
                                id
                        ,"1");

                        webView.post(() -> onSignTransactionListener.onSignTransaction(transaction, getUrl()));
                    }else {
                        /**
                         * 转账
                         */
                        String to = transactionObj.getString("to");
                        Web3Transaction transaction = new Web3Transaction(
                                TextUtils.isEmpty(to) ? Address.EMPTY : new Address(to),
                                null,
                                BigInteger.ZERO,
                                Hex.hexToBigInteger("", BigInteger.ZERO),
                                Hex.hexToBigInteger("", BigInteger.ZERO),
                                Hex.hexToLong("", -1),
                                data,
                                id
                                ,"0");
                    }
                }else {


                }
                break;
            default:
                break;
        }
    }

    private String getJsonData(JSONObject jsonObject) {
        return jsonObject.getJSONObject("object").getString("data");
    }
    private String getJsonRaw(JSONObject jsonObject) {
        return jsonObject.getJSONObject("object").getString("raw");
    }

    private String getUrl() {
        return webView == null ? "" : webView.getUrl();
    }

    private static class TrustProviderTypedData {
        public String name;
        public String type;
        public Object value;
    }



}
