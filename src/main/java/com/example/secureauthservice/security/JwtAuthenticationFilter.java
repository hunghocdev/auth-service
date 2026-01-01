package com.example.secureauthservice.security;

import com.auth0.jwt.interfaces.DecodedJWT;
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

/**
 * Filter xử lý JWT theo luồng:
 * JWT -> Verify -> Lấy Subject (username) -> Load User từ DB -> Lấy Role thực tế -> Set SecurityContext
 */
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

        // 1. Kiểm tra cấu trúc Header Authorization
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            // 2. Verify Token để đảm bảo tính toàn vẹn (Signature) và thời hạn (Expiration)
            DecodedJWT decodedJWT = jwtService.verifyAccessToken(jwt);
            String username = decodedJWT.getSubject();

            // 3. Nếu Token hợp lệ và chưa có thông tin xác thực trong Context hiện tại
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 4. LOAD USER TỪ DATABASE: Đảm bảo lấy được Roles mới nhất từ DB
                // Hàm loadUserByUsername sẽ gọi tới CustomUserDetailsService của bạn
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 5. Tạo đối tượng Authentication với đầy đủ danh sách quyền hạn (Authorities)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Thiết lập vào SecurityContext để các bước kiểm tra @PreAuthorize phía sau hoạt động
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Log để kiểm tra kết quả nạp quyền trong console IntelliJ
                System.out.println(">>> [FILTER SUCCESS] Authenticated User: " + username + " | Authorities: " + userDetails.getAuthorities());
            }
        } catch (Exception e) {
            // Nếu có lỗi (Token sai, hết hạn, hoặc User không tồn tại trong DB), log lỗi và tiếp tục chuỗi Filter
            System.err.println(">>> [FILTER ERROR] Authentication failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}