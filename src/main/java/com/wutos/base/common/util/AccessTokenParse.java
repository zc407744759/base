package com.wutos.base.common.util;

import com.alibaba.fastjson.JSONObject;
import com.wutos.base.common.handler.WutosException;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.*;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;

/**
 * @Author: ZouCong
 * @Date: 2018/7/25
 */
public class AccessTokenParse {
    public static JSONObject parse(String jwt,JSONObject publicKey){

        //来源：https://bitbucket.org/b_c/jose4j/wiki/JWS%20Examples
        try {
            //JSONObject jsonObject = URLConnectionUtil.sendGet("http://172.16.93.34:5002/.well-known/openid-configuration/jwks");
            //得到公钥
            String jsonWebKeySetJson = publicKey.toJSONString();

            String compactSerialization = jwt;

            JsonWebSignature jws = new JsonWebSignature();

            jws.setAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,   AlgorithmIdentifiers.RSA_USING_SHA256));

            jws.setCompactSerialization(compactSerialization);

            JsonWebKeySet jsonWebKeySet = new JsonWebKeySet(jsonWebKeySetJson);

            VerificationJwkSelector jwkSelector = new VerificationJwkSelector();
            JsonWebKey jwk = jwkSelector.select(jws, jsonWebKeySet.getJsonWebKeys());

            jws.setKey(jwk.getKey());
            boolean signatureVerified = jws.verifySignature();
            if(signatureVerified){
                String payload = jws.getPayload();
                return  JSONObject.parseObject(payload);
            }else {
                return null;
            }



        }catch (Exception e){
            e.printStackTrace();
            throw new WutosException("签名异常,请检查token是否正常!");

        }


    }
}
