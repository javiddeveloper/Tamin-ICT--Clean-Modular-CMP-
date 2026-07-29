package com.tamin.taminhamrah.utils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.acra.ACRA;
import org.acra.ktx.ExtensionsKt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import androidx.annotation.NonNull;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;
import timber.log.Timber;

public class GsonFactory extends Converter.Factory {
    public static GsonFactory create() {
        return create(new Gson());
    }

    public static GsonFactory create(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new GsonFactory(gson);
    }

    private final Gson gson;

    private GsonFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NonNull Type type,
                                                            @NonNull Annotation[] annotations,
                                                            @NonNull Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonResponseBodyConverter<>(adapter);
    }

    static class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final TypeAdapter<T> adapter;

        GsonResponseBodyConverter(TypeAdapter<T> adapter) {
            this.adapter = adapter;
        }

        private String readString(Reader reader) throws IOException {
            StringBuilder builder = new StringBuilder();
            BufferedReader buffer = new BufferedReader(reader);
            String line;
            while ((line = buffer.readLine()) != null) {
                builder.append(line);
            }
            buffer.close();
            return builder.toString();
        }

        @Override
        public T convert(ResponseBody value) throws IOException {

            String jSon = "We have Error";
            long contentLength = value.contentLength();
            try {

                jSon = readString(value.charStream());
                JsonReader jsonReader = new JsonReader(new StringReader(jSon));
                jsonReader.setLenient(true);
                T result = adapter.read(jsonReader);
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    Exception exception = new JsonIOException("JSON document was not fully consumed.");
                    ACRA.getErrorReporter().putCustomData("HAVE_EXTRA", jSon);
                    ExtensionsKt.sendSilentlyWithAcra(exception);
                    Timber.e(exception);
                    throw exception;
                }
                return result;
            } catch (IOException e) {
                Timber.e(e, "IOException %s", jSon);
                ACRA.getErrorReporter().putCustomData("REMOVED_ITEMS", jSon);
                ACRA.getErrorReporter().handleSilentException(e);
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(
            @NonNull Type type,
            @NonNull Annotation[] parameterAnnotations,
            @NonNull Annotation[] methodAnnotations,
            @NonNull Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter);
    }

    static class GsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");
        private static final Charset UTF_8 = StandardCharsets.UTF_8;

        private final Gson gson;
        private final TypeAdapter<T> adapter;

        GsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public RequestBody convert(@NonNull T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, value);
            jsonWriter.close();
            return RequestBody.create(buffer.readByteString(), MEDIA_TYPE);
        }
    }
}

