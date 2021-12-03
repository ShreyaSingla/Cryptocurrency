package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {
    this.trarray = t.clone();
    this.previous = null;
    this.dgst = null;
    MerkleTree mtree = new MerkleTree();
    this.trsummary = mtree.Build(t);
    this.Tree = mtree;
  }

  public boolean checkTransaction(Transaction t) {
    
    int count = 0;
    if (t.coinsrc_block == null)
      return true;
    
    // checking if the transaction occurs once in t.coinsrc_block
    
    for (int i = 0; i < t.coinsrc_block.trarray.length; i++) {
      if (t.coinsrc_block.trarray[i].coinID.equals(t.coinID)) {
        if (t.coinsrc_block.trarray[i].Destination.equals(t.Source))
          count++;
      }
    }
    if (count != 1)
      return false;

    // checking block between current and coin source block

    TransactionBlock temp = this;
    while (temp != t.coinsrc_block && temp!= null) {
      for (int i = 0; i < temp.trarray.length; i++) {
        if (t.coinID.equals(temp.trarray[i].coinID))
          return false;
      }
      temp = temp.previous;
    }

    // nothing seems wrong
    return true;
  }
}
