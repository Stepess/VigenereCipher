/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vigenerecipher;
import java.io.*;

public class VigenereCipher {
    
    static final String PATH = "D:\\\\Programming\\Crypt\\VigenereCipher\\text\\1.txt";
    static final String PATH_TO_CIPHER = "D:\\\\Programming\\Crypt\\VigenereCipher\\text\\ciphertext.txt";
    static final String PATH_TO_WRITE = "D:\\\\Programming\\Crypt\\VigenereCipher\\text\\plaintext.txt";
    static final char[] ALPHABET = {'а','б','в','г','д','е','ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х','ц','ч','ш','щ','ъ','ы','ь','э','ю','я'};
    
    public static int[] countLetter(char[] arr,int size){
        int[] count = new int[32];
        for(int i=0;i<32;i++)
            count[i]=0;
        for(int i=0;i<size;i++)
            count[(int)arr[i]-1072]++;
        return count;
    }
    
    public static int max(int[] arr){
        int max = arr[0];
        int res= 1072;
        for(int i=0;i<32;i++){
            if(arr[i]>=max){
                max = arr[i];
                res = i+1072;
            }       
        }
        return res;
    }
    
    public static float coincidenceIndex(String text){
        char[] arr = text.toCharArray();
        float res =0;
        final int size = text.length();
        int[] count = countLetter(arr,size);
        for(int i=0;i<32;i++)
            res = res + count[i]*(count[i]-1);
        res = res/(size*(size-1));
        return res;
    }
    
    public static String[] splitText(String text, int step){
        StringBuffer[] sb = new StringBuffer[step];
        for(int i=0;i<step;i++)
            sb[i] = new StringBuffer();
        String[] res = new String[step];
        char[] arr = text.toCharArray();
        for(int i=0; i<text.length();i++)
            sb[i%step].append(arr[i]);
        for(int i=0; i<step;i++)
            res[i] = sb[i].toString();
        return res;
    }
    
    public static float testOfKeySize(String text, int keySize){
        float res=0;
        String[] arrOfStr = splitText(text,keySize);
        float[] arrOfInd = new float[keySize];
        for(int i=0;i<keySize;i++){
             arrOfInd[i] = coincidenceIndex(arrOfStr[i]);
             res = res + arrOfInd[i]/keySize;
        }      
        return res;
    }
    
    public static String keySelection(String[] text, int step){
        int arr[][] = new int[step][32];
        int index =0;
        for(int i=0;i<step;i++)
            arr[i] = countLetter(text[i].toCharArray(),text[i].length());
        StringBuffer sb = new StringBuffer("громыкове");
        for(int i=9;i<step-3;i++){
            index = (max(arr[i])-(int)'о')%32;
            if (index<0)
                index = index+32;
            sb.append(ALPHABET[index]);
        }
        sb.append("ьма");
        return sb.toString();
        
    }
    
    public static String encrypt(String plaintext, String key){
        int periodOfKey = key.length();
        int sizeOfPT = plaintext.length();
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<sizeOfPT;i++)
            sb.append(ALPHABET[((int)plaintext.charAt(i) + (int)key.charAt(i%periodOfKey))%32]);
        return sb.toString();
    }
    
    public static String decrypt(String ciphertext, String key){
        int periodOfKey = key.length();
        int sizeOfCT = ciphertext.length();
        StringBuilder sb = new StringBuilder();
        int index;
        for(int i=0;i<sizeOfCT;i++){
            index = ((int)ciphertext.charAt(i) - (int)key.charAt(i%periodOfKey))%32;
            if (index<0)
                index +=32;
            sb.append(ALPHABET[index]);
        }  
        return sb.toString();
    }
    
    public static String readFile(String path) throws Exception{
        String text = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),"CP1251"));
        StringBuilder sb = new StringBuilder();
        String s;
        while((s=br.readLine()) != null)
            sb.append(s);
        text = sb.toString();
        sb.setLength(0);
        text = text.replaceAll("[^а-яА-Я]", "");
        text = text.toLowerCase();
        text = text.replaceAll("ё", "е");   
        return text;
    }
    
    public static void main(String[] args) throws Exception {
        String plaintext = "";
        String ciphertext = "";
        try{
            plaintext = readFile(PATH);  
            ciphertext = readFile(PATH_TO_CIPHER); 
        }
        catch(IOException ex){
            System.out.print(ex.getMessage());
        }
             
        String keys[] = {"хз","кот","ключ","ломик","секретноепослание"};
        String ciphers[] = new String[5];
        for(int i=0;i<5;i++)
            ciphers[i] = encrypt(plaintext,keys[i]);
        for(int i=0;i<5;i++)
            System.out.println(ciphers[i]);
        float cIndex[] = new float[6];
        cIndex[0] = coincidenceIndex(plaintext);
        for(int i=0;i<5;i++)
            cIndex[i+1] = coincidenceIndex(ciphers[i]);     
        for(int i=0;i<6;i++)
            System.out.printf("%f ", cIndex[i]);
        System.out.println();
        


        //decrypt var 4
        //System.out.println(ciphertext);
        //float cI = coincidenceIndex(ciphertext);
        //System.out.println(cI);
        //for(int i=2;i<20;i++)//13
            //System.out.println(i + " " +testOfKeySize(ciphertext,i));
        String[] splittedText = splitText(ciphertext,13);
        String key = keySelection(splittedText,13);//громыковедьма
        System.out.println(key);
        String text = decrypt(ciphertext,key);
        char[] arr = text.toCharArray();
        /*for(int i=0;i<200;i++){
            if (i %13==0)
                System.out.println();
            System.out.print(arr[i]);
         */
        //System.out.println(text);
        try {
            try (FileWriter fw = new FileWriter(PATH_TO_WRITE)) {
                fw.write(text);
                fw.close();
            }
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage()); //чтобы хоть что-то знать о возможной ошибке
        }  
    }
    
}
