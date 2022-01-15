
package pro.upchain.wallet.utils.dapp;
import android.app.Dialog
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.afollestad.materialdialogs.MaterialDialog
import org.json.JSONObject
import org.w3c.dom.Text
import pro.upchain.wallet.utils.WalletDaoUtils

class WebAppInterface(
    private val context: Context,
    private val webView: WebView,
    private val dappUrl: String

) {
/*    private val privateKey =
        PrivateKey("0x4646464646464646464646464646464646464646464646464646464646464646".toHexByteArray())
    private val addr = CoinType.ETHEREUM.deriveAddress(privateKey).toLowerCase()*/
private val privateKey ="";
private val addr = WalletDaoUtils.getCurrent().address;
    @JavascriptInterface
    fun postMessage(json: String) {
        val obj = JSONObject(json)
        println(obj)
        val id = obj.getLong("id")
        val method = DAppMethod.fromValue(obj.getString("name"))
        when (method) {
            DAppMethod.REQUESTACCOUNTS -> {
                MaterialDialog(context) .show {
                    title(text = "Request Accounts")
                    message (text = "${dappUrl} requests your address")
                    positiveButton(text = "ok") { dialog ->
                        val setAddress = "window.ethereum.setAddress(\"$addr\");window.ethereum.selectedAddress = window.ethereum.address"
                        val callback = "window.ethereum.sendResponse($id, [\"$addr\"])"
                        webView.post {
                            webView.evaluateJavascript(setAddress) {
                                // ignore

                            }
                            webView.evaluateJavascript(callback) { value ->
                                println(value)
                            }
                        }
                    }
                    negativeButton(text = "cancel") { dialog ->
                        // Do something
                    }


                }
            }
            DAppMethod.SIGNMESSAGE -> {
                val data = extractMessage(obj)

                handleSignMessage(id, data, addPrefix = false)
            }
            DAppMethod.SIGNPERSONALMESSAGE -> {
                val data = extractMessage(obj)
                handleSignMessage(id, data, addPrefix = true)
            }
            DAppMethod.SIGNTYPEDMESSAGE -> {
                val data = extractMessage(obj)
                val raw = extractRaw(obj)
                handleSignTypedMessage(id, data, raw)
            }
            else -> {

                MaterialDialog(context).show {
                    title( text = "Error")
                    message (text = "$method not implemented")
                        positiveButton { null }
                        negativeButton { null }
                }
            }
        }
    }

    private fun extractMessage(json: JSONObject): ByteArray {
        val param = json.getJSONObject("object")
        val data = param.getString("data")
        return Numeric.hexStringToByteArray(data)
    }

    private fun extractRaw(json: JSONObject): String {
        val param = json.getJSONObject("object")
        return param.getString("raw")
    }

    private fun handleSignMessage(id: Long, data: ByteArray, addPrefix: Boolean) {
        MaterialDialog(context).show {
            title(text = "Sign Message")
            message (text = if (addPrefix) String(data, Charsets.UTF_8) else Numeric.toHexString(data))
            positiveButton(text = "OK"){dialog ->
                webView.sendResult(signEthereumMessage(data, addPrefix), id)
            }
            negativeButton (text = "Cancel"){dialog ->
                webView.sendError("Cancel", id)
            }
        }
    }

    private fun handleSignTypedMessage(id: Long, data: ByteArray, raw: String) {


        MaterialDialog(context).show {
            title(text = "Sign Typed Message")
            message (text = raw)
            positiveButton(text = "OK"){dialog ->
                webView.sendResult(signEthereumMessage(data, false), id)
            }
            negativeButton (text = "Cancel"){dialog ->
                webView.sendError("Cancel", id)
            }
        }
    }

    private fun signEthereumMessage(message: ByteArray, addPrefix: Boolean): String {
        var data = message
        if (addPrefix) {
            val messagePrefix = "\u0019Ethereum Signed Message:\n"
            val prefix = (messagePrefix + message.size).toByteArray()
            val result = ByteArray(prefix.size + message.size)
            System.arraycopy(prefix, 0, result, 0, prefix.size)
            System.arraycopy(message, 0, result, prefix.size, message.size)
//            data = wallet.core.jni.Hash.keccak256(result)
        }
/*
        val signatureData = privateKey.sign(data, Curve.SECP256K1)
            .apply {
                (this[this.size - 1]) = (this[this.size - 1] + 27).toByte()
            }
        return Numeric.toHexString(signatureData)*/
        return "";
    }
}
