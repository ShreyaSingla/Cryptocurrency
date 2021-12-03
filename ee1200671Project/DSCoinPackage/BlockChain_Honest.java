package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count = 0;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock = null;

  public void InsertBlock_Honest(TransactionBlock newBlock) {
    TransactionBlock temp = newBlock;
    CRF c = new CRF(64);
    int t = 1000000001;
    String s = "1000000001";
    String d = "11616161616";

    if (lastBlock == null) {
      while (d.substring(0, 4).compareTo("0000") != 0) {
        s = Integer.toString(t);
        d = c.Fn(start_string + "#" + temp.trsummary + "#" + s);
        t++;
      }

      temp.nonce = s;
      temp.dgst = d;
      temp.previous = null;
      lastBlock = temp;
    } else {

      while (d.substring(0, 4).compareTo("0000") != 0) {
        s = Integer.toString(t);
        d = c.Fn(lastBlock.dgst + "#" + temp.trsummary + "#" + s);
        t++;
      }

      temp.nonce = s;
      temp.dgst = d;
      temp.previous = lastBlock;
      lastBlock = temp;
    }
    return;
  }
  
}
