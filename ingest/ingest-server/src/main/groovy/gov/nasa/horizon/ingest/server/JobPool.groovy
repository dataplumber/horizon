package gov.nasa.horizon.ingest.server

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * Simple threaded job pool implementation
 */
class JobPool {
   static Log log = LogFactory.getLog(JobPool.class)

   static Set jobs = new HashSet()
   int max
   def pool

   def JobPool(int max) {
      this.max = max
      pool = Executors.newFixedThreadPool(max)
   }

   def work = {job, yield ->
      log.trace("Job ${job.name} start.")
      yield.call()
      log.trace("Job ${job.name} cleanup.")
      synchronized (JobPool.jobs) {
         log.trace (JobPool.jobs.toString())
         def jobRef = JobPool.jobs.find {
            it.name == job.name
         }
         if (!jobRef) {
            log.error ("Job ${job.name} not found in service queue.")
         } else {
            if (!JobPool.jobs.remove(jobRef)) {
               log.trace ("Failed to remove job ${job.name}.")
            }
            log.trace("Job ${job.name} has been removed from service queue.")
         }
         if (log.traceEnabled) {
            log.trace ("Current ${JobPool.jobs.size()} active job(s) -")
            JobPool.jobs.each {
               log.trace ("${it.name}:${it.received}")
            }
         }
      }
      log.trace("Job ${job.name} done.")
   }

   def execute(String name, Closure yield) {
      synchronized (JobPool.jobs) {
         if (JobPool.jobs.size() < max) {
            def job = [name: name, received: new Date()]
            JobPool.jobs.add(job)
            pool.submit({work(job, yield)} as Callable)
            log.trace("Job ${name} is now in progress.")
            return true
         }
      }
      return false
   }

   synchronized Map getJob(String name) {
      Map result = JobPool.jobs.find {
         it.name == name
      }
      if (result)
         return result.asImmutable()
      return result
   }

   synchronized List getJobs() {
      return JobPool.jobs.asList()
   }

   synchronized int getJobCount() {
      return JobPool.jobs.size()
   }

   synchronized def shutdown() {
      pool.shutdown()
   }
}
