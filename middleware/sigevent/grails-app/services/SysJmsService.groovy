class SysJmsService {

   static exposes = ['jms']
   static pubSub = true

   boolean transactional = false

   def publish(String topic, String message) {
      log.debug("publishing to "+topic)
      
      //sendPubSubJMSMessage(topic, message)
   }
}
