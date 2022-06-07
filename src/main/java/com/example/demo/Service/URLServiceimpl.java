package com.example.demo.Service;

import com.example.demo.Model.Url;
import com.example.demo.Model.UrlDto;
import com.example.demo.Repository.UrlRepo;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
@Component
public class URLServiceimpl implements URLService{
    @Autowired
    private UrlRepo urlRepo;
    @Override
    public Url genrateShortLink(UrlDto urlDto) {
        if(StringUtils.isNotEmpty(urlDto.getUrl())){
            String encodedUrl=encodeUrl(urlDto.getUrl());
            Url urlToPersist=new Url();
            urlToPersist.setCreationDate(LocalDateTime.now());
            urlToPersist.setOriginalUrl(urlDto.getUrl());
            urlToPersist.setShortLink(encodedUrl);
            urlToPersist.setExpirationDate(getExpirationDate(urlDto.getExpirationDate(),urlToPersist.getCreationDate()));
            Url urlToRet=persistShortLink(urlToPersist);
            if(urlToRet!=null)
                return urlToRet;
        }
        return null;
    }

    private LocalDateTime getExpirationDate(String expirationDate, LocalDateTime creatationDate) {
        if(StringUtils.isBlank(expirationDate)) {
            return creatationDate.plusSeconds(120);
        }
        LocalDateTime expirationDatert=LocalDateTime.parse(expirationDate);
        return expirationDatert;
    }

    private String encodeUrl(String url) {
        String encodeurl="";
        LocalDateTime time=LocalDateTime.now();
        encodeurl= Hashing.murmur3_32().hashString(url.concat(time.toString()),
                StandardCharsets.UTF_8).toString();
        return encodeurl;
    }

    @Override
    public Url persistShortLink(Url url) {
        Url urlToRet=urlRepo.save(url);
        return urlToRet;
    }

    @Override
    public Url getEncoderUrl(String url) {
        Url urlToRet=urlRepo.findByShortLink(url);
        return urlToRet;
    }

    @Override
    public void deleteShortLink(Url url) {
        urlRepo.delete(url);
    }
}
