package DSCoinPackage;

import java.util.*;

import HelperClasses.*;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;
  public int in_process_trans_length;
  
  public Members() {
    in_process_trans_length = 0;
    in_process_trans = new Transaction[101];
  }

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    Transaction tobj = new Transaction();
    if (mycoins.get(0) == null)
      return;

    tobj.coinID = mycoins.get(0).first;
    tobj.coinsrc_block = mycoins.get(0).second;
    tobj.Source = this;

    // to find the Destination member

    for (int i = 0; i < DSobj.memberlist.length; i++) {
      if (DSobj.memberlist[i].UID.equals(destUID)) 
      {
        tobj.Destination = DSobj.memberlist[i];
        break;
      }
    }
    tobj.next = null;
    mycoins.remove(0);
    in_process_trans[in_process_trans_length++] = tobj;
    DSobj.pendingTransactions.AddTransactions(tobj);
    return;
  }

  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
    Transaction tobj = new Transaction();
    if (mycoins.get(0) == null)
      return;

    tobj.coinID = mycoins.get(0).first;
    tobj.coinsrc_block = mycoins.get(0).second;
    tobj.Source = this;

    // to find the Destination member

    for (int i = 0; i < DSobj.memberlist.length; i++) {
      if (DSobj.memberlist[i].UID == destUID) {
        tobj.Destination = DSobj.memberlist[i];
        break;
      }
    }

    tobj.next = null;
    mycoins.remove(0);

    in_process_trans[in_process_trans_length++] = tobj;

    DSobj.pendingTransactions.AddTransactions(tobj);
    return;
  }

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
   
    TransactionBlock bl = DSObj.bChain.lastBlock;
    TransactionBlock tB = null;
    boolean find = false;

    while (find == false)
    {
      for (int i = 0; i < bl.trarray.length; i++) 
      {
        if (bl.trarray[i].equals(tobj) == true) 
        {
          tB = bl;
          find = true;
          break;
        }
      }

      if (find)
        break;

      if (bl.previous == null && find == false)
        throw new MissingTransactionException();

      if (bl.previous != null)
        bl = bl.previous;

    }
    
    int index = 0;

    int l = tB.trarray.length;
    
    // finding index of the needed transaction from the transaction block
    for (int i = 0; i < l; i++)
    {
      if (tB.trarray[i] == tobj) {
        index = i;
        break;
      }
    }
    
    // moving tr to the node of given transaction 

    TreeNode tr = new TreeNode();
    tr = tB.Tree.rootnode;

    while(l > 1) {
      if (index < l / 2)
        tr = tr.left;
      else
        tr = tr.right;
      l = l / 2;
      index = index % l;
    }

    // adding elements to the first array

    ArrayList<Pair<String, String>> alist = new ArrayList<Pair<String, String>>();

    while (tr != tB.Tree.rootnode) 
    {
      if (tr.parent.left == tr) {
        Pair<String, String> s = new Pair<String, String>(tr.val, tr.parent.right.val);
        alist.add(s);
      } else {
        Pair<String, String> s = new Pair<String, String>(tr.parent.left.val, tr.val);
        alist.add(s);
      }

      tr = tr.parent;
    }
  
    alist.add(new Pair<String, String>(tB.Tree.rootnode.val, null));
    
    // brlist is the reverse of blist in which the elements are added
    ArrayList<Pair<String, String>> brlist = new ArrayList<Pair<String, String>>();
    
    TransactionBlock temp =  DSObj.bChain.lastBlock;

    while (temp != tB)
    {
      Pair<String, String> s = new Pair<String, String>(temp.dgst,
          temp.previous.dgst + "#" + temp.trsummary + "#" + temp.nonce);
      brlist.add(s);
      temp = temp.previous;
    }

    // adding the given transaction block 

    Pair<String, String> s = new Pair<String, String>(temp.dgst,
        temp.previous.dgst + "#" + temp.trsummary + "#" + temp.nonce);
    brlist.add(s);
    
    Pair<String, String> st = new Pair<String, String>(tB.previous.dgst, null);
    brlist.add(st);

    // reversing 
    // blist is the actual list
    ArrayList<Pair<String, String>> blist = new ArrayList<Pair<String, String>>();
    
    for (int i = brlist.size()- 1; i >=0 ; i--)
    {
      blist.add(brlist.get(i));
    }
    
    //checked
    index = 0;
    for (int i = 0; i < in_process_trans_length; i++)
    {
      if (in_process_trans[i].equals(tobj)) 
      {
        if (in_process_trans_length == 1 || in_process_trans_length - 1 == i) 
        {
          in_process_trans[in_process_trans_length - 1] = null;
          break;
        }
        for (int j = i; j < in_process_trans_length - 1; j++) {
          in_process_trans[i] = in_process_trans[i + 1];
        }
        in_process_trans[in_process_trans_length - 1] = null;
        break;
      }
    }
    
    in_process_trans_length--;
    Members m = new Members();
    m = tobj.Destination;

    for (int i = 0; i < m.mycoins.size(); i++)
    {
      if (m.mycoins.get(i).first.compareTo(tobj.coinID) > 0) {
        Pair<String, TransactionBlock> p = new Pair<String, TransactionBlock>(tobj.coinID, tB);
        m.mycoins.add(i, p);
        break;
      }
    }

    Pair<List<Pair<String, String>>, List<Pair<String, String>>> finallist = new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(
        alist, blist);
    return finallist;
  }

  public void MineCoin(DSCoin_Honest DSObj) 
  {
    Transaction[] trarray = new Transaction[DSObj.bChain.tr_count];
    int count = 0;
    while (count < DSObj.bChain.tr_count - 1 && DSObj.pendingTransactions.firstTransaction != null) 
    {
      try 
      {
        Transaction t = DSObj.pendingTransactions.RemoveTransaction();
        boolean found = false;
        for (int i = 0; i < count && !found; i++)
          if (trarray[i].coinID.equals(t.coinID))
            found = true;

        if (!found && DSObj.bChain.lastBlock.checkTransaction(t))
          trarray[count++] = t;
      } catch (Exception e) {
      }
    }
  
    Transaction minerRewardTransaction = new Transaction();
    String newCoinID = Long.toString(Long.valueOf(DSObj.latestCoinID) + 1);
    
    //updating the objects latest coin 
    DSObj.latestCoinID = newCoinID;

    minerRewardTransaction.coinID = newCoinID;
    minerRewardTransaction.Source = null;
    minerRewardTransaction.Destination = this;
    minerRewardTransaction.coinsrc_block = null;

    trarray[count++] = minerRewardTransaction;
    TransactionBlock tB = new TransactionBlock(trarray);
    DSObj.bChain.InsertBlock_Honest(tB);
    mycoins.add(new Pair<String, TransactionBlock>(newCoinID, tB));
    return;
  }  

  public void MineCoin(DSCoin_Malicious DSObj) {
    Transaction[] trarray = new Transaction[DSObj.bChain.tr_count];
    int count = 0;
    while (count < DSObj.bChain.tr_count - 1 && DSObj.pendingTransactions.firstTransaction != null) {
      try {
        Transaction t = DSObj.pendingTransactions.RemoveTransaction();
        boolean found = false;
        for (int i = 0; i < count && !found; i++)
          if (trarray[i].coinID.equals(t.coinID))
            found = true;

        TransactionBlock tb = DSObj.bChain.FindLongestValidChain();
        if (!found)
        {
          if (tb == null)
            trarray[count++] = t;
          else if (tb.checkTransaction(t))
            trarray[count++] = t;
        }
      } catch (Exception e) {
      }
    }

    Transaction minerRewardTransaction = new Transaction();
    String newCoinID = Integer.toString(Integer.valueOf(DSObj.latestCoinID) + 1);

    // updating the objects latest coin
    DSObj.latestCoinID = newCoinID;

    minerRewardTransaction.coinID = newCoinID;
    minerRewardTransaction.Source = null;
    minerRewardTransaction.Destination = this;
    minerRewardTransaction.coinsrc_block = null;

    trarray[count++] = minerRewardTransaction;
    TransactionBlock tB = new TransactionBlock(trarray);
    DSObj.bChain.InsertBlock_Malicious(tB);
    mycoins.add(new Pair<String, TransactionBlock>(newCoinID, tB));
    return;
  }   
}
