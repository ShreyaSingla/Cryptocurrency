package DSCoinPackage;

import HelperClasses.*;

public class Moderator
 {

   public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
    
    int trc = DSObj.bChain.tr_count; // no of transation in a block
    int membercount = DSObj.memberlist.length; // members count

    Members Moderator = new Members();
    Moderator.UID = "Moderator";

    int coinstart = 100000;

    // tra is transaction array 
    // trc is no of transaction in a block 

    for (int i = 0; i < coinCount; i = i + trc) {
      Transaction[] tra = new Transaction[trc];
      for (int j = 0; j < trc; j++) {

        Transaction t = new Transaction();
        t.coinID = Integer.toString(coinstart + i + j);
        t.Source = Moderator;
        t.Destination = DSObj.memberlist[(i + j) % membercount];
        t.coinsrc_block = null;

        tra[j] = t;
      }

      TransactionBlock tB = new TransactionBlock(tra);
      DSObj.bChain.InsertBlock_Honest(tB);

      for (int j = 0; j < trc; j++) {
        Pair<String, TransactionBlock> p = new Pair<String, TransactionBlock>(tra[j].coinID, tB);
        tra[j].Destination.mycoins.add(p);
      }
    }
    
    DSObj.latestCoinID = Integer.toString(coinstart + coinCount - 1);
    return;
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {

    int trc = DSObj.bChain.tr_count; // no of transation is a block
    int membercount = DSObj.memberlist.length; // members

    Members Moderator = new Members();
    Moderator.UID = "Moderator";

    int coinstart = 100000;

    // tra is transaction array
    // trc is no of transaction in a block

    for (int i = 0; i < coinCount; i = i + trc) 
    {
      Transaction[] tra = new Transaction[trc];

      for (int j = 0; j < trc; j++) 
      {
        Transaction t = new Transaction();
        t.coinID = Integer.toString(coinstart + i + j);
        t.Source = Moderator;
        t.Destination = DSObj.memberlist[(i + j) % membercount];
        t.coinsrc_block = null;
        tra[j] = t;
      }

      TransactionBlock tB = new TransactionBlock(tra);
      DSObj.bChain.InsertBlock_Malicious(tB);

      for (int j = 0; j < trc; j++) 
      {
        Pair<String, TransactionBlock> p = new Pair<String, TransactionBlock>(tra[j].coinID, tB);
        tra[j].Destination.mycoins.add(p);
      }
      
    }

    DSObj.latestCoinID = Integer.toString(coinstart + coinCount - 1);
    return;
  }
}
