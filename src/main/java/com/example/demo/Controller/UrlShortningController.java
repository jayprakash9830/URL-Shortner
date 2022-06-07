package com.example.demo.Controller;

import com.example.demo.Model.Url;
import com.example.demo.Model.UrlDto;
import com.example.demo.Model.UrlErrorResponseDto;
import com.example.demo.Model.UrlResponseDto;
import com.example.demo.Service.URLService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
public class UrlShortningController {
    @Autowired
    private URLService urlService;
    @PostMapping("/genrate")
    public ResponseEntity<?> getrateShortLink(@RequestBody UrlDto urlDto){
        Url urlToRet=urlService.genrateShortLink(urlDto);
        if(urlToRet!=null) {
            UrlResponseDto urlResponseDto=new UrlResponseDto();
            urlResponseDto.setOriginalUrl(urlToRet.getOriginalUrl());
            urlResponseDto.setExpirationDate(urlToRet.getExpirationDate());
            urlResponseDto.setShortUrl(urlToRet.getShortLink());
            return new ResponseEntity<UrlResponseDto>(urlResponseDto, HttpStatus.OK);
        }
        UrlErrorResponseDto urlErrorResponseDto=new UrlErrorResponseDto();
        urlErrorResponseDto.setStatus("404");
        urlErrorResponseDto.setError("There was an error while processing your request. Please Try After Sometime");
        return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
    }

    @GetMapping("/{shortLink}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink,HttpServletResponse response) throws IOException {
        if(StringUtils.isEmpty(shortLink)){
            UrlErrorResponseDto urlErrorResponseDto=new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Invalid URL");
            urlErrorResponseDto.setStatus("404");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
        }
        Url urlToRet =urlService.getEncoderUrl(shortLink);
        if(urlToRet==null)
        {
            UrlErrorResponseDto urlErrorResponseDto=new UrlErrorResponseDto();
            urlErrorResponseDto.setError("URL does not exist or it might have expired");
            urlErrorResponseDto.setStatus("404");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
        }
        if(urlToRet.getExpirationDate().isBefore(LocalDateTime.now())){
            urlService.deleteShortLink(urlToRet);
            UrlErrorResponseDto urlErrorResponseDto=new UrlErrorResponseDto();
            urlErrorResponseDto.setError("URL expired please genrate new link");
            urlErrorResponseDto.setStatus("200");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
        }
        response.sendRedirect(urlToRet.getOriginalUrl());
        return null;
    }
}
