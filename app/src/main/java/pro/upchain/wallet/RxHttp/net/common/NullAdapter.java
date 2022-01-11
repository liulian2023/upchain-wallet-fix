package pro.upchain.wallet.RxHttp.net.common;


import android.util.JsonWriter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;

/**
 * created  by ganzhe on 2019/9/12.
 */
public class NullAdapter extends TypeAdapter<String> {



    @Override
    public void write(com.google.gson.stream.JsonWriter jsonWriter, String s) throws IOException {
        if (null == s) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(s);
    }

    @Override
    public String read(JsonReader in) throws IOException {
        if (JsonToken.NULL == in.peek()) {
            in.nextNull();
            return "";
        }
        return in.nextString();
    }
}
