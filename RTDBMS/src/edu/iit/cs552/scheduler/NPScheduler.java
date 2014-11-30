package edu.iit.cs552.scheduler;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;

import edu.iit.cs552.entity.Transaction;
import edu.iit.cs552.utility.Constants;

public class NPScheduler extends TransactionScheduler implements
		Comparator<Transaction> {

	Logger log = Logger.getLogger(NPScheduler.class);

	PriorityQueue<Transaction> pq = null;
	int miss = 0;
	int hit = 0;
	int total = 0;

	@Override
	public int compare(Transaction t1, Transaction t2) {
		// TODO Auto-generated method stub
		long diff = t1.getStart() - t2.getStart();
		return (int) diff;

	}

	public NPScheduler(List<String> columns, String table) {
		super(columns, table);
		pq = new PriorityQueue<Transaction>(Constants.CAPACITY, this);
		new Thread(this).start();
	}

	public void queueTransaction(Transaction transaction) {
		transaction.schedule();
		pq.offer(transaction);
	}

	public void run() {
		while (!(stop && pq.isEmpty())) {
			Transaction transaction = pq.poll();
			if (transaction != null) {
				total++;
				if (processTransaction(transaction, log)) {
					hit++;
				} else {
					miss++;
				}
			}
		}
		log.info("Hit:[" + hit + "]Miss[" + miss + "]Total[" + total + "]");
	}

}