package com.example.demo.Service;

import com.example.demo.Model.Url;
import com.example.demo.Model.UrlDto;
import org.springframework.stereotype.Service;

import java.net.URL;
@Service
public interface URLService {
    public Url genrateShortLink(UrlDto urlDto);
    public Url persistShortLink(Url url);
    public Url getEncoderUrl(String url);
    public void deleteShortLink(Url url);
}
