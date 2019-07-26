package org.commons.auth;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.*;
import org.commons.exception.ProjectCommonException;
import org.commons.logger.LoggerEnum;
import org.commons.logger.ProjectLogger;
import org.commons.response.Response;
//import io.jsonwebtoken.impl.Base64Codec;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class JwtAuthentication {

    private long expirationTime = 1000 * 60 * 60 * 24 * 1; // 1 days
//  secret Key encoded to BASE64 "TVRJek5EVTJOemc9"
    private String secretKey = "MTIzNDU2Nzg=";
    private String issuer = "NIIT";
    private String subject = "network-visualizer";
    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

//    public void testJWT() {
//        String token = generateJwtToken();
//        System.out.println(token);
//        printStructure(token);
////        printBody(token);
//    }

    public String createUserToken(String issuer, String subject) {

//        String secretKeyDecoded = Base64Codec.BASE64.decodeToString(secretKey);
//        System.out.println("Decoded value is " + secretKeyDecoded);



        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        long time = System.currentTimeMillis();
        JwtBuilder builder = Jwts.builder().setSubject("adam")
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(new Date(time))
                .setExpiration(new Date(time+expirationTime))
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
                Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
                if (!body.getIssuer().equals(issuer) || !body.getSubject().equals(subject))
                    throw new ProjectCommonException(403, "Forbidden", "Please provide a valid 'user-token'");
            } catch (ExpiredJwtException e) {
                throw new ProjectCommonException(403, "Forbidden", "User Token expired. Please create and provide a new 'user-token'.");
            } catch (MalformedJwtException | UnsupportedJwtException | SignatureException e) {
                throw new ProjectCommonException(403, "Forbidden", "Please provide a valid 'user-token'");
            } catch (Exception e) {
                throw new ProjectCommonException(401, "Unauthorised", "You are not Authorised");
            }
        }
        catch (ProjectCommonException e) {
            ProjectLogger.log(" : ",e, LoggerEnum.ERROR.name());
            throw e;
        }
    }

}
