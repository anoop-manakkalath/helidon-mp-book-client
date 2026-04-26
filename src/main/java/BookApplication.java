import org.eclipse.microprofile.auth.LoginConfig;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

import io.helidon.microprofile.cdi.Main;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import lombok.extern.slf4j.Slf4j;

@ApplicationPath("/api")
@LoginConfig(authMethod = "MP-JWT")
@OpenAPIDefinition(
    info = @Info(title = "Book API", version = "1.0.0"),
    security = @SecurityRequirement(name = "jwt") // Global requirement
)
@SecurityScheme(
    securitySchemeName = "jwt",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@Slf4j
class BookApplication extends Application {
	void main(String...args) {
	    try {
	        // Starts Helidon MP
	        Main.main(args);
	        log.info("[ OK ] Server is running. Access it at /api");
	    }
	    catch (Exception ex) {
	        log.error("Failed to start Book Service", ex);
	    }
	}
}
