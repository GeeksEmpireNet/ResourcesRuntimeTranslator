package test.geekstools.rrt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RRT {

    private Context publicContext;
    private Field[] publicFields;
    private String publicPackageName;
    private String[] allStringsNames;
    private int ID;
    private String rawRes, targetLanguage;

    public RRT(Context context, String packageName, Field[] fields) {
        publicContext = context;
        publicFields = fields;
        publicPackageName = packageName;

        targetLanguage = Locale.getDefault().getLanguage();
    }

    //Functions***********************************/
    public void doTranslateOperation(){
        TranslateOperation translateOperation = new TranslateOperation();
        translateOperation.execute();
    }
    public String setupStringResources(String resourcesName, String defaultResourcesValue){
        Field[] fields = R.string.class.getFields();
        System.out.println("KEY >> " + resourcesName + " || DEFAULT >> " + defaultResourcesValue);
        SharedPreferences rawResPref = publicContext.getSharedPreferences("resources", Context.MODE_PRIVATE);
        String value = rawResPref.getString(resourcesName, defaultResourcesValue);     //rawResPref.getString(prefKey, defaultValue);

        return  value;
    }
    private class TranslateOperation extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "Res Null";

            Field[] fields = publicFields;      /*R.string.class.getFields();*/
            allStringsNames = new String[fields.length];    System.out.println("Resource.Length >> " + fields.length);
            for (int  i = 0; i < fields.length; i++) {
                allStringsNames[i] = fields[i].getName();

                System.out.println("STRINGS NAME >> " + allStringsNames[i]);
                ID = publicContext.getResources().getIdentifier(allStringsNames[i], "string", publicContext.getPackageName());
                rawRes = publicContext.getResources().getString(ID);    System.out.println("STRINGS VALUE >> " + rawRes);

                String space = "\\s";
                Pattern p = Pattern.compile(space);
                Matcher m = p.matcher(rawRes);
                if(m.find()){
                    rawRes = rawRes.replaceAll(space, "+");     System.out.println(rawRes);
                }

                try {
                    DefaultHttpClient client = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet("http://m4jid2k.info/translate/translator.php?text="
                            + rawRes
                            /*+ "&from="+ "en" */
                            + "&to=" + targetLanguage);


                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                    int r = execute.getStatusLine().getStatusCode();
                    response = response + " "  + "*" + r + "*";
                    System.out.println("RESPONSE >> " + r);
                }
                catch (Exception e) {System.out.println("ERROR: " + e);}
                finally {
                    String Raw = response.substring(response.lastIndexOf(">") + 1);

                    String codeRegex= "\\*\\d{3}\\*";
                    Pattern P = Pattern.compile(codeRegex);
                    Matcher M = P.matcher(Raw);
                    if(M.find()){
                        String Code = M.group();
                        ResponseCodeHandler(Code);
                        Raw = Raw.replaceAll(codeRegex, "");
                    }
                    System.out.println("unescapeJavaString(Raw);   " + unescapeJavaString(Raw));


                    SharedPreferences rawResPref = publicContext.getSharedPreferences("resources", Context.MODE_APPEND);
                    SharedPreferences.Editor edit = rawResPref.edit();
                    edit.putString(allStringsNames[i], unescapeJavaString(Raw));
                    edit.apply();
                }
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("PostExecute Result >> " + result);
        }
    }
    private void ResponseCodeHandler(String R){
        R = R.replaceAll("\\*", "");
        int I = Integer.parseInt(R);

        if(I >= 300 && I <= 310){
            System.out.println(I);
            Toast.makeText(publicContext, "Connection Redirected: " + I, Toast.LENGTH_SHORT).show();
        }
        else if(I >= 400 && I <= 499){
            System.out.println(I);
            Toast.makeText(publicContext, "Client Error: " + I, Toast.LENGTH_SHORT).show();
        }
        else if(I >= 500 && I <= 599){
            System.out.println(I);
            Toast.makeText(publicContext, "Server Error: " + I, Toast.LENGTH_SHORT).show();

            if(I == 503){
                Toast.makeText(publicContext, "Server is in Enhancement Process", Toast.LENGTH_SHORT).show();
            }
            else if(I == 504){
                Toast.makeText(publicContext, "Request Time Out", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String unescapeJavaString(String st) {
        StringBuilder sb = new StringBuilder(st.length());
        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st.charAt(i + 1);
                // Octal escape?
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                            && st.charAt(i + 1) <= '7') {
                        code += st.charAt(i + 1);
                        i++;
                        if ((i < st.length() - 1) && st.charAt(i + 1) >= '0' && st.charAt(i + 1) <= '7') {
                            code += st.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextChar) {
                    case '\\':
                        ch = '\\';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
                    // Hex Unicode: u????
                    case 'u':
                        if (i >= st.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt(
                                "" + st.charAt(i + 2) + st.charAt(i + 3)
                                        + st.charAt(i + 4) + st.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                }
                i++;
            }
            sb.append(ch);
        }
        //   System.out.println(sb.toString());
        return sb.toString();
    }
}
