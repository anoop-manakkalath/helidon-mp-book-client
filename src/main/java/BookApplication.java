import io.helidon.microprofile.cdi.Main;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import lombok.extern.slf4j.Slf4j;

@ApplicationPath("/api")
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
