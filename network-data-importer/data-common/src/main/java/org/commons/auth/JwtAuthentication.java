package org.commons.auth;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.*;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.responsecode.ResponseCode;
import org.commons.util.Constants;
import org.commons.util.PropertiesCache;
//import io.jsonwebtoken.impl.Base64Codec;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class JwtAuthentication {

    private PropertiesCache propertiesCache = PropertiesCache.getInstance();
    //  secret Key encoded to BASE64 "TVRJek5EVTJOemc9"
    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;



    public String createUserToken(String issuer, String subject) {

//        String secretKeyDecoded = Base64Codec.BASE64.decodeToString(secretKey);
//        System.out.println("Decoded value is " + secretKeyDecoded);



        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(propertiesCache.getProperty("jwt_secret_key"));
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        long time = System.currentTimeMillis();
        long expirationTime = Long.parseLong(propertiesCache.getProperty("jwt_expiration_time"));
        JwtBuilder builder = Jwts.builder().setSubject("adam")
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(new Date(time))
                .setExpiration(new Date(time+(expirationTime*1000)))
                .signWith(signatureAlgorithm, signingKey);

        return builder.compact();
    }

//    private void printStructure(String token) {
//        Jws parseClaimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//
//        System.out.println("Header     : " + parseClaimsJws.getHeader());
//        System.out.println("Body       : " + parseClaimsJws.getBody());
//        System.out.println("Signature  : " + parseClaimsJws.getSignature());
//    }


    //        System.out.println("Issuer     : " + body.getIssuer());
//        System.out.println("IssuedAt   : " +body.getIssuedAt());
//        System.out.println("Subject    : " + body.getSubject());
//        System.out.println("Expiration : " + body.getExpiration());

    public void verifyUserToken(String token) throws ProjectCommonException {

        try {
            try {
                Claims body = Jwts.parser().setSigningKey(propertiesCache.getProperty("jwt_secret_key")).parseClaimsJws(token).getBody();
                if (!body.getIssuer().equals(propertiesCache.getProperty("jwt_issuer")) || !body.getSubject().equals(propertiesCache.getProperty("jwt_subject")))
                    throw new ProjectCommonException(ResponseCode.invalidTokenCredentials,Constants.USER_TOKEN);
            } catch (ExpiredJwtException e) {
                throw new ProjectCommonException(ResponseCode.expiredTokenError,Constants.USER_TOKEN);
            } catch (MalformedJwtException | UnsupportedJwtException | SignatureException e) {
                throw new ProjectCommonException(ResponseCode.invalidTokenCredentials,Constants.USER_TOKEN);
            } catch (Exception e) {
                throw new ProjectCommonException(ResponseCode.unAuthorized);
            }
        }
        catch (ProjectCommonException e) {
            ProjectLogger.log(" : ",e, LoggerEnum.ERROR.name());
            throw e;
        }
    }

}
