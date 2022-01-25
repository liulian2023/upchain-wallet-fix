package pro.upchain.wallet.entity;

import java.util.List;

public class AddTokenEntity {

    private String jsonrpc;
    private List<ResultBean> result;
    private int id;


    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class ResultBean {
        private String address;
        private String symbol;
        private String name;
        private String unit;
        private String tokenType;
        private String methodId;
        private boolean available;
        private String totalSupply;
        private String logo;
        private String availableSupply;
        private boolean verified;
        private boolean audited;
        private String mode;
        private int rank;
        private int defaultGas;
        private int decimal;
        private int risk;
        private boolean transferable;
        private Object messageType;
        private Object message;
        private boolean associated;
        private String rawLogo;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public String getMethodId() {
            return methodId;
        }

        public void setMethodId(String methodId) {
            this.methodId = methodId;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public String getTotalSupply() {
            return totalSupply;
        }

        public void setTotalSupply(String totalSupply) {
            this.totalSupply = totalSupply;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getAvailableSupply() {
            return availableSupply;
        }

        public void setAvailableSupply(String availableSupply) {
            this.availableSupply = availableSupply;
        }

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }

        public boolean isAudited() {
            return audited;
        }

        public void setAudited(boolean audited) {
            this.audited = audited;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public int getDefaultGas() {
            return defaultGas;
        }

        public void setDefaultGas(int defaultGas) {
            this.defaultGas = defaultGas;
        }

        public int getDecimal() {
            return decimal;
        }

        public void setDecimal(int decimal) {
            this.decimal = decimal;
        }

        public int getRisk() {
            return risk;
        }

        public void setRisk(int risk) {
            this.risk = risk;
        }

        public boolean isTransferable() {
            return transferable;
        }

        public void setTransferable(boolean transferable) {
            this.transferable = transferable;
        }

        public Object getMessageType() {
            return messageType;
        }

        public void setMessageType(Object messageType) {
            this.messageType = messageType;
        }

        public Object getMessage() {
            return message;
        }

        public void setMessage(Object message) {
            this.message = message;
        }

        public boolean isAssociated() {
            return associated;
        }

        public void setAssociated(boolean associated) {
            this.associated = associated;
        }

        public String getRawLogo() {
            return rawLogo;
        }

        public void setRawLogo(String rawLogo) {
            this.rawLogo = rawLogo;
        }
    }
}
