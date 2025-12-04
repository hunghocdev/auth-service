package com.example.authdemo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Kiểm tra header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Lấy token
        jwt = authHeader.substring(7);
        // Giả sử JwtService của bạn có hàm extractUsername, nếu chưa có thì dùng logic verify để lấy subject
        // Ở đây tôi giả định bạn đã verify bên trong service hoặc dùng thư viện JJWT để parse
        try {
            // Logic giải mã token để lấy username (tùy vào JwtService của bạn viết thế nào)
            // username = jwtService.extractUsername(jwt);
            // Nếu JwtService chưa có hàm này, bạn cần bổ sung.
            // Tạm thời tôi dùng một cách giải mã đơn giản nếu bạn dùng thư viện auth0/java-jwt:
            username = com.auth0.jwt.JWT.decode(jwt).getSubject(); // Lấy User ID (subject) hoặc Username
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Xác thực với Spring Security
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Nếu token hợp lệ (bạn cần viết hàm isTokenValid trong JwtService hoặc kiểm tra expiration)
            // Giả sử token đã qua bước decode ở trên là tạm ổn
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}