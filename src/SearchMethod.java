import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/** 测试函数类 */
public class SearchMethod {
    //定义常量
    private static String NONCE = "a49eca32";
    private static String STREAM_CIPHER_KEY = "abcdefghijklmnop";
    private static String ENCRYPTION_KEY = "ponmlkjihgfedcba";
    private static String PLAINTEXT = "This is a testes";

    /** 处理关键词 */
    public static String optWord(String word){
        String str = word;
        //TODO 处理过长的单词
        for (int i = 0; i < 32 - word.length();i++){
            str = str + ".";
        }
        //System.out.println("str: " + str);
        byte[] wordByte = EncUtil.aesEncrypt(ENCRYPTION_KEY,str,PLAINTEXT);
        return HexUtil.byteToHex(wordByte);
    }
    /** 加密文件 */
    public static void encryptFile() throws IOException {
        //删除上次测试后加密文件
        FileUtil.deleteFiles("encry/");
        //获取所有明文文件
        File[] files = FileUtil.getFiles("plain/");
        for (File file : files)  {
            String filePath = file.toString();
            String fileName = file.getName();
            //获取文件词语
            String content = FileUtil.read(filePath, "UTF-8");
            List<String> words = FileUtil.getWords(content);
            System.out.println(words);
            //处理词语
            int count = 0;
            for(String word : words) {
                String counter = HexUtil.lengFill(24,count,"0");
                //计算E(Wi)
                String Ewi = optWord(word);
                //System.out.println("Ewi: " + Ewi);
                //计算流密码
                byte[] streamCipher = EncUtil.genStreamCipher(STREAM_CIPHER_KEY, NONCE, counter, PLAINTEXT);
                //计算Si、Fi(Si)，得Ti
                String Si = HexUtil.byteToHex(streamCipher);
                byte[] FiSibyte = EncUtil.aesEncrypt(ENCRYPTION_KEY,streamCipher,PLAINTEXT);
                String FiSi = HexUtil.byteToHex(FiSibyte);
                String Ti = Si + FiSi;
                //System.out.println("Ti: " + Ti);
                //异或计算E（Wi）、Ti得结果
                char[] result = HexUtil.XOR(Ewi,Ti);
                //将结果写入文件
                String writePath = "encry/"+fileName;
                FileUtil.write(new String(result),writePath,"UTF-8");
                count++;
            }
        }
    }
    /** 进行查找 */
    public static void searchFile(String keyword) throws IOException {
        boolean flag;
        //获取所有密文文件
        File[] files = FileUtil.getFiles("encry/");
        //处理关键词
        String cipherSearch = optWord(keyword);
        //在所有密文中查找
        for (File file : files) {
            flag = false;
            String filePath = file.toString();
            String fileName = file.getName();
            //获取加密内容
            String content = FileUtil.read(filePath, "UTF-8");
            String[] cipherWords = FileUtil.getCipherWords(content);
            //逐个匹配
            for(String cipherWord : cipherWords) {
                //加密时的逆过程得Ti
                char[] TiChar = HexUtil.XOR(cipherSearch,cipherWord);
                String TiStr = new String(TiChar);
                String Si = TiStr.substring(0,TiStr.length() / 2);
                String FiSi = TiStr.substring(TiStr.length() / 2);
                //处理Si、FiSi，进行匹配
                byte[] cipherSi = EncUtil.aesEncrypt(ENCRYPTION_KEY,HexUtil.hexToByte(Si),PLAINTEXT);
                String lowSi = HexUtil.byteToHex(cipherSi).toLowerCase();
                if(lowSi.equals(FiSi)){
                    flag = true;
                }
            }
            if(flag){
                System.out.println(keyword+" is found in "+fileName);
            }else{
                System.out.println(keyword+" don't find in "+fileName);
            }
        }
    }
    /** 测试主函数 */
    public static void main(String[] args) throws IOException {
        //加密文件
        encryptFile();
        //查找
        while(true) {
            System.out.print("input keyword to search: ");
            Scanner scanner = new Scanner(System.in);
            String str = scanner.nextLine();
            searchFile(str);
        }
    }
}
