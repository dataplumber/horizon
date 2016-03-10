import java.security.MessageDigest

class DigestUtility {
   private DigestUtility() {
   }
   
   public static String getDigest(String algorithm, String text) {
      MessageDigest digest = MessageDigest.getInstance(algorithm)
      byte[] buf = digest.digest(text.bytes)
      StringBuffer sb = new StringBuffer("")
      String hex = "0123456789abcdef"
      byte value
      for (i in buf) {
         sb.append(hex.charAt((i & 0xf0)>>4))
         sb.append(hex.charAt(i & 0x0f))
      }
      
      return sb.toString()
   }
}
