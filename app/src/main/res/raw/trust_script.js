(function() {
    var config = {
        chainId: __CHAINID__,
        rpcUrl: "__RPCURL__",
        isDebug: true
    };
    window.ethereum = new trustwallet.Provider(config);
    window.web3 = new trustwallet.Web3(window.ethereum);
    trustwallet.postMessage = (json) => {
        window._tw_.postMessage(JSON.stringify(json))
    };
})();