package guru.qa.niffler.api;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadSafeCookieJar implements CookieJar {
    private final Map<String, List<Cookie>> store = new ConcurrentHashMap<>();

    @Override
    public void saveFromResponse(HttpUrl httpUrl, @NotNull List<Cookie> list) {
        store.put(httpUrl.host(), list);
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookies = store.get(httpUrl.host());
        return cookies != null ? cookies : new ArrayList<>();
    }
}
