cd /Users/Emmett/blockchain
geth --datadir  testNet --dev --rpc --rpcaddr "0.0.0.0" console &
sleep 3;

sudo mongod & 
sleep 5;

cd /Users/Emmett/BCProject/trust-ray
node dist/Server.js &
