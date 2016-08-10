

//import net.unto.twitter.Api
//import net.unto.twitter.TwitterProtos.Status
//import net.unto.twitter.TwitterProtos.RateLimitStatus

/**
 * SysTwitterService
 */
class SysTwitterService {
   def grailsApplication
   private final String TwitterUserName = grailsApplication.config.sigevent_twitter_username
   private final String TwitterPassword = grailsApplication.config.sigevent_twitter_password
   boolean transactional = false

   public void publish(String message) {
      /*
      log.debug("SysTwitterService.publish()")
      log.debug("\ttwitter username: "+SysTwitterService.TwitterUserName)
      log.debug("\ttwitter password: "+SysTwitterService.TwitterPassword)
      
      def api = Api.builder().username(SysTwitterService.TwitterUserName).password(SysTwitterService.TwitterPassword).build()
      
      def rateLimitStatus = api.rateLimitStatus().build().get()
      log.debug("API limits: "+rateLimitStatus.getRemainingHits())
      
      log.debug("Tweeting...")
      def status = api.updateStatus(message).build().post()
      log.debug("Status="+status.getText())
      
      rateLimitStatus = api.rateLimitStatus().build().get()
      log.debug("API limits: "+rateLimitStatus.getRemainingHits())
      
      api.endSession().build().post()
      log.debug("Ended session")
      */
   }
}
