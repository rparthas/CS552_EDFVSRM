package edu.iit.cs552.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.iit.cs552.entity.Job;
import edu.iit.cs552.entity.Task;
import edu.iit.cs552.utility.UtilityFunctions;

public class Dispatcher {

	Logger logger = Logger.getLogger(Dispatcher.class);

	public void dispatchTasks(List<Task> tasks, String algorithm) {
		logger.info("--------------Start of Dispatcher---------");
		List<Integer> periods = new ArrayList<Integer>();
		List<String> stats = new ArrayList<String>();
		double utilization = 0.0;

		for (Task task : tasks) {
			periods.add(task.period);
			utilization = ((double)task.executionTime / (double)task.period) + utilization;
		}

		int hyperPeriod = UtilityFunctions.computeLCM(periods);
		stats.add("Scheduler running for a hyperperiod of[" + hyperPeriod + "]");
		stats.add("Utilization Percent of the taskset is ["
				+ (utilization * 100) + "]");

		Map<Long, List<Job>> jobMap = new HashMap<Long, List<Job>>();
		for (Task task : tasks) {
			int noOfinstances = hyperPeriod / task.period;
			if (hyperPeriod % task.period != 0)
				noOfinstances++;
			for (int i = 0; i < noOfinstances; i++) {
				Job job = null;
				if (i == 0) {
					job = new Job(0, task.period * 1, task.period,
							task.executionTime, 1, task.name);
				} else {
					int j = i + 1;
					job = new Job(task.period * i, task.period * j,
							task.period, task.executionTime, j, task.name);

				}
				List<Job> jobs = jobMap.get(job.arrivalTime);
				if (jobs == null) {
					jobs = new ArrayList<Job>();
				}
				jobs.add(job);
				jobMap.put(job.arrivalTime, jobs);

			}
		}

		Scheduler scheduler = null;
		switch (algorithm) {
		case "EDF":
			scheduler = new EDFScheduler();
			break;
		default:
			scheduler = new RMScheduler();
			break;
		}

		stats.addAll(scheduler.schedule(hyperPeriod, jobMap));
		logger.fatal("--------------Stats of the taskset begins---------");
		for (String stat : stats) {
			logger.fatal(stat);
		}
		logger.fatal("--------------Stats of the taskset ends---------");
		logger.info("--------------End of Dispatcher---------");

	}
}
