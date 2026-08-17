package com.energie.platform.security;
import jakarta.servlet.*; import jakarta.servlet.http.*; import org.springframework.beans.factory.annotation.Value; import org.springframework.http.MediaType; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter; import java.io.IOException;
@Component public class ApiKeyFilter extends OncePerRequestFilter {private final String key;public ApiKeyFilter(@Value("${app.ai.api-key}") String key){this.key=key;}
 protected boolean shouldNotFilter(HttpServletRequest r){return !r.getRequestURI().startsWith("/api/ai/");}
 protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{if(!key.equals(req.getHeader("X-API-KEY"))){res.setStatus(401);res.setContentType(MediaType.APPLICATION_JSON_VALUE);res.getWriter().write("{\"message\":\"Clé API IA invalide\"}");return;}chain.doFilter(req,res);}}
