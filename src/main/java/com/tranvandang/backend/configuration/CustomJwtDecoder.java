package com.tranvandang.backend.configuration;

// Import các class và thư viện cần thiết


import com.nimbusds.jose.JOSEException;
import com.tranvandang.backend.dto.request.IntrospectRequest;
import com.tranvandang.backend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

// Đánh dấu class này là một Spring Component để Spring quản lý dependency injection
@Component
public class CustomJwtDecoder implements JwtDecoder {

    // Lấy giá trị của `jwt.signerKey` từ file cấu hình (application.yml hoặc application.properties)
    @Value("${jwt.signerKey}")
    private String signerKey;

    // Inject service AuthenticationService để gọi API introspection
    @Autowired
    private AuthenticationService authenticationService;

    // Đối tượng NimbusJwtDecoder dùng để giải mã JWT, sẽ được khởi tạo khi cần
    private NimbusJwtDecoder nimbusJwtDecoder = null;

    // Override phương thức decode() từ interface JwtDecoder để thực hiện giải mã JWT
    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            // Gọi service introspect để kiểm tra token có hợp lệ không
            var response = authenticationService.introspect(
                    IntrospectRequest.builder().token(token).build());

            // Nếu token không hợp lệ, ném ra ngoại lệ JwtException
            if (!response.isValid()) throw new JwtException("Token invalid");
        } catch (JOSEException | ParseException e) {
            // Nếu có lỗi xảy ra trong quá trình kiểm tra, ném ra JwtException với thông báo lỗi
            throw new JwtException(e.getMessage());
        }

        // Nếu nimbusJwtDecoder chưa được khởi tạo, thì khởi tạo nó với khóa bí mật
        if (Objects.isNull(nimbusJwtDecoder)) {
            // Tạo SecretKeySpec từ khóa bí mật (dùng thuật toán HS512)
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");

            // Khởi tạo NimbusJwtDecoder với khóa bí mật và thuật toán HS512
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        // Giải mã và trả về đối tượng JWT
        return nimbusJwtDecoder.decode(token);
    }
}
