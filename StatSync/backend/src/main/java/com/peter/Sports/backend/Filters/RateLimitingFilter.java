package com.peter.Sports.backend.Filters;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter implements Filter  {

    private static final int MAX_REQUESTS_PER_MINUTE = 175;

    private final Map<String,Deque<Long>> requestsLog = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) 
        throws IOException, ServletException{
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;

            String clientIpAddress = httpServletRequest.getRemoteAddr();
            long timeOfRequest = System.currentTimeMillis();

            requestsLog.putIfAbsent(clientIpAddress, new ArrayDeque<>());
            Deque<Long> timeStamps = requestsLog.get(clientIpAddress);
            
            synchronized(timeStamps){
                while(!timeStamps.isEmpty() && timeOfRequest - timeStamps.peekFirst() > 60_000){
                    timeStamps.pollFirst();
                }

                if(timeStamps.size() >= MAX_REQUESTS_PER_MINUTE){
                    httpServletResponse.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
                    httpServletResponse.getWriter().write("Too many requests. Please try again later.");
                    return;
                }

                timeStamps.addLast(timeOfRequest);
            }
        filterChain.doFilter(request,response);
    }

}
