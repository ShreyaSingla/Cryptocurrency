package DSCoinPackage;
import HelperClasses.*;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;
  public int lbl = 0;
  // lbl = last block lenghth

  public static boolean checkTransactionBlock (TransactionBlock tB) {
   
    CRF c = new CRF(64);
    if (tB.dgst.substring(0, 4).equals("0000") == false)
      return false;

    if (tB.previous == null && tB.dgst.equals(c.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce)) == false)
      return false;
    if (tB.previous != null && tB.dgst.equals(c.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce)) == false)
      return false;

    MerkleTree m = new MerkleTree();
    m.Build(tB.trarray);

    if (tB.trsummary.equals(m.rootnode.val) == false)
      return false;

    int s = tB.trarray.length;
    for (int i = 0; i < s; i++) {
      if (tB.checkTransaction(tB.trarray[i]) == false)
        return false;
    }

    return true;
  }

  public TransactionBlock FindLongestValidChain () {
    int max = 0;
    TransactionBlock last = lastBlocksList[0];

    if (last == null)
      return null;

    for (int i = 0; lastBlocksList[i] != null; i++) {
      TransactionBlock temp = lastBlocksList[i]; 
      TransactionBlock templast = null;

      int t = 0; 

      while (temp != null) {
        if (checkTransactionBlock(temp)) 
        {
          if (t == 0)
            templast = temp;
          t++;
        } 
        else {
          if (t > max) {
            max = t;
            last = templast;
          }
          t = 0;
          templast = null;
        }
        temp = temp.previous;
      }

      if (t > max) {
        max = t;
        last = templast;
      }
    }
    return last;
  }

  public void InsertBlock_Malicious(TransactionBlock newBlock) {
    
    TransactionBlock lastBlock = FindLongestValidChain();

    int t = 1000000001;
    String s = "1000000001";
    String d = "10101010";
    CRF c = new CRF(64);
    if (lastBlock == null) 
    {
      while (d.substring(0, 4).compareTo("0000")!= 0) {
        s = Integer.toString(t);
        d = c.Fn(start_string + "#" + newBlock.trsummary + "#" + s);
        t++;
      }

      newBlock.nonce = s;
      newBlock.dgst = d;
      newBlock.previous = null;
    } 
    else 
    {

      while (d.substring(0, 4).compareTo("0000") != 0) {
        s = Integer.toString(t);
        d = c.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + s);
        t++;
      }

      newBlock.nonce = s;
      newBlock.dgst = d;
      newBlock.previous = lastBlock;
    }
   
    for (int k = 0; k < lbl; k++) 
    {
      if (lastBlocksList[k].equals(lastBlock)) 
      {
        lastBlocksList[k] = newBlock;
        return;
      }
    }
    lastBlock = newBlock;
    lastBlocksList[lbl++] = newBlock;
    return;
  }
}
