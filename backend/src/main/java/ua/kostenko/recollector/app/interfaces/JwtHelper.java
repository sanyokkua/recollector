package ua.kostenko.recollector.app.interfaces;

import io.jsonwebtoken.Claims;
import ua.kostenko.recollector.app.dto.auth.TokensDto;

import java.util.Date;

public interface JwtHelper {

    String generateMainJwt(String username, Date timeNow);
    String generateRefreshJwt(String username, Date timeNow);
    TokensDto generateJwtTokensPair(String username);

    boolean validateMainJwtToken(String token, String username);
    boolean validateRefreshJwtToken(String token, String username);

    Claims extractClaimsFromMainJwtToken(String token);
    Claims extractClaimsFromRefreshJwtToken(String token);
}
