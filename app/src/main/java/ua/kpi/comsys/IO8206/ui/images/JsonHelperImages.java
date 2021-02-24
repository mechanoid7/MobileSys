package ua.kpi.comsys.IO8206.ui.images;

import android.content.Context;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ua.kpi.comsys.IO8206.Film;

public class JsonHelperImages {
    private static String FILE_USER_NAME;
    private static Boolean userFileEnable = false;
    private static File f;

    public static void setFileUserName(String fileUserName) {
        FILE_USER_NAME = fileUserName;
    }

    public static boolean exportToJSON(Context context, List<List<String>> dataList) { // запись в файл

        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setSearch(dataList);
        String jsonString = gson.toJson(dataItems);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = context.openFileOutput(FILE_USER_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            userFileEnable = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static List<List<String>> importStringListFromJSON(Context context) {
        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;

        try{
            Gson gson = new Gson();

            f = new File(context.getFilesDir() + "/"+FILE_USER_NAME);
            if(f.exists()){ // файл найден
                userFileEnable = true;
            }
            else{// файл не найден
                try(FileWriter writer = new FileWriter(f)){
                    writer.write(getStringFromRawFile(context)); // запись в файл юзерспейса JSON`а
                    writer.flush();
                    userFileEnable = true;
                }
                catch(IOException ex){
                    ex.printStackTrace();
                }
            }

            DataItems dataItems = gson.fromJson(getStringFromRawFile(context), DataItems.class); // создание объектов из файла
            try {
                return dataItems.getSearch();
            } catch (Exception e){return new ArrayList<>();}
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return null;
    }

    private static class DataItems {
        private List<List<String>> Search;

        List<List<String>> getSearch() {
            return Search;
        }
        void setSearch(List<List<String>> search) {
            this.Search = search;
        }
    }

    public static String getStringFromRawFile(Context context) {
        InputStream is = null;

        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String myText = null;
        try {
            myText = convertStreamToString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  myText;
    }

    static String convertStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = is.read();
        while( i != -1)
        {
            baos.write(i);
            i = is.read();
        }
        return  baos.toString();
    }
}