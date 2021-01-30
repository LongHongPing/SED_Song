import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/** 加密工具类 */
public class EncUtil {
    /** 生成流加密 */
    public static byte[] genStreamCipher(String key,String NONCE,String counter,String target){
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(key.getBytes(),"AES"),new IvParameterSpec(HexUtil.hexToByte(NONCE+counter)));
            return cipher.doFinal(target.getBytes("UTF-8"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /** 加密string类型词语 */
    public static byte[] aesEncrypt(String key,String target,String iv){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(key.getBytes(),"AES"),new IvParameterSpec(iv.getBytes("UTF-8")));
            return cipher.doFinal(target.getBytes("UTF-8"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /** 加密byte类型词语 */
    public static byte[] aesEncrypt(String key,byte[] target,String iv){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(key.getBytes(),"AES"),new IvParameterSpec(iv.getBytes("UTF-8")));
            return cipher.doFinal(target);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
