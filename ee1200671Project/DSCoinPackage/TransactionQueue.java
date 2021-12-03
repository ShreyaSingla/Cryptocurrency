package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction = null;
  public Transaction lastTransaction = null;
  public int numTransactions = 0;

  public void AddTransactions (Transaction transaction) {
    Transaction t = new Transaction();
    t = transaction;
    if (firstTransaction == null) {

      t.previous = null;
      t.next = null;

      firstTransaction = t;
      lastTransaction = t;
    } 
    else {
      lastTransaction.next = t;
      t.previous = lastTransaction;
      lastTransaction = t;
    }
    numTransactions ++;
    return;
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    if (numTransactions == 0) {
      throw new EmptyQueueException();
    }

    Transaction t = new Transaction();
    t = firstTransaction;

    if (numTransactions == 1) {
      firstTransaction = null;
      lastTransaction = null;
    } else {
      firstTransaction = firstTransaction.next;
      firstTransaction.previous = null;
    }
    numTransactions--;
    t.next = null;
    return t;
  }

  public int size() {
    return numTransactions;
  }
}
